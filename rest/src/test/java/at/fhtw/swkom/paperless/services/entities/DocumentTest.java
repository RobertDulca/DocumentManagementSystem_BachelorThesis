package at.fhtw.swkom.paperless.services.entities;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {

    @Test
    void testNoArgsConstructor() {
        Document document = new Document();
        assertNotNull(document);
    }

    @Test
    void testAllArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        Document document = new Document(1, "Test Title", "Test Content", "PDF", now);

        assertEquals(1, document.getId());
        assertEquals("Test Title", document.getTitle());
        assertEquals("Test Content", document.getContent());
        assertEquals("PDF", document.getDocumentType());
        assertEquals(now, document.getCreatedAt());
    }

    @Test
    void testBuilderWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        Document document = Document.builder()
                .id(1)
                .title("Test Title")
                .content("Test Content")
                .documentType("PDF")
                .createdAt(now)
                .build();

        assertEquals(1, document.getId());
        assertEquals("Test Title", document.getTitle());
        assertEquals("Test Content", document.getContent());
        assertEquals("PDF", document.getDocumentType());
        assertEquals(now, document.getCreatedAt());
    }

    @Test
    void testSettersAndGetters() {
        Document document = new Document();
        LocalDateTime now = LocalDateTime.now();

        document.setId(2);
        document.setTitle("Updated Title");
        document.setContent("Updated Content");
        document.setDocumentType("DOCX");
        document.setCreatedAt(now);

        assertEquals(2, document.getId());
        assertEquals("Updated Title", document.getTitle());
        assertEquals("Updated Content", document.getContent());
        assertEquals("DOCX", document.getDocumentType());
        assertEquals(now, document.getCreatedAt());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        Document doc1 = new Document(1, "Title", "Content", "PDF", now);
        Document doc2 = new Document(1, "Title", "Content", "PDF", now);

        assertEquals(doc1, doc2);
        assertEquals(doc1.hashCode(), doc2.hashCode());

        Document doc3 = new Document(2, "Different Title", "Different Content", "DOCX", now);
        assertNotEquals(doc1, doc3);
    }

    @Test
    void testToString() {
        LocalDateTime now = LocalDateTime.now();
        Document document = new Document(1, "Title", "Content", "PDF", now);

        String expectedString = "Document(id=1, title=Title, content=Content, documentType=PDF, createdAt=" + now + ")";
        assertEquals(expectedString, document.toString());
    }
}

