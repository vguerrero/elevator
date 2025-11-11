package org.victor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo
{
    public static void main( String[] args )
    {

        log.info("=== Public Elevator example ===");
        PublicElevator publicElevator = new PublicElevator(50);
        publicElevator.addWeight(800);
        publicElevator.goToFloor(10, false);
        publicElevator.goToFloor(50, false);
        publicElevator.goToFloor(50, true);
        publicElevator.addWeight(300);

        log.info("\n=== Freight Elevator example===");
        FreightElevator freightElevator = new FreightElevator(50);
        freightElevator.addWeight(2500);
        freightElevator.goToFloor(0);
        freightElevator.addWeight(600);

    }
}
