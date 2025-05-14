package at.fhtw.swkom.paperless.services.circuit_breaker;

public enum CircuitBreakerState {
    CLOSED,
    OPEN,
    HALF_OPEN
}
