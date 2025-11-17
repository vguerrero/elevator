package org.victor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.victor.audit.AuditEvent;
import org.victor.audit.AuditLogger;
import org.victor.exception.*;
import org.victor.monitoring.ElevatorMetrics;
import org.victor.monitoring.SystemMonitor;

import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Getter
public abstract class Elevator {

    protected final String elevatorId;
    protected final String name;
    protected final double maxWeight;
    protected final int totalFloors;

    protected double currentWeight;
    protected int currentFloor;
    protected boolean operational;

    // thread-safe synchronization
    protected final ReadWriteLock lock = new ReentrantReadWriteLock();

    // audit and logging
    protected AuditLogger auditLogger;
    protected ElevatorMetrics metrics;
    protected SystemMonitor systemMonitor;

    public Elevator(String name, double maxWeight, int totalFloors) {
        this.elevatorId = UUID.randomUUID().toString();
        this.name = name;
        this.maxWeight = maxWeight;
        this.totalFloors = totalFloors;
        this.currentFloor = 1;
        this.currentWeight = 0;
        this.operational = true;
    }

    // inject audit logger
    public void setAuditLogger(AuditLogger auditLogger) {
        this.auditLogger = auditLogger;
    }

    // metric injection
    public void setMetrics(ElevatorMetrics metrics) {
        this.metrics = metrics;
    }

    // monitor injection
    public void setSystemMonitor(SystemMonitor systemMonitor) {
        this.systemMonitor = systemMonitor;
    }

    // add weight with validation and synchronization
    public void addWeight(double weight) {
        if (weight < 0) {
            log.warn("Negative weight ignored: {}", weight);
            return;
        }

        lock.writeLock().lock();
        try {
            if (!operational) {
                logAuditEvent(AuditEvent.EventType.ELEVATOR_SHUTDOWN,
                        "Cannot add weight to non-operational elevator");
                throw new ElevatorNotOperationalException(name);
            }

            double newWeight = currentWeight + weight;
            if (newWeight > maxWeight) {
                logAuditEvent(AuditEvent.EventType.OVERLOAD_DETECTED,
                        String.format("Overload: %.0f kg exceeds limit of %.0f kg",
                                newWeight, maxWeight),
                        AuditEvent.AuditLevel.CRITICAL);

                if (metrics != null) {
                    metrics.recordOverload();
                }
                if (systemMonitor != null) {
                    systemMonitor.recordAlert(elevatorId,
                            SystemMonitor.AlertSeverity.CRITICAL,
                            "Overload detected");
                }

                triggerAlarm(newWeight);
                throw new ElevatorOverloadException(name, newWeight, maxWeight);
            }

            currentWeight = newWeight;

            if (metrics != null) {
                metrics.recordWeightAdded((long) weight);
            }

            logAuditEvent(AuditEvent.EventType.WEIGHT_ADDED,
                    String.format("Weight added: %.0f kg, total: %.0f kg", weight, currentWeight));

        } finally {
            lock.writeLock().unlock();
        }
    }

    // remove weight with synchronization
    public void removeWeight(double weight) {
        lock.writeLock().lock();
        try {
            double newWeight = Math.max(0, currentWeight - weight);
            currentWeight = newWeight;

            if (metrics != null) {
                metrics.recordWeightRemoved((long) weight);
            }

            logAuditEvent(AuditEvent.EventType.WEIGHT_REMOVED,
                    String.format("Weight removed: %.0f kg, total: %.0f kg", weight, currentWeight));

        } finally {
            lock.writeLock().unlock();
        }
    }

    // move to specified floor with validation and synchronization
    public void goToFloor(int floor) {
        lock.writeLock().lock();
        try {
            if (!operational) {
                logAuditEvent(AuditEvent.EventType.ELEVATOR_SHUTDOWN,
                        "Cannot move non-operational elevator");
                throw new ElevatorNotOperationalException(name);
            }

            if (!isValidFloor(floor)) {
                logAuditEvent(AuditEvent.EventType.ACCESS_DENIED,
                        String.format("Invalid floor requested: %d", floor),
                        AuditEvent.AuditLevel.WARNING);
                throw new InvalidFloorException(floor, 0, totalFloors);
            }

            int previousFloor = currentFloor;
            currentFloor = floor;

            if (metrics != null) {
                metrics.recordMove();
            }

            log.info("{} moved from floor {} to floor {}", name, previousFloor, currentFloor);
            logAuditEvent(AuditEvent.EventType.ELEVATOR_FLOOR_CHANGE,
                    String.format("Moved from floor %d to floor %d", previousFloor, floor));

        } finally {
            lock.writeLock().unlock();
        }
    }

    // if is a valid floor
    protected boolean isValidFloor(int floor) {
        return floor >= 0 && floor <= totalFloors;
    }

    // trigger alarm
    protected void triggerAlarm() {
        lock.writeLock().lock();
        try {
            log.error("ALARM! {} exceeded weight limit ({} kg).", name, maxWeight);
            shutdown();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // trigger alarm with current weight
    protected void triggerAlarm(double currentWeight) {
        lock.writeLock().lock();
        try {
            log.error("ALARM! {} overloaded: {:.0f} kg exceeds limit of {:.0f} kg",
                    name, (long) currentWeight, (long) maxWeight);
            shutdown();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // shutdown elevator
    protected void shutdown() {
        lock.writeLock().lock();
        try {
            operational = false;
            log.warn("{} has been shut down for safety.", name);

            if (metrics != null) {
                metrics.recordShutdown();
            }

            logAuditEvent(AuditEvent.EventType.ELEVATOR_SHUTDOWN,
                    "Elevator emergency shutdown",
                    AuditEvent.AuditLevel.CRITICAL);

        } finally {
            lock.writeLock().unlock();
        }
    }

    // Audit and logging by default info level
    protected void logAuditEvent(AuditEvent.EventType eventType, String description) {
        logAuditEvent(eventType, description, AuditEvent.AuditLevel.INFO);
    }

    // log level audit event
    protected void logAuditEvent(AuditEvent.EventType eventType, String description,
            AuditEvent.AuditLevel level) {
        if (auditLogger != null) {
            AuditEvent event = new AuditEvent.Builder(UUID.randomUUID().toString(), eventType)
                    .elevatorId(elevatorId)
                    .description(description)
                    .level(level)
                    .build();
            auditLogger.logEvent(event);
        }
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return String.format("Elevator{id='%s', name='%s', floor=%d, weight=%.0f/%.0f, operational=%s}",
                    elevatorId, name, currentFloor, currentWeight, maxWeight, operational);
        } finally {
            lock.readLock().unlock();
        }
    }
}
