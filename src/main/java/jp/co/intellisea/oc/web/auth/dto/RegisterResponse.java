package jp.co.intellisea.oc.web.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Response DTO for registration.
 */
@Getter
@Builder
public class RegisterResponse {

    /**
     * User ID.
     */
    private String userId;

    /**
     * Username.
     */
    private String username;

    /**
     * Create a RegisterResponse.
     *
     * @param userId the user ID
     * @param username the username
     * @return RegisterResponse instance
     */
    public static RegisterResponse of(String userId, String username) {
        return RegisterResponse.builder()
                .userId(userId)
                .username(username)
                .build();
    }
}
