package at.fhtw.swkom.paperless.config;

import at.fhtw.swkom.paperless.services.ServicesErrorHandler;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;


@Configuration
public class RabbitMQConfig {

    public static final String OCR_QUEUE_NAME = "OCR_QUEUE";
    public static final String RESULT_QUEUE_NAME = "RESULT_QUEUE";
    public static final String DOCUMENT_STORAGE_PATH_PROPERTY_NAME = "FileStoragePath";
    public static final String ECHO_MESSAGE_COUNT_PROPERTY_NAME = "MessageCount";

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("queue");
        connectionFactory.setUsername("test");
        connectionFactory.setPassword("test");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setDefaultReceiveQueue(OCR_QUEUE_NAME);
        return rabbitTemplate;
    }

    // Custom error handling with Spring AMQP
    // see <a href="https://www.baeldung.com/spring-amqp-error-handling">Error Handling with Spring AMQP</a>
    // Necessary to avoid requeuing of messages with JSON-parse exceptions
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(errorHandler());
        return factory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ServicesErrorHandler();
    }
}
