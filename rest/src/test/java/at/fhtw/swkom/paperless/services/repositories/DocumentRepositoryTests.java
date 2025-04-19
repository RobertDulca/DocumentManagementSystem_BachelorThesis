package at.fhtw.swkom.paperless.services.repositories;

import at.fhtw.swkom.paperless.controller.DocumentController;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.ElasticsearchService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource("/application-test.properties")
class DocumentRepositoryTests {

    @MockBean
    private ElasticsearchService elasticsearchService;

    @MockBean
    private DocumentController documentController;

    @MockBean
    RabbitListenerContainerFactory rabbitListenerContainerFactory;

    @Autowired
    private DocumentRepository documentRepository;

    @Test
    void testSaveAndFindById() {
        // Arrange
        Document document = Document.builder()
                .title("Test Document")
                .createdAt(LocalDateTime.now())
                .build();

        // Act
        Document savedDocument = documentRepository.save(document);
        Optional<Document> retrievedDocument = documentRepository.findById(savedDocument.getId());

        // Assert
        assertTrue(retrievedDocument.isPresent());
        assertEquals("Test Document", retrievedDocument.get().getTitle());
    }

    @Test
    void testFindByTitle() {
        // Arrange
        Document document = Document.builder()
                .title("Unique Title")
                .createdAt(LocalDateTime.now())
                .build();
        documentRepository.save(document);

        // Act
        Document foundDocument = documentRepository.findByTitle("Unique Title");

        // Assert
        assertNotNull(foundDocument);
        assertEquals("Unique Title", foundDocument.getTitle());
    }

    @Test
    void testDeleteById() {
        // Arrange
        Document document = Document.builder()
                .title("To Be Deleted")
                .createdAt(LocalDateTime.now())
                .build();
        Document savedDocument = documentRepository.save(document);

        // Act
        documentRepository.deleteById(savedDocument.getId());
        Optional<Document> retrievedDocument = documentRepository.findById(savedDocument.getId());

        // Assert
        assertFalse(retrievedDocument.isPresent());
    }
}

