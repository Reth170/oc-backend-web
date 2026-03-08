package jp.co.intellisea.oc.web.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Request DTO for user registration.
 */
@Getter
@Setter
public class RegisterRequest {

    /**
     * Username (3-20 characters, alphanumeric only).
     */
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username can only contain letters and numbers")
    private String username;

    /**
     * Email address (must be valid format).
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid format")
    private String email;

    /**
     * Password (min 8 chars, must contain uppercase, lowercase, number, and special char).
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*[0-9].*", message = "Password must contain at least one number")
    @Pattern(regexp = ".*[^A-Za-z0-9].*", message = "Password must contain at least one special character")
    private String password;

    /**
     * Optional company name.
     */
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String company;
}
