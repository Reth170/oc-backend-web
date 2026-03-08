package jp.co.intellisea.oc.web.auth.controller;

import jp.co.intellisea.oc.web.auth.common.ApiResponse;
import jp.co.intellisea.oc.web.auth.common.AuthConstants;
import jp.co.intellisea.oc.web.auth.config.RateLimiter;
import jp.co.intellisea.oc.web.auth.dto.AuthResponse;
import jp.co.intellisea.oc.web.auth.dto.LoginRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterRequest;
import jp.co.intellisea.oc.web.auth.dto.RegisterResponse;
import jp.co.intellisea.oc.web.auth.dto.UserResponse;
import jp.co.intellisea.oc.web.auth.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for authentication endpoints.
 * Implements the API specification defined in auth_spec.md.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RateLimiter rateLimiter;

    /**
     * Register a new user.
     * POST /api/auth/register
     *
     * @param request the registration request
     * @param bindingResult validation result
     * @return the registration response
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            List<ApiResponse.FieldError> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> ApiResponse.FieldError.of(
                            error.getField(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
        }

        try {
            RegisterResponse response = authService.register(request);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ApiResponse.success(HttpStatus.CREATED.value(), "Registration successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error(HttpStatus.TOO_MANY_REQUESTS.value(), e.getMessage()));
        }
    }

    /**
     * Login user and return tokens.
     * POST /api/auth/login
     *
     * @param request the login request
     * @param bindingResult validation result
     * @param httpRequest the HTTP request (for IP address)
     * @return the authentication response with refresh token cookie
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            BindingResult bindingResult,
            HttpServletRequest httpRequest) {

        if (bindingResult.hasErrors()) {
            List<ApiResponse.FieldError> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> ApiResponse.FieldError.of(
                            error.getField(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
        }

        try {
            // Get client IP address for rate limiting
            String ipAddress = getClientIpAddress(httpRequest);

            AuthResponse response = authService.login(request, ipAddress);

            // Create refresh token cookie
            ResponseCookie refreshCookie = ResponseCookie.from(
                    AuthConstants.REFRESH_TOKEN_COOKIE_NAME,
                    response.getAccessToken()) // In production, this should be a separate refresh token
                    .httpOnly(true)
                    .secure(true) // Use true in production with HTTPS
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(ApiResponse.success(HttpStatus.OK.value(), "Login successful", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), e.getMessage()));
        } catch (Exception e) {
            log.error("Login error", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error"));
        }
    }

    /**
     * Refresh access token.
     * POST /api/auth/refresh
     *
     * @param httpRequest the HTTP request (for refresh token cookie)
     * @return the authentication response with new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(HttpServletRequest httpRequest) {
        // Get refresh token from cookie
        String refreshToken = null;
        if (httpRequest.getCookies() != null) {
            for (var cookie : httpRequest.getCookies()) {
                if (AuthConstants.REFRESH_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Refresh token not found"));
        }

        try {
            AuthResponse response = authService.refresh(refreshToken);

            // Create new refresh token cookie
            ResponseCookie refreshCookie = ResponseCookie.from(
                    AuthConstants.REFRESH_TOKEN_COOKIE_NAME,
                    response.getAccessToken()) // In production, this should be a separate refresh token
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .build();

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(ApiResponse.success(HttpStatus.OK.value(), null, response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
    }

    /**
     * Logout user.
     * POST /api/auth/logout
     *
     * @param httpRequest the HTTP request
     * @return the logout response
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest) {
        // Get access token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;

        if (authHeader != null && authHeader.startsWith(AuthConstants.AUTHORIZATION_HEADER_PREFIX)) {
            accessToken = authHeader.substring(AuthConstants.AUTHORIZATION_HEADER_PREFIX.length());
        }

        if (accessToken != null && !accessToken.isEmpty()) {
            authService.logout(accessToken);
        }

        // Clear refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from(
                AuthConstants.REFRESH_TOKEN_COOKIE_NAME,
                "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // Delete cookie
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(ApiResponse.success(HttpStatus.OK.value(), "Logout successful", null));
    }

    /**
     * Get current user information.
     * GET /api/auth/me
     *
     * @param httpRequest the HTTP request
     * @return the user response
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(HttpServletRequest httpRequest) {
        // Get access token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        String accessToken = null;

        if (authHeader != null && authHeader.startsWith(AuthConstants.AUTHORIZATION_HEADER_PREFIX)) {
            accessToken = authHeader.substring(AuthConstants.AUTHORIZATION_HEADER_PREFIX.length());
        }

        if (accessToken == null || accessToken.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "Access token required"));
        }

        try {
            UserResponse userResponse = authService.getCurrentUser(accessToken);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(ApiResponse.success(HttpStatus.OK.value(), null, userResponse));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
        }
    }

    /**
     * Extract client IP address from HTTP request.
     *
     * @param request the HTTP request
     * @return the client IP address
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] HEADERS_TO_TRY = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : HEADERS_TO_TRY) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For can contain multiple IPs; take the first one
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
