package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.exception.StorageException;
import at.fhtw.swkom.paperless.services.mappers.DocumentMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentCommandService {
    private static final Logger logger = LogManager.getLogger(DocumentCommandService.class);

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Autowired
    public DocumentCommandService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    public Document store(Document documentEntity) {
        if (documentEntity == null) {
            throw new StorageException("No documentEntity found!");
        }
        Document saved = documentRepository.save(documentEntity);
        logger.info("Document stored successfully with ID {}", saved.getId());
        return saved;
    }

    public void delete(Integer id) {
        if (!documentRepository.existsById(id)) {
            throw new EntityNotFoundException("Document with ID " + id + " not found for deletion");
        }
        documentRepository.deleteById(id);
        logger.info("Document with ID {} deleted successfully", id);
    }

    public void update(Integer id, DocumentDTO documentDTO) {
        Document existingDocument = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document with ID " + id + " not found for update"));

        Document updatedDocument = documentMapper.dtoToEntity(documentDTO);
        updatedDocument.setId(existingDocument.getId());

        documentRepository.save(updatedDocument);
        logger.info("Document with ID {} updated successfully", id);
    }
}
