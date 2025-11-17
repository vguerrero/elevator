package org.victor;

import lombok.extern.slf4j.Slf4j;
import org.victor.audit.AuditEvent;
import org.victor.exception.AccessDeniedException;

@Slf4j
public class PublicElevator extends Elevator {

    public static final int MAX_FLOOR = 50;
    public static final int MAX_WEIGHT = 1000;

    public PublicElevator(int totalFloors) {
        super("Public Elevator", MAX_WEIGHT, totalFloors);
    }

    // move elevator to restricted floor requiring keycard
    public void goToFloor(int floor, boolean hasKeycard) {
        lock.readLock().lock();
        try {
            if ((floor == 0 || floor == MAX_FLOOR) && !hasKeycard) {
                String reason = String.format("Keycard required for floor %d", floor);

                logAuditEvent(AuditEvent.EventType.ACCESS_DENIED,
                        reason, AuditEvent.AuditLevel.WARNING);

                throw new AccessDeniedException(
                        "Access denied: keycard required for floor " + floor,
                        "MISSING_KEYCARD");
            }
        } finally {
            lock.readLock().unlock();
        }

        super.goToFloor(floor);
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return String.format(
                    "PublicElevator{id='%s', floor=%d, weight=%.0f/%d, operational=%s, maxFloor=%d}",
                    elevatorId, currentFloor, currentWeight, MAX_WEIGHT, operational, MAX_FLOOR);
        } finally {
            lock.readLock().unlock();
        }
    }
}
