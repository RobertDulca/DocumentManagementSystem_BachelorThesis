package at.fhtw.swkom.paperless.services;

import java.util.concurrent.Callable;

public class CircuitBreaker {
    public enum State { CLOSED, OPEN, HALF_OPEN }

    private State state = State.CLOSED;

    private final int failureThreshold;
    private final int successThreshold;
    private final long openTimeoutMillis;

    private int failureCount = 0;
    private int successCount = 0;
    private long lastFailureTime = 0;

    public CircuitBreaker(int failureThreshold, int successThreshold, long openTimeoutMillis) {
        this.failureThreshold = failureThreshold;
        this.successThreshold = successThreshold;
        this.openTimeoutMillis = openTimeoutMillis;
    }

    public synchronized <T> T call(Callable<T> task, Callable<T> fallback) throws Exception {
        long now = System.currentTimeMillis();

        switch (state) {
            case OPEN:
                if (now - lastFailureTime >= openTimeoutMillis) {
                    state = State.HALF_OPEN;
                    successCount = 0;
                } else {
                    return fallback.call();
                }
                break;

            case HALF_OPEN:
                try {
                    T result = task.call();
                    successCount++;
                    if (successCount >= successThreshold) {
                        reset();
                    }
                    return result;
                } catch (Exception e) {
                    trip();
                    return fallback.call();
                }

            case CLOSED:
                try {
                    T result = task.call();
                    reset();
                    return result;
                } catch (Exception e) {
                    failureCount++;
                    if (failureCount >= failureThreshold) {
                        trip();
                    }
                    throw e;
                }
        }
        return null; // This line should never be reached
    }


    private void reset() {
        state = State.CLOSED;
        failureCount = 0;
        successCount = 0;
    }

    private void trip() {
        state = State.OPEN;
        lastFailureTime = System.currentTimeMillis();
    }

    public State getState() {
        return state;
    }
}
