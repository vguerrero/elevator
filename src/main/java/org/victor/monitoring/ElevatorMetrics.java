package org.victor.monitoring;

import lombok.Getter;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Elevator metrics tracking class
 */
@Getter
public class ElevatorMetrics {
    
    private final String elevatorId;
    private final AtomicLong totalMoves = new AtomicLong(0);
    private final AtomicLong totalWeightAdded = new AtomicLong(0);
    private final AtomicLong totalWeightRemoved = new AtomicLong(0);
    private final AtomicLong overloadCount = new AtomicLong(0);
    private final AtomicLong shutdownCount = new AtomicLong(0);
    private final long createdAt = System.currentTimeMillis();
    
    private long lastMoveTime = 0;
    private long totalOperatingTime = 0;
    private int currentPassengers = 0;

    public ElevatorMetrics(String elevatorId) {
        this.elevatorId = elevatorId;
    }

    public void recordMove() {
        totalMoves.incrementAndGet();
        lastMoveTime = System.currentTimeMillis();
    }

    public void recordWeightAdded(long weight) {
        totalWeightAdded.addAndGet(weight);
    }

    public void recordWeightRemoved(long weight) {
        totalWeightRemoved.addAndGet(weight);
    }

    public void recordOverload() {
        overloadCount.incrementAndGet();
    }

    public void recordShutdown() {
        shutdownCount.incrementAndGet();
    }

    public void addPassenger() {
        currentPassengers++;
    }

    public void removePassenger() {
        currentPassengers = Math.max(0, currentPassengers - 1);
    }

    public long getUptimeMs() {
        return System.currentTimeMillis() - createdAt;
    }

    public double getAverageMoveDuration() {
        return totalMoves.get() > 0 ? (double) getUptimeMs() / totalMoves.get() : 0;
    }

    @Override
    public String toString() {
        return String.format(
                "ElevatorMetrics{id='%s', moves=%d, overloads=%d, shutdowns=%d, passengers=%d, uptime=%dms}",
                elevatorId, totalMoves.get(), overloadCount.get(), shutdownCount.get(),
                currentPassengers, getUptimeMs()
        );
    }
}
