package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.sage_orchestrator.DocumentSagaOrchestrator;
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
    private final DocumentSagaOrchestrator sagaOrchestrator;

    @Autowired
    public ResultQueueContentService(DocumentSagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @RabbitListener(queues = RabbitMQConfig.RESULT_QUEUE_NAME)
    public void processMessage(org.springframework.amqp.core.Message message) {
        try {
            // Retrieve the document ID
            Object documentIdHeader = message.getMessageProperties().getHeader("documentId");
            if (documentIdHeader == null) {
                log.warn("Document ID is missing in message headers.");
                return;
            }
            Integer documentId = (Integer) documentIdHeader;

            // Check if OCR failed
            Object ocrFailedHeader = message.getMessageProperties().getHeader("ocrFailed");
            boolean ocrFailed = ocrFailedHeader != null && Boolean.TRUE.equals(ocrFailedHeader);

            if (ocrFailed) {
                log.warn("OCR failed for document ID {}. Triggering compensation.", documentId);
                sagaOrchestrator.compensateDocumentUpload(documentId); // you must expose this method publicly
            } else {
                String ocrContent = new String(message.getBody());
                sagaOrchestrator.completeOcrStep(documentId, ocrContent);
            }

        } catch (Exception e) {
            log.error("Error processing message from result queue: {}", e.getMessage(), e);
        }
    }

}
