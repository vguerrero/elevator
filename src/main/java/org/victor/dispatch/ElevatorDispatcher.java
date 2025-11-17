package org.victor.dispatch;

import lombok.extern.slf4j.Slf4j;
import org.victor.Elevator;
import org.victor.FreightElevator;
import org.victor.PublicElevator;
import org.victor.audit.AuditLogger;
import org.victor.monitoring.ElevatorMetrics;
import org.victor.monitoring.SystemMonitor;
import org.victor.security.UserManager;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Dispatcher system to manage multiple elevators in a thread-safe manner
 */
@Slf4j
public class ElevatorDispatcher {

    private final Map<String, Elevator> elevators = new ConcurrentHashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final AuditLogger auditLogger;
    private final SystemMonitor systemMonitor;
    private final UserManager userManager;
    private final Queue<ElevatorRequest> requestQueue = new java.util.concurrent.ConcurrentLinkedQueue<>();

    private static final int DISPATCH_TIMEOUT_MS = 5000;

    public ElevatorDispatcher(AuditLogger auditLogger, SystemMonitor systemMonitor, UserManager userManager) {
        this.auditLogger = auditLogger;
        this.systemMonitor = systemMonitor;
        this.userManager = userManager;
        log.info("ElevatorDispatcher initialized");
    }

    /**
     * elevator registration
     */
    public void registerElevator(Elevator elevator) {
        lock.writeLock().lock();
        try {
            elevators.put(elevator.getElevatorId(), elevator);

            ElevatorMetrics metrics = systemMonitor.getOrCreateMetrics(elevator.getElevatorId());
            elevator.setAuditLogger(auditLogger);
            elevator.setMetrics(metrics);
            elevator.setSystemMonitor(systemMonitor);

            log.info("Elevator registered: {} (ID: {})", elevator.getName(), elevator.getElevatorId());
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * elevator unregistration
     */
    public void unregisterElevator(String elevatorId) {
        lock.writeLock().lock();
        try {
            elevators.remove(elevatorId);
            log.info("Elevator unregistered: {}", elevatorId);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * get elevator by ID
     */
    public Elevator getElevator(String elevatorId) {
        lock.readLock().lock();
        try {
            Elevator elevator = elevators.get(elevatorId);
            if (elevator == null) {
                throw new IllegalArgumentException("Elevator not found: " + elevatorId);
            }
            return elevator;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * getall elevators
     */
    public Collection<Elevator> getAllElevators() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(elevators.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get available public elevators
     */
    public List<PublicElevator> getAvailablePublicElevators() {
        lock.readLock().lock();
        try {
            return elevators.values().stream()
                    .filter(e -> e instanceof PublicElevator && e.isOperational())
                    .map(e -> (PublicElevator) e)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get available freight elevators
     */
    public List<FreightElevator> getAvailableFreightElevators() {
        lock.readLock().lock();
        try {
            return elevators.values().stream()
                    .filter(e -> e instanceof FreightElevator && e.isOperational())
                    .map(e -> (FreightElevator) e)
                    .toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get closest elevator
     */
    public Elevator findNearestElevator(int targetFloor) {
        lock.readLock().lock();
        try {
            return elevators.values().stream()
                    .filter(Elevator::isOperational)
                    .min((e1, e2) -> Integer.compare(
                            Math.abs(e1.getCurrentFloor() - targetFloor),
                            Math.abs(e2.getCurrentFloor() - targetFloor)))
                    .orElseThrow(() -> new IllegalStateException("No operational elevators available"));
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * sends an elevator request to queue
     */
    public void requestElevator(ElevatorRequest request) {
        requestQueue.add(request);
        log.debug("Elevator request queued: {} to floor {}", request.getUserId(), request.getTargetFloor());
    }

    /**
     * process queued requests
     */
    public void processRequests() {
        while (!requestQueue.isEmpty()) {
            ElevatorRequest request = requestQueue.poll();
            if (request != null) {
                try {
                    dispatch(request);
                } catch (Exception e) {
                    log.error("Error processing request: {}", request, e);
                }
            }
        }
    }

    /**
     * Dispatches an elevator request to an elevator
     */
    private void dispatch(ElevatorRequest request) {
        Elevator elevator;

        if (request.getElevatorType() == ElevatorRequest.ElevatorType.PUBLIC) {
            List<PublicElevator> available = getAvailablePublicElevators();
            if (available.isEmpty()) {
                throw new IllegalStateException("No public elevators available");
            }
            elevator = available.get(0);
        } else {
            List<FreightElevator> available = getAvailableFreightElevators();
            if (available.isEmpty()) {
                throw new IllegalStateException("No freight elevators available");
            }
            elevator = available.get(0);
        }

        try {
            if (elevator instanceof PublicElevator) {
                ((PublicElevator) elevator).goToFloor(request.getTargetFloor(), request.hasKeycard());
            } else {
                elevator.goToFloor(request.getTargetFloor());
            }
            log.info("Dispatched user {} to elevator {}", request.getUserId(), elevator.getElevatorId());
        } catch (Exception e) {
            log.error("Failed to dispatch request: {}", request, e);
            throw e;
        }
    }

    /**
     * get dispatcher statistics
     */
    public DispatcherStats getStats() {
        lock.readLock().lock();
        try {
            DispatcherStats stats = new DispatcherStats();
            stats.totalElevators = elevators.size();
            stats.operationalElevators = (int) elevators.values().stream()
                    .filter(Elevator::isOperational)
                    .count();
            stats.publicElevators = (int) elevators.values().stream()
                    .filter(e -> e instanceof PublicElevator)
                    .count();
            stats.freightElevators = (int) elevators.values().stream()
                    .filter(e -> e instanceof FreightElevator)
                    .count();
            stats.pendingRequests = requestQueue.size();
            return stats;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * reset dispatcher
     */
    public void reset() {
        lock.writeLock().lock();
        try {
            elevators.clear();
            requestQueue.clear();
            log.info("ElevatorDispatcher reset");
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * dispatcher statistics class
     */
    @lombok.Getter
    public static class DispatcherStats {
        private int totalElevators;
        private int operationalElevators;
        private int publicElevators;
        private int freightElevators;
        private int pendingRequests;

        @Override
        public String toString() {
            return String.format(
                    "DispatcherStats{total=%d, operational=%d, public=%d, freight=%d, pending=%d}",
                    totalElevators, operationalElevators, publicElevators, freightElevators, pendingRequests);
        }
    }
}
