package org.victor.audit;

import lombok.Getter;
import lombok.ToString;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Audit event in the elevator system
 */
@Getter
@ToString
public class AuditEvent {
    
    private final String eventId;
    private final EventType eventType;
    private final long timestamp;
    private final String userId;
    private final String elevatorId;
    private final String description;
    private final Map<String, Object> metadata;
    private final AuditLevel level;

    public enum EventType {
        USER_LOGIN,
        USER_LOGOUT,
        USER_AUTHENTICATION_FAILED,
        USER_LOCKED,
        ELEVATOR_CALL,
        ELEVATOR_FLOOR_CHANGE,
        WEIGHT_ADDED,
        WEIGHT_REMOVED,
        OVERLOAD_DETECTED,
        ELEVATOR_SHUTDOWN,
        MAINTENANCE_REQUEST,
        ACCESS_DENIED,
        KEYCARD_ASSIGNED,
        KEYCARD_REVOKED
    }

    public enum AuditLevel {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    private AuditEvent(Builder builder) {
        this.eventId = builder.eventId;
        this.eventType = builder.eventType;
        this.timestamp = builder.timestamp;
        this.userId = builder.userId;
        this.elevatorId = builder.elevatorId;
        this.description = builder.description;
        this.metadata = builder.metadata;
        this.level = builder.level;
    }

    public static class Builder {
        private final String eventId;
        private final EventType eventType;
        private long timestamp = System.currentTimeMillis();
        private String userId;
        private String elevatorId;
        private String description;
        private final Map<String, Object> metadata = new HashMap<>();
        private AuditLevel level = AuditLevel.INFO;

        public Builder(String eventId, EventType eventType) {
            this.eventId = eventId;
            this.eventType = eventType;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder elevatorId(String elevatorId) {
            this.elevatorId = elevatorId;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addMetadata(String key, Object value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder level(AuditLevel level) {
            this.level = level;
            return this;
        }

        public AuditEvent build() {
            return new AuditEvent(this);
        }
    }
}
