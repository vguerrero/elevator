package org.victor.exception;

/**
 * thrown when an elevator is not operational
 */
public class ElevatorNotOperationalException extends ElevatorException {
    
    public ElevatorNotOperationalException(String elevatorName) {
        super(elevatorName + " is not operational");
    }
}
