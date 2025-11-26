package org.victor;

import lombok.extern.slf4j.Slf4j;
import org.victor.audit.AuditLogger;
import org.victor.dispatch.ElevatorDispatcher;
import org.victor.dispatch.ElevatorRequest;
import org.victor.monitoring.SystemMonitor;
import org.victor.security.User;
import org.victor.security.UserManager;

@Slf4j
public class Demo {

    public static void main(String[] args) throws InterruptedException {
        log.info("========================================================");
        log.info("         ADVANCED ELEVATOR SYSTEM DEMONSTRATION        ");
        log.info("========================================================");

        // Initialize main components
        AuditLogger auditLogger = new AuditLogger();
        SystemMonitor systemMonitor = new SystemMonitor();
        UserManager userManager = new UserManager();
        ElevatorDispatcher dispatcher = new ElevatorDispatcher(auditLogger, systemMonitor, userManager);

        // ========== SECURITY AND AUTHENTICATION ==========
        log.info("\n========== SECURITY AND AUTHENTICATION ==========");

        try {
            // Register users
            userManager.registerUser("admin-001", "Administrator", "secureAdmin123!");
            userManager.registerUser("user-001", "John Doe", "password123!");
            userManager.registerUser("user-002", "Jane Smith", "password456!");

            log.info("Registered {} users successfully", userManager.getUserCount());

            // Assign roles
            userManager.assignRole("admin-001", "ADMIN");
            userManager.assignRole("admin-001", "MAINTENANCE");
            userManager.assignRole("user-001", "EMPLOYEE");
            userManager.assignRole("user-002", "EMPLOYEE");

            log.info("Assigned roles to users");

            // Assign keycards
            userManager.assignKeycard("admin-001", "KEYCARD-VIP-001");
            userManager.assignKeycard("admin-001", "KEYCARD-ROOF-001");
            userManager.assignKeycard("user-001", "KEYCARD-STD-001");

            log.info("Assigned keycards to users");

            // Authenticate user
            User authenticatedUser = userManager.authenticate("user-001", "password123!");
            log.info("Authentication successful for user: {}", authenticatedUser.getUsername());
            log.info("  User roles: {}", authenticatedUser.getRoles());
            log.info("  User keycards: {}", authenticatedUser.getKeycards());
            User authenticatedUser2 = userManager.authenticate("user-001", "passwor");
            log.info(authenticatedUser2.getUsername());

        } catch (Exception e) {
            log.error("Security module error: {}", e.getMessage());
        }

        // ========== ELEVATOR REGISTRATION ==========
        log.info("\n========== ELEVATOR REGISTRATION AND MANAGEMENT ==========");

        PublicElevator publicElevator = new PublicElevator(50);
        FreightElevator freightElevator = new FreightElevator(50);
        PublicElevator publicElevator2 = new PublicElevator(50);

        dispatcher.registerElevator(publicElevator);
        dispatcher.registerElevator(freightElevator);
        dispatcher.registerElevator(publicElevator2);

        // Move elevators to different floors for testing findNearestElevator
        log.info("Setting up elevator positions for nearest elevator test...");
        publicElevator.goToFloor(10, true); // Move to floor 10
        freightElevator.goToFloor(25); // Move to floor 25
        publicElevator2.goToFloor(45, true); // Move to floor 45

        log.info("Registered {} elevators", dispatcher.getStats().getTotalElevators());
        dispatcher.getAllElevators().forEach(e -> log.info("  Elevator: {}", e.getName()));

        // ========== FIND NEAREST ELEVATOR TEST ==========
        log.info("\n========== FIND NEAREST ELEVATOR TEST ==========");

        log.info("Current elevator positions:");
        dispatcher.getAllElevators().forEach(e ->
                log.info("  {}: Floor {}", e.getName(), e.getCurrentFloor()));

        try {
            // Test finding nearest elevator to floor 15
            Elevator nearestTo15 = dispatcher.findNearestElevator(15);
            log.info("Nearest elevator to floor 15: {} (currently on floor {})",
                    nearestTo15.getName(), nearestTo15.getCurrentFloor());

            // Test finding nearest elevator to floor 30
            Elevator nearestTo30 = dispatcher.findNearestElevator(30);
            log.info("Nearest elevator to floor 30: {} (currently on floor {})",
                    nearestTo30.getName(), nearestTo30.getCurrentFloor());

            // Test finding nearest elevator to floor 40
            Elevator nearestTo40 = dispatcher.findNearestElevator(40);
            log.info("Nearest elevator to floor 40: {} (currently on floor {})",
                    nearestTo40.getName(), nearestTo40.getCurrentFloor());

            // Test using the nearest elevator
            log.info("Moving nearest elevator to floor 15 using dispatcher.findNearestElevator()...");
            nearestTo15.goToFloor(15);
            log.info("Success: {} moved to floor 15", nearestTo15.getName());

        } catch (Exception e) {
            log.error("Error in findNearestElevator test: {}", e.getMessage());
        }

        // ========== SAFE OPERATIONS ==========
        log.info("\n========== SAFE OPERATIONS WITH ERROR HANDLING ==========");

        try {
            log.info("Adding 500 kg to public elevator...");
            publicElevator.addWeight(500);
            log.info("Success. Current weight: {}/{} kg",
                    (int) publicElevator.getCurrentWeight(),
                    (int) publicElevator.getMaxWeight());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        try {
            log.info("Moving public elevator to floor 25...");
            publicElevator.goToFloor(25);
            log.info("Success. Current floor: {}", publicElevator.getCurrentFloor());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // ========== OVERLOAD DETECTION ==========
        log.info("\n========== OVERLOAD DETECTION AND EMERGENCY SHUTDOWN ==========");

        try {
            log.info("Attempting to exceed weight limit (current: {} kg)...",
                    (int) publicElevator.getCurrentWeight());
            publicElevator.addWeight(700);
        } catch (Exception e) {
            log.error("Overload detected: {}", e.getMessage());
            log.warn("Elevator operational status: {}", publicElevator.isOperational());
        }

        // ========== ACCESS CONTROL ==========
        log.info("\n========== ACCESS CONTROL (RESTRICTED FLOORS) ==========");

        try {
            log.info("Attempting to access floor 50 WITHOUT keycard...");
            publicElevator2.goToFloor(50, false);
        } catch (Exception e) {
            log.error("Access denied: {}", e.getMessage());
        }

        try {
            log.info("Attempting to access floor 50 WITH keycard...");
            publicElevator2.goToFloor(50, true);
            log.info("Access granted. Current floor: {}", publicElevator2.getCurrentFloor());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // ========== FREIGHT ELEVATOR OPERATIONS ==========
        log.info("\n========== FREIGHT ELEVATOR OPERATIONS ==========");

        try {
            log.info("Adding 2500 kg to freight elevator...");
            freightElevator.addWeight(2500);
            log.info("Success. Current weight: {}/{} kg",
                    (int) freightElevator.getCurrentWeight(),
                    (int) freightElevator.getMaxWeight());

            log.info("Moving freight elevator to basement (floor 0)...");
            freightElevator.goToFloor(0);
            log.info("Success. Current floor: {}", freightElevator.getCurrentFloor());
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }

        // ========== CONCURRENT OPERATIONS ==========
        log.info("\n========== CONCURRENT OPERATIONS (MULTITHREADING) ==========");

        PublicElevator publicElevator3 = new PublicElevator(50);
        dispatcher.registerElevator(publicElevator3);

        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    log.info("Thread {} - Adding weight to elevator...", threadId);
                    publicElevator3.addWeight(100 + threadId * 20);

                    Thread.sleep(100 * (threadId + 1));

                    log.info("Thread {} - Moving elevator to floor {}", threadId, 10 + threadId * 8);
                    publicElevator3.goToFloor(10 + threadId * 8);

                    log.info("Thread {} - Removing weight...", threadId);
                    publicElevator3.removeWeight(50);

                } catch (Exception e) {
                    log.error("Thread {} - Error: {}", threadId, e.getMessage());
                }
            });
            threads[i].start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            t.join();
        }

        log.info("All concurrent operations completed");
        log.info("Final elevator state: {}", publicElevator3);

        // ========== ELEVATOR REQUESTS AND PROCESSING ==========
        log.info("\n========== ELEVATOR REQUESTS AND PROCESSING ==========");

        log.info("Creating elevator requests for dispatcher processing...");

        // Employee with keycard requesting public elevator to floor 35 (access granted)
        dispatcher.requestElevator(new ElevatorRequest("user-001", 35, ElevatorRequest.ElevatorType.PUBLIC, userManager.hasKeycard("user-001", "KEYCARD-STD-001")));

        // Employee without keycard requesting public elevator to basement 0 (access denied due to no keycard)
        dispatcher.requestElevator(new ElevatorRequest("user-002", 0, ElevatorRequest.ElevatorType.PUBLIC, userManager.hasKeycard("user-002", "KEYCARD-STD-001")));

        // Admin with roof keycard requesting public elevator to floor 50 (access granted)
        dispatcher.requestElevator(new ElevatorRequest("admin-001", 50, ElevatorRequest.ElevatorType.PUBLIC, userManager.hasKeycard("admin-001", "KEYCARD-ROOF-001")));

        // Employee requesting freight elevator to floor 20 (freight has no access restrictions)
        dispatcher.requestElevator(new ElevatorRequest("user-001", 20, ElevatorRequest.ElevatorType.FREIGHT, false));

        log.info("Processing queue of {} requests...", dispatcher.getStats().getPendingRequests());

        dispatcher.processRequests();

        log.info("Requests processed successfully. Elevator positions after processing:");
        dispatcher.getAllElevators().forEach(e ->
                log.info("  {}: Floor {}", e.getName(), e.getCurrentFloor()));

        // ========== AUDIT LOG REVIEW ==========
        log.info("\n========== AUDIT LOG ==========");

        int auditCount = auditLogger.getAllEvents().size();
        log.info("Total audit events: {}", auditCount);

        java.util.List<org.victor.audit.AuditEvent> criticalEvents = auditLogger.getCriticalEvents();
        log.info("Critical events: {}", criticalEvents.size());
        if (!criticalEvents.isEmpty()) {
            log.info("First critical event: {}", criticalEvents.get(0).getDescription());
        }

        // ========== SYSTEM METRICS ==========
        log.info("\n========== SYSTEM METRICS ==========");

        SystemMonitor.HealthStatus healthStatus = systemMonitor.performHealthCheck();
        log.info("System health status: {}", healthStatus);

        log.info("Dispatcher statistics: {}", dispatcher.getStats());

        for (org.victor.monitoring.ElevatorMetrics metrics : systemMonitor.getAllMetrics()) {
            log.info("Elevator metrics: {}", metrics);
        }

        log.info("\n========================================================");
        log.info("         DEMONSTRATION COMPLETED SUCCESSFULLY          ");


    }
}
