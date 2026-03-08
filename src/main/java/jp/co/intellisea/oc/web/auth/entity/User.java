package jp.co.intellisea.oc.web.auth.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User entity for authentication system.
 * Maps to the 'users' table in the database.
 */
@Setter
@Getter
public class User {
    /**
     * Primary key - UUID format
     */
    private UUID id;

    /**
     * Unique username (3-20 characters, alphanumeric)
     */
    private String username;

    /**
     * Unique email address
     */
    private String email;

    /**
     * BCrypt hashed password
     */
    private String passwordHash;

    /**
     * Optional company name
     */
    private String company;

    /**
     * Account active status
     */
    private Boolean isActive;

    /**
     * Number of failed login attempts
     */
    private Integer failedLoginAttempts;

    /**
     * Account lock expiration time (null if not locked)
     */
    private LocalDateTime lockedUntil;

    /**
     * Account creation timestamp
     */
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    private LocalDateTime updatedAt;
}
