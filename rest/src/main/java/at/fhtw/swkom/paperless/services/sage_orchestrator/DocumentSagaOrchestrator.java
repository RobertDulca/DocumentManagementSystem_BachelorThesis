package at.fhtw.swkom.paperless.services.sage_orchestrator;

import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.controller.DocumentController;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.FileStorage;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DocumentSagaOrchestrator {
    private static final Logger logger = LogManager.getLogger(DocumentSagaOrchestrator.class);

    private final DocumentService documentService;
    private final FileStorage fileStorage;
    private final RabbitTemplate rabbitTemplate;

    public void startDocumentUpload(String title, MultipartFile file) {
        String objectName = null;
        Integer documentId = null;

        try {
            // Step 1: create and save metadata
            Document document = Document.builder()
                    .title(title)
                    .createdAt(LocalDateTime.now())
                    .build();
            document = documentService.store(document);
            documentId = document.getId();

            // Step 2: upload to MinIO
            String folderPath = "documents/" + documentId + "/";
            objectName = folderPath + file.getOriginalFilename();
            logger.debug("Uploading file to MinIO: {}", objectName);
            fileStorage.upload(objectName, file.getBytes());

            // Step 3: send message to OCR queue
            MessageProperties props = new MessageProperties();
            props.setHeader("storagePath", objectName);
            props.setHeader("documentId", documentId);
            Message rabbitMessage = new Message(new byte[0], props);

            rabbitTemplate.send(RabbitMQConfig.OCR_QUEUE_NAME, rabbitMessage);
            logger.info("Headers sent to RabbitMQ queue: {}", RabbitMQConfig.OCR_QUEUE_NAME);

        } catch (Exception e) {
            logger.error("Saga failed: {}", e.getMessage(), e);

            if (documentId != null && objectName != null) {
                compensateDocumentUpload(documentId, objectName);
            }
        }
    }


    public void completeOcrStep(Integer documentId, String ocrContent) {
        // Retrieve the document by ID from the database
        DocumentDTO documentDTO = documentService.load(documentId);

        if (documentDTO != null) {
            logger.info("ocrContent: {}", ocrContent);
            // Truncate content if it exceeds 255 characters
            String truncatedContent = ocrContent.length() > 255
                    ? ocrContent.substring(0, 255)
                    : ocrContent;

            // Update the document content with the truncated content
            documentDTO.setContent(truncatedContent);

            // Save the updated document
            documentService.update(documentId, documentDTO);

            logger.info("Successfully updated document ID {} with OCR content.", documentId);
        } else {
            logger.warn("Document with ID {} not found in the database.", documentId);
        }
    }

    public void compensateDocumentUpload(Integer documentId, String objectPath) {
        logger.warn("Compensating upload for document ID {}", documentId);

        try {
            // Attempt to retrieve document (if still in DB)
            DocumentDTO document = null;
            try {
                document = documentService.load(documentId);
            } catch (Exception e) {
                logger.warn("Document not found in DB during compensation (might already be deleted): {}", e.getMessage());
            }

            // Reconstruct path if necessary
            if (objectPath == null && document != null && document.getTitle() != null) {
                // This assumes the original filename was derived from the title
                objectPath = "documents/" + documentId + "/" + document.getTitle() + ".pdf"; // or whatever extension you expect
                logger.info("Reconstructed object path: {}", objectPath);
            }

            // Delete from DB if present
            if (document != null) {
                documentService.delete(documentId);
                logger.info("Deleted document entry from DB");
            }

            // Delete from MinIO if object path is known
            if (objectPath != null) {
                fileStorage.delete(objectPath);
                logger.info("Deleted file from MinIO: {}", objectPath);
            } else {
                logger.warn("File path unknown. Could not delete object from MinIO.");
            }

        } catch (Exception ex) {
            logger.error("Compensation failed for document ID {}: {}", documentId, ex.getMessage(), ex);
        }
    }

    public void compensateDocumentUpload(Integer documentId) {
        compensateDocumentUpload(documentId, null);
    }

}
