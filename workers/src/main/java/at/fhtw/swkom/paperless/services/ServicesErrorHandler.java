package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.exception.StorageFileNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.util.ErrorHandler;

/**
 * Custom error handling
 * see <a href="https://www.baeldung.com/spring-amqp-error-handling">Error Handling with Spring AMQP</a>
 */
@Slf4j
public class ServicesErrorHandler implements ErrorHandler {

    @Override
    public void handleError(Throwable t) {
        log.error("Error occurred while processing message: " + t.getMessage());

        // Necessary to avoid requeuing of messages with JSON-parse exceptions:
        if ( t.getCause() instanceof StorageFileNotFoundException)
            throw new AmqpRejectAndDontRequeueException("File not found on Storage", t);
        if ( t.getCause() instanceof JsonProcessingException)
            throw new AmqpRejectAndDontRequeueException("Invalid JSON", t);
    }
}

