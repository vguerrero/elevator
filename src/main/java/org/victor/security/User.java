package org.victor.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

/**
 * User entity representing system users
 */
@Getter
@Setter
public class User {
    
    private final String userId;
    private final String username;
    private String passwordHash;
    private final Set<String> roles;
    private final Set<String> keycards;
    private final long createdAt;
    private long lastAccessAt;
    private boolean locked;
    private int failedLoginAttempts;
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_MS = 15 * 60 * 1000; // 15 min
    
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = encoder.encode(password);
        this.roles = new HashSet<>();
        this.keycards = new HashSet<>();
        this.createdAt = System.currentTimeMillis();
        this.lastAccessAt = System.currentTimeMillis();
        this.locked = false;
        this.failedLoginAttempts = 0;
    }

    /**
     * password validation method
     */
    public boolean validatePassword(String rawPassword) {
        if (isLocked()) {
            throw new SecurityException("User account is locked");
        }

        boolean isValid = encoder.matches(rawPassword, passwordHash);

        if (isValid) {
            failedLoginAttempts = 0;
            lastAccessAt = System.currentTimeMillis();
        } else {
            failedLoginAttempts++;
            if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
                locked = true;
            }
        }

        return isValid;
    }

    /**
     * account unlock check
     */
    public void unlockIfExpired() {
        if (locked && (System.currentTimeMillis() - lastAccessAt > LOCKOUT_DURATION_MS)) {
            locked = false;
            failedLoginAttempts = 0;
        }
    }

    /**
     * verify if account is locked
     */
    public boolean isLocked() {
        unlockIfExpired();
        return locked;
    }

    /**
     * add role to user
     */
    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * user role check
     */
    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    /**
     * give keycard to user
     */
    public void assignKeycard(String keycard) {
        keycards.add(keycard);
    }

    /**
     * verify if user has keycard
     */
    public boolean hasKeycard(String keycard) {
        return keycards.contains(keycard);
    }

    /**
     * revoke keycard from user
     */
    public void revokeKeycard(String keycard) {
        keycards.remove(keycard);
    }

    /**
     * get string representation of user (without sensitive data)
     */
    @Override
    public String toString() {
        return String.format("User{userId='%s', username='%s', roles=%s, locked=%s}",
                userId, username, roles, locked);
    }
}
