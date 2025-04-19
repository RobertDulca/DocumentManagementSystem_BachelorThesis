package at.fhtw.swkom.paperless.services.services;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.DocumentServiceImpl;
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
    private DocumentServiceImpl documentService;

    @BeforeEach
    void setUp() {
        documentRepository = mock(DocumentRepository.class);
        documentMapper = mock(DocumentMapper.class);
        documentService = new DocumentServiceImpl(documentRepository, documentMapper);
    }

    @Test
    void testStoreSavesDocument() {
        // Arrange
        Document document = new Document();
        document.setTitle("Test Document");

        when(documentRepository.save(document)).thenReturn(document);

        // Act
        Document savedDocument = documentService.store(document);

        // Assert
        assertNotNull(savedDocument);
        assertEquals("Test Document", savedDocument.getTitle());
        verify(documentRepository, times(1)).save(document);
    }

    @Test
    void testStoreThrowsExceptionForNullDocument() {
        // Act & Assert
        StorageException exception = assertThrows(StorageException.class, () -> documentService.store(null));
        assertEquals("No documentEntity found!", exception.getMessage());
    }

    @Test
    void testLoadAllReturnsAllDocuments() {
        // Arrange
        Document document1 = new Document();
        document1.setId(1);
        document1.setTitle("Document 1");

        Document document2 = new Document();
        document2.setId(2);
        document2.setTitle("Document 2");

        DocumentDTO documentDTO1 = new DocumentDTO(1, "Document 1", LocalDateTime.now(), null);
        DocumentDTO documentDTO2 = new DocumentDTO(2, "Document 2", LocalDateTime.now(), null);

        when(documentRepository.findAll()).thenReturn(Arrays.asList(document1, document2));
        when(documentMapper.entityToDto(document1)).thenReturn(documentDTO1);
        when(documentMapper.entityToDto(document2)).thenReturn(documentDTO2);

        // Act
        List<DocumentDTO> documents = documentService.loadAll();

        // Assert
        assertEquals(2, documents.size());
        verify(documentRepository, times(1)).findAll();
        verify(documentMapper, times(2)).entityToDto(any(Document.class));
    }

    @Test
    void testDeleteDeletesDocumentForValidId() {
        // Arrange
        when(documentRepository.existsById(1)).thenReturn(true);

        // Act
        documentService.delete(1);

        // Assert
        verify(documentRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteThrowsExceptionForInvalidId() {
        // Arrange
        when(documentRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> documentService.delete(1));
        assertEquals("Document with ID 1 not found for deletion", exception.getMessage());
    }

    @Test
    void testUpdateUpdatesDocument() {
        // Arrange
        Document existingDocument = new Document();
        existingDocument.setId(1);
        existingDocument.setTitle("Old Title");

        DocumentDTO updatedDTO = new DocumentDTO(1, "New Title", LocalDateTime.now(), null);

        Document updatedDocument = new Document();
        updatedDocument.setId(1);
        updatedDocument.setTitle("New Title");

        when(documentRepository.findById(1)).thenReturn(Optional.of(existingDocument));
        when(documentMapper.dtoToEntity(updatedDTO)).thenReturn(updatedDocument);

        // Act
        documentService.update(1, updatedDTO);

        // Assert
        verify(documentRepository, times(1)).save(updatedDocument);
    }

    @Test
    void testUpdateThrowsExceptionForInvalidId() {
        // Arrange
        when(documentRepository.findById(1)).thenReturn(Optional.empty());

        DocumentDTO updatedDTO = new DocumentDTO(1, "New Title", LocalDateTime.now(), null);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> documentService.update(1, updatedDTO));
        assertEquals("Document with ID 1 not found for update", exception.getMessage());
    }
}

