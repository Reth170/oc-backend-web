package jp.co.intellisea.oc.web.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

/**
 * Response DTO for login and token refresh operations.
 */
@Getter
@Builder
public class AuthResponse {

    /**
     * JWT access token.
     */
    @JsonProperty("accessToken")
    private String accessToken;

    /**
     * Token type (always "Bearer").
     */
    @JsonProperty("tokenType")
    private String tokenType;

    /**
     * Access token expiration time in seconds.
     */
    @JsonProperty("expiresIn")
    private Long expiresIn;

    /**
     * User information.
     */
    @JsonProperty("user")
    private UserResponse user;

    /**
     * Create an AuthResponse with user info (for login).
     *
     * @param accessToken the access token
     * @param expiresIn the expiration time in seconds
     * @param user the user response
     * @return AuthResponse instance
     */
    public static AuthResponse withUser(String accessToken, long expiresIn, UserResponse user) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .user(user)
                .build();
    }

    /**
     * Create an AuthResponse without user info (for token refresh).
     *
     * @param accessToken the access token
     * @param expiresIn the expiration time in seconds
     * @return AuthResponse instance
     */
    public static AuthResponse tokenOnly(String accessToken, long expiresIn) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}
