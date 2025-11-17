package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FreightElevator extends Elevator {

    public static final int MAX_WEIGHT = 3000;

    public FreightElevator(int totalFloors) {
        super("Freight Elevator", MAX_WEIGHT, totalFloors);
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return String.format(
                    "FreightElevator{id='%s', floor=%d, weight=%.0f/%d, operational=%s}",
                    elevatorId, currentFloor, currentWeight, MAX_WEIGHT, operational);
        } finally {
            lock.readLock().unlock();
        }
    }
}
