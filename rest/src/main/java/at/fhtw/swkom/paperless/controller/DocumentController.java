package at.fhtw.swkom.paperless.controller;

import at.fhtw.swkom.paperless.config.RabbitMQConfig;
import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.DocumentService;
import at.fhtw.swkom.paperless.services.ElasticsearchService;
import at.fhtw.swkom.paperless.services.FileStorageImpl;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.exception.StorageFileNotFoundException;
import at.fhtw.swkom.paperless.services.sage_orchestrator.DocumentSagaOrchestrator;
import jakarta.annotation.Generated;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-10-17T08:44:06.510922473Z[Etc/UTC]", comments = "Generator version: 7.10.0-SNAPSHOT")
@RestController
@RequestMapping("${openapi.paperlessRESTServer.base-path:}")
public class DocumentController implements ApiApi {

    private static final Logger logger = LogManager.getLogger(DocumentController.class);

    private final NativeWebRequest request;
    private final DocumentService documentService;
    private final RabbitTemplate rabbitTemplate;
    private final FileStorageImpl fileStorage;
    private final ElasticsearchService elasticsearchService;
    private final DocumentSagaOrchestrator documentSagaOrchestrator;

    @Autowired
    public DocumentController(NativeWebRequest request, DocumentService documentService, RabbitTemplate rabbitTemplate, FileStorageImpl fileStorage, ElasticsearchService elasticsearchService, DocumentSagaOrchestrator documentSagaOrchestrator) {
        this.request = request;
        this.documentService = documentService;
        this.rabbitTemplate = rabbitTemplate;
        this.fileStorage = fileStorage;
        this.elasticsearchService = elasticsearchService;
        this.documentSagaOrchestrator = documentSagaOrchestrator;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    @Override
    public ResponseEntity<Void> deleteDocument(Integer id) {
        logger.info("Received request to delete document with ID: {}", id);
        try {
            documentService.delete(id);
            elasticsearchService.deleteDocumentById(id);
            logger.info("Successfully deleted document with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            logger.error("Error occurred while deleting document with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<DocumentDTO> getDocument(Integer id) {
        logger.info("Received request to fetch document with ID: {}", id);
        try {
            DocumentDTO document = documentService.load(id);
            logger.info("Successfully fetched document with ID: {}", id);
            return new ResponseEntity<>(document, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error occurred while fetching document with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<List<DocumentDTO>> getDocuments(String search) {
        logger.info("Received request to fetch documents with search term: {}", search);

        try {
            List<DocumentDTO> combinedResults = new ArrayList<>();

            // If a search term is provided, perform a search
            if (search != null && !search.isBlank()) {
                logger.info("Performing search for documents matching: {}", search);
                List<DocumentDTO> results = elasticsearchService.searchDocuments(search);

                if (results.isEmpty()) {
                    logger.info("No documents found for search term: {}", search);
                    return ResponseEntity.noContent().build();
                }

                logger.info("Successfully found {} documents for search term: {}", results.size(), search);

                for (DocumentDTO result : results) {
                    Optional<DocumentDTO> document = Optional.ofNullable(documentService.load(result.getId()));
                    document.ifPresent(combinedResults::add);
                }

                return ResponseEntity.ok(combinedResults);
            }

            // If no search term, fetch all documents
            logger.info("Fetching all documents");
            List<DocumentDTO> documents = documentService.loadAll();

            if (documents.isEmpty()) {
                logger.info("No documents found");
                return ResponseEntity.noContent().build();
            }

            logger.info("Successfully fetched {} documents", documents.size());
            return ResponseEntity.ok(documents);

        } catch (Exception e) {
            logger.error("Error occurred while fetching documents", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Override
    public ResponseEntity<Void> postDocument(String documentTitle, MultipartFile file) {
        logger.info("Received request to upload a document");

        if (documentTitle == null || file == null || file.isEmpty()) {
            logger.warn("Bad request: document title or file is null/empty");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            documentSagaOrchestrator.startDocumentUpload(documentTitle, file);
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (Exception e) {
            logger.error("Error occurred while uploading document", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @Override
    public ResponseEntity<Void> updateDocument(Integer id) {
        logger.info("Received request to update document with ID: {}", id);
        try {
            DocumentDTO documentDTO = new DocumentDTO();
            documentService.update(id, documentDTO);
            logger.info("Successfully updated document with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error occurred while updating document with ID: {}", id, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }



    private static String getFilePath(HttpServletRequest rq) {
        String servletPath = rq.getServletPath();
        if ( servletPath == null || servletPath.isEmpty() || servletPath.isBlank() )
            return servletPath;
        if ( servletPath.startsWith("/") )
            return servletPath.substring(1);
        return servletPath;
    }

    private String getSecureFullPath(String fileUploadDirectory, String filePath) {
        filePath = filePath.replaceFirst("^(/|\\\\)", "");

        Path path = Paths.get(fileUploadDirectory, filePath)/*.toAbsolutePath()*/.normalize();

        if (!path.startsWith(Paths.get(fileUploadDirectory))) {
            throw new IllegalArgumentException("Not a valid path");
        } else {
            return path.toString();
        }
    }
}
