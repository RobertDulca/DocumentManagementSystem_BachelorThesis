package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.exception.StorageFileNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public interface OcrService {
    @RabbitListener(queues = RabbitMQConfig.OCR_QUEUE_NAME)
    public void processMessage(org.springframework.amqp.core.Message message) throws JsonProcessingException;
}
