package org.victor.audit;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Centralized audit logger for the elevator system
 */
@Slf4j
public class AuditLogger {

    private final List<AuditEvent> events = new CopyOnWriteArrayList<>();
    private final List<AuditListener> listeners = new CopyOnWriteArrayList<>();
    private static final int MAX_EVENTS = 10000; // Límit

    public void logEvent(AuditEvent event) {
        events.add(event);

        if (events.size() > MAX_EVENTS) {
            events.remove(0);
        }

        switch (event.getLevel()) {
            case INFO:
                log.info("AUDIT [{}] - User: {}, Elevator: {}, Description: {}",
                        event.getEventType(), event.getUserId(), event.getElevatorId(),
                        event.getDescription());
                break;
            case WARNING:
                log.warn("AUDIT [{}] - User: {}, Elevator: {}, Description: {}",
                        event.getEventType(), event.getUserId(), event.getElevatorId(),
                        event.getDescription());
                break;
            case ERROR:
            case CRITICAL:
                log.error("AUDIT [{}] - User: {}, Elevator: {}, Description: {}",
                        event.getEventType(), event.getUserId(), event.getElevatorId(),
                        event.getDescription());
                break;
        }

        // listeners
        notifyListeners(event);
    }

    public void logEvent(String eventId, AuditEvent.EventType type, String userId,
            String elevatorId, String description, AuditEvent.AuditLevel level) {
        AuditEvent event = new AuditEvent.Builder(eventId, type)
                .userId(userId)
                .elevatorId(elevatorId)
                .description(description)
                .level(level)
                .build();
        logEvent(event);
    }

    public List<AuditEvent> getEventsByType(AuditEvent.EventType type) {
        return new ArrayList<>(events.stream()
                .filter(e -> e.getEventType() == type)
                .toList());
    }

    public List<AuditEvent> getEventsByUser(String userId) {
        return new ArrayList<>(events.stream()
                .filter(e -> userId.equals(e.getUserId()))
                .toList());
    }

    public List<AuditEvent> getEventsByElevator(String elevatorId) {
        return new ArrayList<>(events.stream()
                .filter(e -> elevatorId.equals(e.getElevatorId()))
                .toList());
    }

    public List<AuditEvent> getCriticalEvents() {
        return new ArrayList<>(events.stream()
                .filter(e -> e.getLevel() == AuditEvent.AuditLevel.CRITICAL)
                .toList());
    }

    public List<AuditEvent> getAllEvents() {
        return new ArrayList<>(events);
    }

    public int getEventCount() {
        return events.size();
    }

    public void subscribe(AuditListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(AuditListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(AuditEvent event) {
        for (AuditListener listener : listeners) {
            try {
                listener.onAuditEvent(event);
            } catch (Exception e) {
                log.error("Error notifying audit listener", e);
            }
        }
    }

    public void clearEvents() {
        events.clear();
        log.info("Audit log cleared");
    }

    @FunctionalInterface
    public interface AuditListener {
        void onAuditEvent(AuditEvent event);
    }
}
