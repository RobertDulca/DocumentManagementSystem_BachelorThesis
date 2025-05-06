package at.fhtw.swkom.paperless.services.sage_orchestrator;

public enum SagaStep {
    DOCUMENT_STORED,
    DB_ENTRY_CREATED,
    OCR_REQUESTED,
    OCR_COMPLETED,
    INDEXED
}
