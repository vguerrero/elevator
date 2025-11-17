package org.victor.exception;

/**
 * custom exception thrown when an invalid floor is requested
 */
public class InvalidFloorException extends ElevatorException {
    
    private final int requestedFloor;
    private final int minFloor;
    private final int maxFloor;

    public InvalidFloorException(int requestedFloor, int minFloor, int maxFloor) {
        super(String.format("Invalid floor: %d is outside range [%d, %d]", 
                requestedFloor, minFloor, maxFloor));
        this.requestedFloor = requestedFloor;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
    }

    public int getRequestedFloor() {
        return requestedFloor;
    }

    public int getMinFloor() {
        return minFloor;
    }

    public int getMaxFloor() {
        return maxFloor;
    }
}
