package jp.co.intellisea.oc.web.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Response DTO containing user information.
 */
@Getter
@Builder
public class UserResponse {

    /**
     * User ID.
     */
    private UUID id;

    /**
     * Username.
     */
    private String username;

    /**
     * Email address.
     */
    private String email;

    /**
     * Company name.
     */
    private String company;

    /**
     * Account creation timestamp (ISO8601 format).
     */
    @JsonProperty("createdAt")
    private String createdAt;

    /**
     * Create a UserResponse from entity object.
     *
     * @param id the user ID
     * @param username the username
     * @param email the email
     * @param company the company name
     * @param createdAt the creation timestamp
     * @return UserResponse instance
     */
    public static UserResponse of(UUID id, String username, String email, String company, String createdAt) {
        return UserResponse.builder()
                .id(id)
                .username(username)
                .email(email)
                .company(company)
                .createdAt(createdAt)
                .build();
    }
}
