package org.victor.exception;

/**
 * custom exception thrown when an elevator is overloaded
 */
public class ElevatorOverloadException extends ElevatorException {
    
    private final double currentWeight;
    private final double maxWeight;

    public ElevatorOverloadException(String elevatorName, double currentWeight, double maxWeight) {
        super(String.format("%s overloaded: %f kg exceeds limit of %f kg", 
                elevatorName, currentWeight, maxWeight));
        this.currentWeight = currentWeight;
        this.maxWeight = maxWeight;
    }

    public double getCurrentWeight() {
        return currentWeight;
    }

    public double getMaxWeight() {
        return maxWeight;
    }
}
