package org.victor.dispatch;

import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
public class ElevatorRequest {
    
    private final String requestId;
    private final String userId;
    private final int targetFloor;
    private final ElevatorType elevatorType;
    private final boolean hasKeycard;
    private final long createdAt;

    public enum ElevatorType {
        PUBLIC,
        FREIGHT
    }

    public ElevatorRequest(String userId, int targetFloor, ElevatorType elevatorType, boolean hasKeycard) {
        this.requestId = java.util.UUID.randomUUID().toString();
        this.userId = userId;
        this.targetFloor = targetFloor;
        this.elevatorType = elevatorType;
        this.hasKeycard = hasKeycard;
        this.createdAt = System.currentTimeMillis();
    }

    public long getAgeMs() {
        return System.currentTimeMillis() - createdAt;
    }

    public boolean hasKeycard() {
        return this.hasKeycard;
    }
}
