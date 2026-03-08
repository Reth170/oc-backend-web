package jp.co.intellisea.oc.web.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * CORS filter for authentication endpoints.
 * Configures cross-origin resource sharing with secure defaults.
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthCorsFilter extends OncePerRequestFilter {

    /**
     * Allowed origins for CORS.
     * In production, replace with specific frontend domain.
     */
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:5173",
            "http://127.0.0.1:3000",
            "http://127.0.0.1:5173"
    );

    /**
     * Allowed HTTP methods.
     */
    private static final List<String> ALLOWED_METHODS = Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    );

    /**
     * Allowed headers.
     */
    private static final List<String> ALLOWED_HEADERS = Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin"
    );

    /**
     * Exposed headers (accessible to the browser).
     */
    private static final List<String> EXPOSED_HEADERS = Arrays.asList(
            "Set-Cookie"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String origin = request.getHeader("Origin");

        // Check if origin is allowed
        if (origin != null && ALLOWED_ORIGINS.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        // Allow credentials (cookies)
        response.setHeader("Access-Control-Allow-Credentials", "true");

        // Set allowed methods
        response.setHeader("Access-Control-Allow-Methods", String.join(",", ALLOWED_METHODS));

        // Set allowed headers
        response.setHeader("Access-Control-Allow-Headers", String.join(",", ALLOWED_HEADERS));

        // Set exposed headers
        response.setHeader("Access-Control-Expose-Headers", String.join(",", EXPOSED_HEADERS));

        // Handle preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
