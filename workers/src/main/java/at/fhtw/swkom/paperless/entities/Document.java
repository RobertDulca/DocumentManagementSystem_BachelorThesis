package at.fhtw.swkom.paperless.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor(force = true) // Ensures a no-args constructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String title;
    private String content;
    private String documentType;
    private LocalDateTime createdAt;

    @Builder
    public Document(String title) {
        this.title = title;
        this.createdAt = LocalDateTime.now();
    }
}

