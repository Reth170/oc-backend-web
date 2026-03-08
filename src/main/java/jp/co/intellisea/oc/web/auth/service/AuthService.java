package jp.co.intellisea.oc.web.auth.service;

import jp.co.intellisea.oc.web.auth.dto.AuthResponse;
import jp.co.intellisea.oc.web.auth.dto.LoginRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterResponse;
import jp.co.intellisea.oc.web.auth.dto.UserResponse;

import java.util.UUID;

/**
 * Authentication service interface.
 * Defines the contract for authentication operations.
 */
public interface AuthService {

    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return the registration response with user ID and username
     * @throws IllegalArgumentException if registration fails (e.g., username/email exists)
     */
    RegisterResponse register(RegisterRequest request);

    /**
     * Authenticate a user and return tokens.
     *
     * @param request the login request
     * @param ipAddress the client IP address for rate limiting
     * @return the authentication response with access token and user info
     * @throws IllegalArgumentException if credentials are invalid
     * @throws IllegalStateException if account is locked
     */
    AuthResponse login(LoginRequest request, String ipAddress);

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken the refresh token
     * @return the authentication response with new access token
     * @throws IllegalArgumentException if refresh token is invalid
     */
    AuthResponse refresh(String refreshToken);

    /**
     * Logout a user (invalidate tokens).
     *
     * @param accessToken the access token to invalidate
     */
    void logout(String accessToken);

    /**
     * Get current user information.
     *
     * @param accessToken the access token
     * @return the user response
     * @throws IllegalArgumentException if token is invalid
     */
    UserResponse getCurrentUser(String accessToken);
}
