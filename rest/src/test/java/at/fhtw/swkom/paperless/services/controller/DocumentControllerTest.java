package at.fhtw.swkom.paperless.services.controller;

import at.fhtw.swkom.paperless.controller.DocumentController;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.*;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.exception.StorageFileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentControllerTest {

    private DocumentController controller;
    private DocumentCommandService documentCommandService;
    private DocumentQueryService documentQueryService;
    private ElasticsearchService elasticsearchService;
    private FileStorageImpl fileStorage;
    private RabbitTemplate rabbitTemplate;

    @BeforeEach
    void setUp() {
        documentCommandService = mock(DocumentCommandService.class);
        documentQueryService = mock(DocumentQueryService.class);
        elasticsearchService = mock(ElasticsearchService.class);
        fileStorage = mock(FileStorageImpl.class);
        rabbitTemplate = mock(RabbitTemplate.class);
        NativeWebRequest nativeWebRequest = mock(NativeWebRequest.class);

        controller = new DocumentController(nativeWebRequest, documentCommandService, documentQueryService, rabbitTemplate, fileStorage, elasticsearchService);
    }

    @Test
    void testDeleteDocument_Success() {
        int documentId = 1;
        doNothing().when(documentCommandService).delete(documentId);

        ResponseEntity<Void> response = controller.deleteDocument(documentId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(documentCommandService).delete(documentId);
    }

    @Test
    void testDeleteDocument_Exception() {
        int documentId = 1;
        doThrow(new RuntimeException("Test exception")).when(documentCommandService).delete(documentId);

        ResponseEntity<Void> response = controller.deleteDocument(documentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(documentCommandService).delete(documentId);
    }

    @Test
    void testGetDocument_Success() {
        int documentId = 1;
        DocumentDTO mockDocument = new DocumentDTO();
        when(documentQueryService.load(documentId)).thenReturn(mockDocument);

        ResponseEntity<DocumentDTO> response = controller.getDocument(documentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockDocument, response.getBody());
        verify(documentQueryService).load(documentId);
    }

    @Test
    void testGetDocument_Exception() {
        int documentId = 1;
        when(documentQueryService.load(documentId)).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<DocumentDTO> response = controller.getDocument(documentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(documentQueryService).load(documentId);
    }

    @Test
    void testGetDocuments_WithSearchTerm() {
        String searchTerm = "test";
        DocumentDTO mockDocument = new DocumentDTO();
        mockDocument.setId(1);
        when(elasticsearchService.searchDocuments(searchTerm)).thenReturn(List.of(mockDocument));
        when(documentQueryService.load(mockDocument.getId())).thenReturn(mockDocument);

        ResponseEntity<List<DocumentDTO>> response = controller.getDocuments(searchTerm);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        verify(elasticsearchService).searchDocuments(searchTerm);
        verify(documentQueryService).load(mockDocument.getId());
    }

    @Test
    void testGetDocuments_NoSearchTerm() {
        when(documentQueryService.loadAll()).thenReturn(List.of());

        ResponseEntity<List<DocumentDTO>> response = controller.getDocuments(null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(documentQueryService).loadAll();
    }

    @Test
    void testPostDocument_Success() throws IOException {
        String documentTitle = "Test Document";
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());
        Document mockDocument = Document.builder()
                .id(1)
                .title(documentTitle)
                .createdAt(LocalDateTime.now())
                .build();
        when(documentCommandService.store(any())).thenReturn(mockDocument);

        ResponseEntity<Void> response = controller.postDocument(documentTitle, file);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(fileStorage).upload(anyString(), eq(file.getBytes()));
        verify(rabbitTemplate).send(anyString(), any());
    }

    @Test
    void testPostDocument_BadRequest() {
        ResponseEntity<Void> response = controller.postDocument(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateDocument_Success() {
        int documentId = 1;
        doNothing().when(documentCommandService).update(eq(documentId), any());

        ResponseEntity<Void> response = controller.updateDocument(documentId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(documentCommandService).update(eq(documentId), any());
    }

    @Test
    void testUpdateDocument_Exception() {
        int documentId = 1;
        doThrow(new RuntimeException("Test exception")).when(documentCommandService).update(eq(documentId), any());

        ResponseEntity<Void> response = controller.updateDocument(documentId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(documentCommandService).update(eq(documentId), any());
    }

    @Test
    void testHandleStorageFileNotFound() {
        ResponseEntity<?> response = controller.handleStorageFileNotFound(new StorageFileNotFoundException("File not found"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

