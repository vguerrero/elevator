package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FreightElevator extends Elevator{

    public static final int MAX_WEIGHT = 3000;

    public FreightElevator(int totalFloors) {
        super("Freight Elevator", MAX_WEIGHT, totalFloors);
    }
}
