package jp.co.intellisea.oc.web.auth.dao;

import jp.co.intellisea.oc.web.auth.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MyBatis Mapper interface for User entity.
 * Provides database operations for user authentication.
 */
@Mapper
public interface UserMapper {

    /**
     * Insert a new user record.
     *
     * @param user the user to insert
     * @return number of rows affected
     */
    int insert(User user);

    /**
     * Insert a new user record with selective fields.
     *
     * @param user the user to insert
     * @return number of rows affected
     */
    int insertSelective(User user);

    /**
     * Select a user by primary key (UUID).
     *
     * @param id the user ID
     * @return the user entity, or null if not found
     */
    User selectByPrimaryKey(@Param("id") UUID id);

    /**
     * Select a user by username.
     *
     * @param username the username
     * @return the user entity, or null if not found
     */
    User selectByUsername(@Param("username") String username);

    /**
     * Select a user by email.
     *
     * @param email the email address
     * @return the user entity, or null if not found
     */
    User selectByEmail(@Param("email") String email);

    /**
     * Check if username exists.
     *
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    boolean existsByUsername(@Param("username") String username);

    /**
     * Check if email exists.
     *
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(@Param("email") String email);

    /**
     * Update user by primary key with selective fields.
     *
     * @param user the user with updated values
     * @return number of rows affected
     */
    int updateByPrimaryKeySelective(User user);

    /**
     * Update user by primary key.
     *
     * @param user the user with updated values
     * @return number of rows affected
     */
    int updateByPrimaryKey(User user);

    /**
     * Increment failed login attempts.
     *
     * @param id the user ID
     * @param attempts the number of attempts to add
     * @param lockedUntil the lock expiration time
     * @return number of rows affected
     */
    int incrementFailedLoginAttempts(
            @Param("id") UUID id,
            @Param("attempts") int attempts,
            @Param("lockedUntil") LocalDateTime lockedUntil);

    /**
     * Reset failed login attempts after successful login.
     *
     * @param id the user ID
     * @return number of rows affected
     */
    int resetFailedLoginAttempts(@Param("id") UUID id);
}
