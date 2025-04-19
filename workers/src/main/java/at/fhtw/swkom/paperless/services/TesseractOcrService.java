package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.config.ElasticsearchConfig;
import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.exception.StorageFileNotFoundException;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;

@Component
@Slf4j
public class TesseractOcrService implements OcrService {
    private final RabbitTemplate rabbit;
    private final FileStorage storageService;
    private final ElasticsearchClient esClient;
    private final String tesseractData;

    @Autowired
    public TesseractOcrService(RabbitTemplate rabbit, FileStorage storageService, ElasticsearchClient esClient, @Value("${tesseract.data}") String tessData) {
        this.rabbit = rabbit;
        this.storageService = storageService;
        this.esClient = esClient;
        this.tesseractData = tessData;
    }

    @Override
    @RabbitListener(queues = RabbitMQConfig.OCR_QUEUE_NAME)
    public void processMessage(org.springframework.amqp.core.Message message) {
        log.info("Received Message: {}", message);

        try {
            String storagePath = message.getMessageProperties().getHeader("storagePath");
            Integer documentId = message.getMessageProperties().getHeader("documentId");

            if (storagePath == null || storagePath.isEmpty() || documentId == null) {
                throw new IllegalArgumentException("Storage path or Document ID is missing in message headers");
            }

            log.debug("Storage Path: {}", storagePath);
            log.debug("Document ID: {}", documentId);

            byte[] fileBytes = storageService.download(storagePath);
            if (fileBytes == null || fileBytes.length == 0) {
                throw new StorageFileNotFoundException(storagePath);
            }

            try (InputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                File tempFile = createTempFile(storagePath, inputStream);
                String ocrResult = doOCR(tempFile);
                log.info("OCR Result: {}", ocrResult);

                // Index the OCR result in Elasticsearch
                indexDocumentInElasticsearch(documentId, ocrResult);

                // Prepare headers for the new message
                MessageProperties newMessageProperties = new MessageProperties();
                newMessageProperties.setHeader("documentId", documentId);

                // Create a new message with the OCR result and headers
                org.springframework.amqp.core.Message newMessage = new org.springframework.amqp.core.Message(
                        ocrResult.getBytes(),
                        newMessageProperties
                );

                rabbit.send(RabbitMQConfig.RESULT_QUEUE_NAME, newMessage);
                log.info("Message sent to RabbitMQ queue: {}", RabbitMQConfig.RESULT_QUEUE_NAME);

            } catch (TesseractException | IOException e) {
                log.error("Error during OCR process", e);
            }

        } catch (Exception e) {
            log.error("Error processing message", e);
        }
    }

    public void indexDocumentInElasticsearch(Integer documentId, String content) {
        try {
            DocumentDTO documentDTO = new DocumentDTO();
            documentDTO.setId(documentId);
            documentDTO.setContent(content);

            IndexResponse response = esClient.index(i -> i
                    .index(ElasticsearchConfig.DOCUMENTS_INDEX_NAME)
                    .id(documentId.toString())
                    .document(documentDTO)
            );

            log.info("Indexed document {}: result={}, index={}", documentId, response.result(), response.index());
        } catch (IOException e) {
            log.error("Failed to index document in Elasticsearch", e);
        }
    }

    public String doOCR(File tempFile) throws TesseractException {
        var tesseract = new Tesseract();
        tesseract.setDatapath(tesseractData);
        tesseract.setLanguage("eng");
        return tesseract.doOCR(tempFile);
    }

    private static File createTempFile(String storagePath, InputStream is) throws IOException {
        File tempFile = File.createTempFile(StringUtils.getFilename(storagePath), "." + StringUtils.getFilenameExtension(storagePath));
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }
}
