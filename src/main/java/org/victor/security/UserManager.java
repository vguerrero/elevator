package org.victor.security;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * user management system with thread-safe authentication and authorization
 */
@Slf4j
public class UserManager {

    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, Long> loginAttempts = new ConcurrentHashMap<>();
    private static final long LOGIN_TIMEOUT_MS = 5 * 60 * 1000; // 5 min

    /**
     * User registration
     */
    public synchronized User registerUser(String userId, String username, String password) {
        if (users.containsKey(userId)) {
            throw new SecurityException("User already exists: " + userId);
        }

        User user = new User(userId, username, password);
        users.put(userId, user);
        log.info("User registered: {}", userId);
        return user;
    }

    /**
     * user authentication
     */
    public synchronized User authenticate(String userId, String password) {
        User user = users.get(userId);
        if (user == null) {
            log.warn("Authentication failed: user not found: {}", userId);
            throw new SecurityException("User not found: " + userId);
        }

        if (!user.validatePassword(password)) {
            log.warn("Authentication failed: invalid password for user: {}", userId);
            throw new SecurityException("Invalid password for user: " + userId);
        }

        log.info("User authenticated: {}", userId);
        return user;
    }

    /**
     * get by user ID
     */
    public User getUser(String userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new SecurityException("User not found: " + userId);
        }
        return user;
    }

    /**
     * role assignment
     */
    public synchronized void assignRole(String userId, String role) {
        User user = getUser(userId);
        user.addRole(role);
        log.info("Role assigned: {} -> {}", userId, role);
    }

    /**
     * give a keycard to a user
     */
    public synchronized void assignKeycard(String userId, String keycard) {
        User user = getUser(userId);
        user.assignKeycard(keycard);
        log.info("Keycard assigned: {} -> {}", userId, keycard);
    }

    /**
     * cancel a keycard from a user
     */
    public synchronized void revokeKeycard(String userId, String keycard) {
        User user = getUser(userId);
        user.revokeKeycard(keycard);
        log.info("Keycard revoked: {} <- {}", userId, keycard);
    }

    /**
     * verify if a user has a specific role
     */
    public boolean hasRole(String userId, String role) {
        User user = users.get(userId);
        return user != null && user.hasRole(role);
    }

    /**
     * verify if a user has a specific keycard
     */
    public boolean hasKeycard(String userId, String keycard) {
        User user = users.get(userId);
        return user != null && user.hasKeycard(keycard);
    }

    /**
     * collection of all users
     */
    public Collection<User> getAllUsers() {
        return Collections.unmodifiableCollection(users.values());
    }

    /**
     * delete a user
     */
    public synchronized void removeUser(String userId) {
        users.remove(userId);
        log.info("User removed: {}", userId);
    }

    /**
     * get user count
     */
    public int getUserCount() {
        return users.size();
    }
}
