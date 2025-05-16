package at.fhtw.swkom.paperless.services.services;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.DocumentCommandService;
import at.fhtw.swkom.paperless.services.DocumentQueryService;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.exception.StorageException;
import at.fhtw.swkom.paperless.services.mappers.DocumentMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentServiceImplTests {

    private DocumentRepository documentRepository;
    private DocumentMapper documentMapper;

    private DocumentCommandService commandService;
    private DocumentQueryService queryService;

    @BeforeEach
    void setUp() {
        documentRepository = mock(DocumentRepository.class);
        documentMapper = mock(DocumentMapper.class);

        commandService = new DocumentCommandService(documentRepository, documentMapper);
        queryService = new DocumentQueryService(documentRepository, documentMapper);
    }

    @Test
    void testStoreSavesDocument() {
        Document document = new Document();
        document.setTitle("Test Document");

        when(documentRepository.save(document)).thenReturn(document);

        Document savedDocument = commandService.store(document);

        assertNotNull(savedDocument);
        assertEquals("Test Document", savedDocument.getTitle());
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void testStoreThrowsExceptionForNullDocument() {
        StorageException exception = assertThrows(StorageException.class, () -> commandService.store(null));
        assertEquals("No documentEntity found!", exception.getMessage());
    }

    @Test
    void testLoadAllReturnsAllDocuments() {
        Document doc1 = new Document();
        doc1.setId(1);
        doc1.setTitle("Doc 1");

        Document doc2 = new Document();
        doc2.setId(2);
        doc2.setTitle("Doc 2");

        DocumentDTO dto1 = new DocumentDTO(1, "Doc 1", LocalDateTime.now(), null);
        DocumentDTO dto2 = new DocumentDTO(2, "Doc 2", LocalDateTime.now(), null);

        when(documentRepository.findAll()).thenReturn(Arrays.asList(doc1, doc2));
        when(documentMapper.entityToDto(doc1)).thenReturn(dto1);
        when(documentMapper.entityToDto(doc2)).thenReturn(dto2);

        List<DocumentDTO> result = queryService.loadAll();

        assertEquals(2, result.size());
        verify(documentRepository).findAll();
        verify(documentMapper, times(2)).entityToDto(any(Document.class));
    }

    @Test
    void testLoadThrowsExceptionIfDocumentNotFound() {
        when(documentRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> queryService.load(1));
    }

    @Test
    void testLoadReturnsDocument() {
        Document doc = new Document();
        doc.setId(1);
        doc.setTitle("Doc");

        DocumentDTO dto = new DocumentDTO(1, "Doc", LocalDateTime.now(), null);

        when(documentRepository.findById(1)).thenReturn(Optional.of(doc));
        when(documentMapper.entityToDto(doc)).thenReturn(dto);

        DocumentDTO result = queryService.load(1);

        assertEquals("Doc", result.getTitle());
        verify(documentRepository).findById(1);
    }

    @Test
    void testDeleteDeletesDocument() {
        when(documentRepository.existsById(1)).thenReturn(true);

        commandService.delete(1);

        verify(documentRepository).deleteById(1);
    }

    @Test
    void testDeleteThrowsExceptionForInvalidId() {
        when(documentRepository.existsById(1)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commandService.delete(1));
        assertEquals("Document with ID 1 not found for deletion", exception.getMessage());
    }

    @Test
    void testUpdateUpdatesDocument() {
        Document existing = new Document();
        existing.setId(1);
        existing.setTitle("Old Title");

        DocumentDTO dto = new DocumentDTO(1, "New Title", LocalDateTime.now(), null);

        Document mapped = new Document();
        mapped.setId(1);
        mapped.setTitle("New Title");

        when(documentRepository.findById(1)).thenReturn(Optional.of(existing));
        when(documentMapper.dtoToEntity(dto)).thenReturn(mapped);

        commandService.update(1, dto);

        verify(documentRepository).save(mapped);
    }

    @Test
    void testUpdateThrowsExceptionForInvalidId() {
        when(documentRepository.findById(1)).thenReturn(Optional.empty());

        DocumentDTO dto = new DocumentDTO(1, "New Title", LocalDateTime.now(), null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> commandService.update(1, dto));
        assertEquals("Document with ID 1 not found for update", exception.getMessage());
    }
}

