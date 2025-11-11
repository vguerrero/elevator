package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublicElevator extends Elevator{

    public static final int MAX_FLOOR = 50;
    public static final int MAX_WEIGHT = 1000;

    public PublicElevator(int totalFloors) {
        super("Public Elevator", MAX_WEIGHT, totalFloors);
    }

    public void goToFloor(int floor, boolean hasKeycard) {
        if ((floor == 0 || floor == MAX_FLOOR) && !hasKeycard) {
            log.warn("Access denied: keycard required for floor {}", floor);
            return;
        }
        super.goToFloor(floor);
    }
}
