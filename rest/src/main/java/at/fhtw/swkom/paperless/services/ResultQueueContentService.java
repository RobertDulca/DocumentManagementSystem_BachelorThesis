package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.Optional;

@Component
@Slf4j
public class ResultQueueContentService {
    private final RabbitTemplate rabbit;
    private final DocumentService documentService;

    @Autowired
    public ResultQueueContentService(RabbitTemplate rabbit, DocumentService documentService) {
        this.rabbit = rabbit;
        this.documentService = documentService;
    }

    @RabbitListener(queues = RabbitMQConfig.RESULT_QUEUE_NAME)
    public void processMessage(org.springframework.amqp.core.Message message) {
        try {
            // Retrieve the document ID from message headers
            Object documentIdHeader = message.getMessageProperties().getHeader("documentId");
            if (documentIdHeader == null) {
                log.warn("Document ID is missing in message headers.");
                return;
            }

            Integer documentId = (Integer) documentIdHeader;
            String ocrContent = new String(message.getBody());

            // Retrieve the document by ID from the database
            DocumentDTO documentDTO = documentService.load(documentId);

            if (documentDTO != null) {
                log.info("ocrContent: {}", ocrContent);
                // Truncate content if it exceeds 255 characters
                String truncatedContent = ocrContent.length() > 255
                        ? ocrContent.substring(0, 255)
                        : ocrContent;

                // Update the document content with the truncated content
                documentDTO.setContent(truncatedContent);

                // Save the updated document
                documentService.update(documentId, documentDTO);

                log.info("Successfully updated document ID {} with OCR content.", documentId);
            } else {
                log.warn("Document with ID {} not found in the database.", documentId);
            }
        } catch (Exception e) {
            log.error("Error processing message from result queue: {}", e.getMessage(), e);
        }
    }

}
