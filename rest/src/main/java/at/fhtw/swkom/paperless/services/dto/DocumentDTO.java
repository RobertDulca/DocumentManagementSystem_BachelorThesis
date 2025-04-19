package at.fhtw.swkom.paperless.services.dto;

import java.time.LocalDateTime;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import jakarta.annotation.Generated;

/**
 * Document
 */

@JsonTypeName("document")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2024-11-26T19:49:30.564802578Z[Etc/UTC]", comments = "Generator version: 7.10.0-SNAPSHOT")
public class DocumentDTO {

    private Integer id;

    private String title;

    private LocalDateTime createdAt;
    private String content;

    public DocumentDTO() {
        super();
    }

    /**
     * Constructor with only required parameters
     */
    public DocumentDTO(String title) {
        this.title = title;
    }

    public DocumentDTO(int i, String s, LocalDateTime now, Object o) {
    }

    public DocumentDTO id(Integer id) {
        this.id = id;
        return this;
    }

    /**
     * The id of the document
     * @return id
     */

    @Schema(name = "id", description = "The id of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DocumentDTO title(String title) {
        this.title = title;
        return this;
    }

    /**
     * The title of the document
     * @return title
     */
    @NotNull
    @Schema(name = "title", description = "The title of the document", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DocumentDTO content(String content) {
        this.content = content;
        return this;
    }

    /**
     * The content of the document
     * @return content
     */
    @Schema(name = "content", description = "The content of the document", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DocumentDTO created(LocalDateTime created) {
        this.createdAt = created;
        return this;
    }

    /**
     * Get created
     * @return created
     */

    @Schema(name = "created", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("created")
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DocumentDTO document = (DocumentDTO) o;
        return Objects.equals(this.id, document.id) &&
                Objects.equals(this.title, document.title) &&
                Objects.equals(this.createdAt, document.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, createdAt);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Document {\n");
        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    created: ").append(toIndentedString(createdAt)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

