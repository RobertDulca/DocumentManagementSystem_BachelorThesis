package at.fhtw.swkom.paperless.services;

import at.fhtw.swkom.paperless.services.dto.DocumentDTO;
import co.elastic.clients.elasticsearch._types.Result;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface SearchIndexService {
    Result indexDocument(DocumentDTO document) throws IOException;

    Optional<DocumentDTO> getDocumentById(int id);

    boolean deleteDocumentById(int id);

    List<DocumentDTO> searchDocuments(String searchTerm);
}
