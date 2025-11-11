package org.victor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class Elevator {


    protected String name;
    protected double maxWeight;
    protected double currentWeight;
    protected int currentFloor;
    protected int totalFloors;
    protected boolean operational;

    public Elevator(String name, double maxWeight, int totalFloors) {
        this.name = name;
        this.maxWeight = maxWeight;
        this.totalFloors = totalFloors;
        this.currentFloor = 1;
        this.operational = true;
    }

    public void addWeight(double weight) {
        if (weight < 0) {
            log.warn("Negative weight ignored: {}", weight);
            return;
        }

        if (!operational) {
            shutDownNotification();
            return;
        }

        currentWeight += weight;
        if (currentWeight > maxWeight) {
            triggerAlarm();
        }
    }

    private void shutDownNotification() {
        log.warn("{} is shut down!", name);
    }

    public void removeWeight(double weight) {
        currentWeight = Math.max(0, currentWeight - weight);
    }

    public void goToFloor(int floor) {
        if (!operational) {
            shutDownNotification();
            return;
        }
        if (floor < 0 || floor > totalFloors) {
            log.warn("Invalid floor.");
            return;
        }
        currentFloor = floor;
        System.out.println(name + " moved to floor " + currentFloor + ".");
    }

    protected void triggerAlarm() {
        log.error("Alarm! {} exceeded weight limit ({} kg).", name, maxWeight);
        shutdown();
    }

    protected void shutdown() {
        operational = false;
        log.warn("{} has been shut down for safety.", name);
    }

}
