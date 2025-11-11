package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublicElevator extends Elevator{
    public PublicElevator(int totalFloors) {
        super("Public Elevator", 1000, totalFloors);
    }

    public void goToFloor(int floor, boolean hasKeycard) {
        if ((floor == 0 || floor == 50) && !hasKeycard) {
            log.warn("Access denied: keycard required for floor {}", floor);
            return;
        }
        super.goToFloor(floor);
    }
}
