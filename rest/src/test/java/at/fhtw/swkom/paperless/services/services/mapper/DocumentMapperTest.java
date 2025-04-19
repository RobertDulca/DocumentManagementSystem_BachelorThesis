package at.fhtw.swkom.paperless.services.services.mapper;

import at.fhtw.swkom.paperless.controller.DocumentController;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.DocumentServiceImpl;
import at.fhtw.swkom.paperless.services.ElasticsearchService;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mappers.DocumentMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@TestPropertySource("/application-test.properties")
class DocumentMapperTest {

    @MockBean
    DocumentServiceImpl service;

    @MockBean
    DocumentRepository repository;

    @MockBean
    JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockBean
    private ElasticsearchService elasticsearchService;

    @MockBean
    private DocumentController documentController;

    @MockBean
    RabbitListenerContainerFactory rabbitListenerContainerFactory;

   @Test
    void dtoToEntity() {
        // Arrange
        var dtoDocument = new DocumentDTO();
        dtoDocument.setTitle("title");

        // Act
        var result = DocumentMapper.INSTANCE.dtoToEntity(dtoDocument);

        // Assert
        assertNotNull( result );
        assertEquals( dtoDocument.getTitle(), result.getTitle() );
    }

    @Test
    void entityToDto() {
        // Arrange
        var entityDocument = Document.builder()
                .id( 1 )
                .title( "title")
                .build();

        // Act
        var result = DocumentMapper.INSTANCE.entityToDto(entityDocument);

        // Assert
        assertNotNull( result );
        assertEquals( entityDocument.getTitle(), result.getTitle());
    }
}
