package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import at.fhtw.swkom.paperless.services.dto.DocumentDTO;

import java.util.List;

public interface DocumentService {

    Document store(Document documentEntity);

    List<DocumentDTO> loadAll();

    DocumentDTO load(Integer id);

    void delete(Integer id);

    void update(Integer id, DocumentDTO documentDTO);
}
