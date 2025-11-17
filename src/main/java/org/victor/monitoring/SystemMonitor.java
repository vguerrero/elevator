package org.victor.monitoring;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central system monitor for elevators
 */
@Slf4j
public class SystemMonitor {

    private final Map<String, ElevatorMetrics> metrics = new ConcurrentHashMap<>();
    private final List<HealthAlert> alerts = new ArrayList<>();
    private long lastHealthCheck = System.currentTimeMillis();

    /**
     * get and create metrics for an elevator
     */
    public ElevatorMetrics getOrCreateMetrics(String elevatorId) {
        return metrics.computeIfAbsent(elevatorId, ElevatorMetrics::new);
    }

    /**
     * get metrics by elevator ID
     */
    public ElevatorMetrics getMetrics(String elevatorId) {
        return metrics.get(elevatorId);
    }

    /**
     * getall metrics
     */
    public Collection<ElevatorMetrics> getAllMetrics() {
        return Collections.unmodifiableCollection(metrics.values());
    }

    /**
     * elevate a health alert
     */
    public void recordAlert(String elevatorId, AlertSeverity severity, String message) {
        HealthAlert alert = new HealthAlert(elevatorId, severity, message);
        alerts.add(alert);

        switch (severity) {
            case INFO:
                log.info("Health Alert [{}]: {}", elevatorId, message);
                break;
            case WARNING:
                log.warn("Health Alert [{}]: {}", elevatorId, message);
                break;
            case CRITICAL:
                log.error("Health Alert [{}]: {}", elevatorId, message);
                break;
        }
    }

    /**
     * system health check
     */
    public HealthStatus performHealthCheck() {
        lastHealthCheck = System.currentTimeMillis();

        HealthStatus status = new HealthStatus();
        status.totalElevators = metrics.size();
        status.checksTimestamp = lastHealthCheck;

        for (ElevatorMetrics m : metrics.values()) {
            if (m.getShutdownCount().get() > 0) {
                status.shutdownCount++;
            }
            if (m.getOverloadCount().get() > 0) {
                status.overloadedCount++;
            }
        }

        status.recentAlertsCount = (int) alerts.stream()
                .filter(a -> a.timestamp > lastHealthCheck - 5 * 60 * 1000) // últimos 5 minutos
                .count();

        return status;
    }

    /**
     * recent alerts retrieval
     */
    public List<HealthAlert> getRecentAlerts(long sinceMs) {
        long threshold = System.currentTimeMillis() - sinceMs;
        return new ArrayList<>(alerts.stream()
                .filter(a -> a.timestamp > threshold)
                .toList());
    }

    /**
     * critical alerts retrieval
     */
    public List<HealthAlert> getCriticalAlerts() {
        return new ArrayList<>(alerts.stream()
                .filter(a -> a.severity == AlertSeverity.CRITICAL)
                .toList());
    }

    /**
     * clear all monitoring data
     */
    public void reset() {
        metrics.clear();
        alerts.clear();
        log.info("System monitor reset");
    }

    public enum AlertSeverity {
        INFO, WARNING, CRITICAL
    }

    @lombok.Getter
    public static class HealthAlert {
        private final String elevatorId;
        private final AlertSeverity severity;
        private final String message;
        private final long timestamp;

        public HealthAlert(String elevatorId, AlertSeverity severity, String message) {
            this.elevatorId = elevatorId;
            this.severity = severity;
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("HealthAlert{elevator='%s', severity=%s, message='%s'}",
                    elevatorId, severity, message);
        }
    }

    // Centralized health status
    @lombok.Getter
    public static class HealthStatus {
        private int totalElevators;
        private int shutdownCount;
        private int overloadedCount;
        private int recentAlertsCount;
        private long checksTimestamp;

        public boolean isHealthy() {
            return shutdownCount == 0 && overloadedCount == 0;
        }

        @Override
        public String toString() {
            return String.format("HealthStatus{total=%d, shutdown=%d, overloaded=%d, alerts=%d, healthy=%s}",
                    totalElevators, shutdownCount, overloadedCount, recentAlertsCount, isHealthy());
        }
    }
}
