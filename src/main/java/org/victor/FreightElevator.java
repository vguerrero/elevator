package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FreightElevator extends Elevator{
    public FreightElevator(int totalFloors) {
        super("Freight Elevator", 3000, totalFloors);
    }
}
