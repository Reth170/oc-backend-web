package jp.co.intellisea.oc.web.auth.config;

import jp.co.intellisea.oc.web.auth.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.AccountLockedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler for authentication endpoints.
 * Provides unified error response format.
 */
@Slf4j
@RestControllerAdvice(basePackages = "jp.co.intellisea.oc.web.auth")
public class GlobalExceptionHandler {

    /**
     * Handle validation errors.
     *
     * @param ex the exception
     * @return standardized error response
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> ApiResponse.FieldError.of(
                        error.getField(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());

        log.warn("Validation error: {}", errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    /**
     * Handle illegal argument errors (invalid credentials, etc.).
     *
     * @param ex the exception
     * @return standardized error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    /**
     * Handle illegal state errors (account locked, rate limit exceeded, etc.).
     *
     * @param ex the exception
     * @return standardized error response
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        log.warn("Illegal state: {}", ex.getMessage());

        HttpStatus status = HttpStatus.FORBIDDEN;
        if (ex.getMessage().contains("Too many")) {
            status = HttpStatus.TOO_MANY_REQUESTS;
        } else if (ex.getMessage().contains("locked")) {
            status = HttpStatus.FORBIDDEN;
        }

        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(status.value(), ex.getMessage()));
    }

    /**
     * Handle account locked exceptions.
     *
     * @param ex the exception
     * @return standardized error response
     */
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountLockedException(AccountLockedException ex) {
        log.warn("Account locked: {}", ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "Account is temporarily locked"));
    }

    /**
     * Handle all other exceptions.
     *
     * @param ex the exception
     * @return standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred"));
    }
}
