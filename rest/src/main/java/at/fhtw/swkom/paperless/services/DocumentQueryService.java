package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.repositories.DocumentRepository;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import at.fhtw.swkom.paperless.services.mappers.DocumentMapper;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentQueryService {
    private static final Logger logger = LogManager.getLogger(DocumentQueryService.class);

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Autowired
    public DocumentQueryService(DocumentRepository documentRepository, DocumentMapper documentMapper) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
    }

    public List<DocumentDTO> loadAll() {
        List<DocumentDTO> results = documentRepository.findAll()
                .stream()
                .map(documentMapper::entityToDto)
                .collect(Collectors.toList());
        logger.info("Loaded {} documents from database", results.size());
        return results;
    }

    public DocumentDTO load(Integer id) {
        DocumentDTO dto = documentRepository.findById(id)
                .map(documentMapper::entityToDto)
                .orElseThrow(() -> new EntityNotFoundException("Document with ID " + id + " not found"));
        logger.info("Loaded document with ID {}", id);
        return dto;
    }
}
