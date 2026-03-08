package jp.co.intellisea.oc.web.auth.common;

/**
 * Authentication-related constant definitions.
 */
public final class AuthConstants {

    /**
     * Maximum failed login attempts before account lockout.
     */
    public static final int MAX_FAILED_LOGIN_ATTEMPTS = 5;

    /**
     * Account lock duration in minutes.
     */
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 15;

    /**
     * Rate limit for login attempts: 5 per 15 minutes.
     */
    public static final int LOGIN_RATE_LIMIT_MAX = 5;
    public static final int LOGIN_RATE_LIMIT_WINDOW_MINUTES = 15;

    /**
     * Rate limit for registration: 3 per hour.
     */
    public static final int REGISTER_RATE_LIMIT_MAX = 3;
    public static final int REGISTER_RATE_LIMIT_WINDOW_MINUTES = 60;

    /**
     * Rate limit for refresh token: 10 per minute.
     */
    public static final int REFRESH_RATE_LIMIT_MAX = 10;
    public static final int REFRESH_RATE_LIMIT_WINDOW_MINUTES = 1;

    /**
     * Cookie name for refresh token.
     */
    public static final String REFRESH_TOKEN_COOKIE_NAME = "Refresh-Token";

    /**
     * Authorization header prefix.
     */
    public static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";

    /**
     * Minimum password length.
     */
    public static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Minimum username length.
     */
    public static final int MIN_USERNAME_LENGTH = 3;

    /**
     * Maximum username length.
     */
    public static final int MAX_USERNAME_LENGTH = 20;

    /**
     * Maximum password length.
     */
    public static final int MAX_PASSWORD_LENGTH = 100;

    private AuthConstants() {
        // Utility class, prevent instantiation
    }
}
