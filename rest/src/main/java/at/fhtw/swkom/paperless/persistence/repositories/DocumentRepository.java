package at.fhtw.swkom.paperless.persistence.repositories;

import at.fhtw.swkom.paperless.persistence.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    Document findByTitle(String title);
}

