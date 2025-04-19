package at.fhtw.swkom.paperless.services.services.dto;

import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentDTOTest {

    @Test
    void testConstructorAndGetters() {
        String title = "Test Document";
        LocalDateTime now = LocalDateTime.now();

        DocumentDTO document = new DocumentDTO(title);
        document.setId(1);
        document.setCreatedAt(now);
        document.setContent("This is a test document content");

        assertEquals(1, document.getId());
        assertEquals(title, document.getTitle());
        assertEquals(now, document.getCreatedAt());
        assertEquals("This is a test document content", document.getContent());
    }

    @Test
    void testSetters() {
        DocumentDTO document = new DocumentDTO();

        document.setId(10);
        document.setTitle("Another Test Document");
        LocalDateTime createdAt = LocalDateTime.of(2025, 1, 1, 12, 0);
        document.setCreatedAt(createdAt);
        document.setContent("Updated content");

        assertEquals(10, document.getId());
        assertEquals("Another Test Document", document.getTitle());
        assertEquals(createdAt, document.getCreatedAt());
        assertEquals("Updated content", document.getContent());
    }

    @Test
    void testEqualsAndHashCode() {
        DocumentDTO doc1 = new DocumentDTO("Document 1");
        doc1.setId(1);
        doc1.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));

        DocumentDTO doc2 = new DocumentDTO("Document 1");
        doc2.setId(1);
        doc2.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));

        DocumentDTO doc3 = new DocumentDTO("Document 2");
        doc3.setId(2);
        doc3.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());
        assertNotEquals(doc1, doc3);
        assertNotEquals(doc1.hashCode(), doc3.hashCode());
    }

    @Test
    void testToString() {
        DocumentDTO document = new DocumentDTO("Test Document");
        document.setId(100);
        document.setCreatedAt(LocalDateTime.of(2025, 1, 1, 12, 0));

        String expected = "class Document {\n" +
                "    id: 100\n" +
                "    title: Test Document\n" +
                "    created: 2025-01-01T12:00\n" +
                "}";

        assertEquals(expected, document.toString());
    }

    @Test
    void testEmptyConstructor() {
        DocumentDTO document = new DocumentDTO();

        assertNull(document.getId());
        assertNull(document.getTitle());
        assertNull(document.getCreatedAt());
        assertNull(document.getContent());
    }

    @Test
    void testPartialConstructor() {
        DocumentDTO document = new DocumentDTO("Partial Constructor Test");

        assertNull(document.getId());
        assertEquals("Partial Constructor Test", document.getTitle());
        assertNull(document.getCreatedAt());
        assertNull(document.getContent());
    }
}

