package jp.co.intellisea.oc.web.auth.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Standard API response wrapper.
 * Follows the specification in auth_spec.md.
 *
 * @param <T> the type of data
 */
@Getter
@Builder
public class ApiResponse<T> {

    /**
     * HTTP status code.
     */
    private Integer code;

    /**
     * Success flag.
     */
    private Boolean success;

    /**
     * Human-readable message.
     */
    private String message;

    /**
     * Response data.
     */
    private T data;

    /**
     * Validation errors (optional).
     */
    private List<FieldError> errors;

    /**
     * Create a success response.
     *
     * @param code the HTTP status code
     * @param message the success message
     * @param data the response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(Integer code, String message, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create a success response without message.
     *
     * @param code the HTTP status code
     * @param data the response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(Integer code, T data) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Create an error response.
     *
     * @param code the HTTP status code
     * @param message the error message
     * @return ApiResponse instance
     */
    public static ApiResponse<Void> error(Integer code, String message) {
        return ApiResponse.<Void>builder()
                .code(code)
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Create an error response with validation errors.
     *
     * @param code the HTTP status code
     * @param message the error message
     * @param errors the field errors
     * @return ApiResponse instance
     */
    public static ApiResponse<Void> error(Integer code, String message, List<FieldError> errors) {
        return ApiResponse.<Void>builder()
                .code(code)
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Create an error response with generic type.
     *
     * @param code the HTTP status code
     * @param message the error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Create an error response with validation errors and generic type.
     *
     * @param code the HTTP status code
     * @param message the error message
     * @param errors the field errors
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(Integer code, String message, List<FieldError> errors) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    /**
     * Field error representation.
     */
    @Getter
    @Builder
    public static class FieldError {
        private final String field;
        private final String message;

        public static FieldError of(String field, String message) {
            return FieldError.builder()
                    .field(field)
                    .message(message)
                    .build();
        }
    }
}
