package jp.co.intellisea.oc.web.auth.config;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 * Password encoder using BCrypt algorithm.
 * BCrypt is a password hashing function designed to be slow and computationally expensive,
 * making it resistant to brute-force attacks.
 */
@Component
public class PasswordEncoder {

    /**
     * BCrypt work factor (log rounds). Higher values increase security but also computation time.
     * 12 is a good balance for most applications as of 2024.
     */
    private static final int BCRYPT_WORK_FACTOR = 12;

    /**
     * Hashes a plain text password using BCrypt.
     * Automatically generates a random salt.
     *
     * @param plainPassword the plain text password to hash
     * @return the BCrypt hashed password
     * @throws IllegalArgumentException if password is null or empty
     */
    public String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_WORK_FACTOR));
    }

    /**
     * Verifies a plain text password against a BCrypt hashed password.
     *
     * @param plainPassword the plain text password to verify
     * @param hashedPassword the BCrypt hashed password to compare against
     * @return true if the password matches, false otherwise
     * @throws IllegalArgumentException if either parameter is null or empty
     */
    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Plain password cannot be null or empty");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("Hashed password cannot be null or empty");
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Handle invalid hash format
            return false;
        }
    }
}
