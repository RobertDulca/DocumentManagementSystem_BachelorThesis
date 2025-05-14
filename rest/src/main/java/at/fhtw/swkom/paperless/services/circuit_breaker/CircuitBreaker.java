package at.fhtw.swkom.paperless.services.circuit_breaker;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CircuitBreaker {
    private static final Logger logger = LogManager.getLogger(CircuitBreaker.class);

    private CircuitBreakerState state = CircuitBreakerState.CLOSED;
    private int failureCount = 0;
    private int successCount = 0;
    private final int failureThreshold = 3;
    private final int successThreshold = 2;
    private final long openTimeoutMillis = 10_000;
    private long lastFailureTime = 0;

    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        switch (state) {
            case OPEN:
                if ((now - lastFailureTime) >= openTimeoutMillis) {
                    state = CircuitBreakerState.HALF_OPEN;
                    logStateChange("Transition to HALF_OPEN after timeout");
                    return true;
                }
                return false;

            case HALF_OPEN:
            case CLOSED:
                return true;

            default:
                return false;
        }
    }

    public synchronized void recordSuccess() {
        if (state == CircuitBreakerState.HALF_OPEN) {
            successCount++;
            if (successCount >= successThreshold) {
                state = CircuitBreakerState.CLOSED;
                resetCounts();
                logStateChange("Transition to CLOSED after successful test calls");
            }
        } else {
            resetCounts();
        }
    }

    public synchronized void recordFailure() {
        failureCount++;
        if (state == CircuitBreakerState.HALF_OPEN || failureCount >= failureThreshold) {
            state = CircuitBreakerState.OPEN;
            lastFailureTime = System.currentTimeMillis();
            resetCounts();
            logStateChange("Transition to OPEN due to failures");
        }
    }

    private void resetCounts() {
        failureCount = 0;
        successCount = 0;
    }

    private void logStateChange(String message) {
        logger.warn("[CircuitBreaker] {} | New state: {}", message, state);
    }
}
