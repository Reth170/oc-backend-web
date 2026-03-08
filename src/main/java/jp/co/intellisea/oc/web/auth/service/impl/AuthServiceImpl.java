package jp.co.intellisea.oc.web.auth.service.impl;

import jp.co.intellisea.oc.web.auth.common.AuthConstants;
import jp.co.intellisea.oc.web.auth.config.JwtTokenProvider;
import jp.co.intellisea.oc.web.auth.config.PasswordEncoder;
import jp.co.intellisea.oc.web.auth.config.RateLimiter;
import jp.co.intellisea.oc.web.auth.dao.UserMapper;
import jp.co.intellisea.oc.web.auth.dto.AuthResponse;
import jp.co.intellisea.oc.web.auth.dto.LoginRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterResponse;
import jp.co.intellisea.oc.web.auth.dto.UserResponse;
import jp.co.intellisea.oc.web.auth.entity.User;
import jp.co.intellisea.oc.web.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Implementation of the Authentication Service.
 * Handles user registration, login, token refresh, and logout.
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RateLimiter rateLimiter;

    /**
     * Register a new user with validation.
     *
     * @param request the registration request
     * @return the registration response
     * @throws IllegalArgumentException if username or email already exists
     */
    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check rate limit for registration
        String rateLimitKey = "register:" + request.getUsername();
        if (!rateLimiter.isAllowed(rateLimitKey,
                AuthConstants.REGISTER_RATE_LIMIT_MAX,
                AuthConstants.REGISTER_RATE_LIMIT_WINDOW_MINUTES)) {
            throw new IllegalStateException("Too many registration attempts. Please try again later.");
        }

        // Check if username already exists
        if (userMapper.existsByUsername(request.getUsername())) {
            log.warn("Registration failed: username {} already exists", request.getUsername());
            throw new IllegalArgumentException("Username already exists");
        }

        // Check if email already exists
        if (userMapper.existsByEmail(request.getEmail())) {
            log.warn("Registration failed: email {} already exists", request.getEmail());
            throw new IllegalArgumentException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.hashPassword(request.getPassword()));
        user.setCompany(request.getCompany());
        user.setIsActive(true);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Insert user into database
        userMapper.insertSelective(user);

        log.info("User registered successfully: {}", user.getId());

        return RegisterResponse.of(
                user.getId().toString(),
                user.getUsername()
        );
    }

    /**
     * Authenticate user and return tokens.
     * Uses generic error messages to prevent username enumeration.
     *
     * @param request the login request
     * @param ipAddress the client IP address
     * @return the authentication response
     * @throws IllegalArgumentException if credentials are invalid
     * @throws IllegalStateException if account is locked
     */
    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress) {
        log.info("Login attempt for user: {}", request.getUsername());

        // Check rate limit by IP address
        String rateLimitKey = "login:" + ipAddress;
        if (!rateLimiter.isAllowed(rateLimitKey,
                AuthConstants.LOGIN_RATE_LIMIT_MAX,
                AuthConstants.LOGIN_RATE_LIMIT_WINDOW_MINUTES)) {
            long retryAfter = rateLimiter.getRetryAfterSeconds(rateLimitKey,
                    AuthConstants.LOGIN_RATE_LIMIT_WINDOW_MINUTES);
            log.warn("Rate limit exceeded for IP: {}", ipAddress);
            throw new IllegalStateException("Too many login attempts. Please try again in " + retryAfter + " seconds.");
        }

        // Find user by username (could also support email login)
        User user = userMapper.selectByUsername(request.getUsername());

        // Use generic error message to prevent username enumeration
        if (user == null) {
            log.warn("Login failed: user {} not found", request.getUsername());
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Check if account is locked
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            log.warn("Login failed: account {} is locked until {}", user.getUsername(), user.getLockedUntil());
            throw new IllegalStateException("Account is temporarily locked. Please try again later.");
        }

        // Reset lock if it has expired
        if (user.getLockedUntil() != null && user.getLockedUntil().isBefore(LocalDateTime.now())) {
            userMapper.resetFailedLoginAttempts(user.getId());
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
        }

        // Verify password
        if (!passwordEncoder.verifyPassword(request.getPassword(), user.getPasswordHash())) {
            // Increment failed login attempts
            int newAttempts = (user.getFailedLoginAttempts() != null ? user.getFailedLoginAttempts() : 0) + 1;
            LocalDateTime lockedUntil = null;

            // Lock account after max failed attempts
            if (newAttempts >= AuthConstants.MAX_FAILED_LOGIN_ATTEMPTS) {
                lockedUntil = LocalDateTime.now().plusMinutes(AuthConstants.ACCOUNT_LOCK_DURATION_MINUTES);
                log.warn("Account locked: {} after {} failed attempts", user.getUsername(), newAttempts);
            }

            userMapper.incrementFailedLoginAttempts(user.getId(), 1, lockedUntil);

            log.warn("Login failed: invalid password for user {}", request.getUsername());
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Reset failed login attempts on successful login
        if (user.getFailedLoginAttempts() != null && user.getFailedLoginAttempts() > 0) {
            userMapper.resetFailedLoginAttempts(user.getId());
        }

        // Generate tokens
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        long accessTokenExpirySeconds = jwtTokenProvider.getAccessTokenValidityMs() / 1000;

        log.info("Login successful for user: {}", user.getUsername());

        // Build user response
        UserResponse userResponse = UserResponse.of(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCompany(),
                user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ISO_INSTANT) : null
        );

        return AuthResponse.withUser(accessToken, accessTokenExpirySeconds, userResponse);
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken the refresh token
     * @return the authentication response with new access token
     * @throws IllegalArgumentException if refresh token is invalid
     */
    @Override
    public AuthResponse refresh(String refreshToken) {
        log.info("Refreshing access token");

        // Validate refresh token
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        // Verify it's a refresh token
        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {
            log.warn("Token type mismatch: expected refresh token");
            throw new IllegalArgumentException("Invalid token type");
        }

        // Extract user ID
        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);

        // Verify user still exists and is active
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null || !user.getIsActive()) {
            log.warn("User not found or inactive: {}", userId);
            throw new IllegalArgumentException("User not found or inactive");
        }

        // Generate new access token
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        long accessTokenExpirySeconds = jwtTokenProvider.getAccessTokenValidityMs() / 1000;

        log.info("Access token refreshed for user: {}", user.getUsername());

        return AuthResponse.tokenOnly(newAccessToken, accessTokenExpirySeconds);
    }

    /**
     * Logout user (add token to blacklist).
     * Note: In production, implement Redis-based token blacklist.
     *
     * @param accessToken the access token to invalidate
     */
    @Override
    public void logout(String accessToken) {
        log.info("Logout request received");

        // Validate token before logout
        if (jwtTokenProvider.validateToken(accessToken)) {
            String userId = jwtTokenProvider.getUserIdFromToken(accessToken).toString();
            log.info("User logged out: {}", userId);
            // In production: add token to Redis blacklist with remaining TTL
        }
    }

    /**
     * Get current user information from access token.
     *
     * @param accessToken the access token
     * @return the user response
     * @throws IllegalArgumentException if token is invalid
     */
    @Override
    public UserResponse getCurrentUser(String accessToken) {
        // Validate token
        if (!jwtTokenProvider.validateToken(accessToken)) {
            log.warn("Invalid access token");
            throw new IllegalArgumentException("Invalid or expired access token");
        }

        // Verify it's an access token
        if (!jwtTokenProvider.isAccessToken(accessToken)) {
            log.warn("Token type mismatch: expected access token");
            throw new IllegalArgumentException("Invalid token type");
        }

        // Extract user ID
        UUID userId = jwtTokenProvider.getUserIdFromToken(accessToken);

        // Fetch user from database
        User user = userMapper.selectByPrimaryKey(userId);
        if (user == null) {
            log.warn("User not found: {}", userId);
            throw new IllegalArgumentException("User not found");
        }

        return UserResponse.of(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCompany(),
                user.getCreatedAt() != null ? user.getCreatedAt().format(DateTimeFormatter.ISO_INSTANT) : null
        );
    }
}
