package jp.co.intellisea.oc.web.auth.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

/**
 * Request DTO for user login.
 */
@Getter
@Setter
public class LoginRequest {

    /**
     * Username or email for login.
     */
    @NotBlank(message = "Username is required")
    private String username;

    /**
     * Password.
     */
    @NotBlank(message = "Password is required")
    private String password;

    /**
     * Remember me flag (extends refresh token expiry).
     */
    private Boolean rememberMe;
}
