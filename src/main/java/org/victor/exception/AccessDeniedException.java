package org.victor.exception;

/**
 * thrown when access to a restricted floor is denied
 */
public class AccessDeniedException extends ElevatorException {
    
    private final String reason;

    public AccessDeniedException(String message, String reason) {
        super(message);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
