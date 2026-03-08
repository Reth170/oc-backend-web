package jp.co.intellisea.oc.web.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

/**
 * JWT Token provider for generating, validating, and refreshing access tokens.
 *
 * Token Strategy:
 * - Access Token: JWT, 15 minutes expiry, sent in Authorization header
 * - Refresh Token: Stored in HttpOnly cookie, 7 days expiry
 */
@Component
public class JwtTokenProvider {

    /**
     * Secret key for JWT signing. In production, this should be loaded from
     * environment variables or a secure configuration store.
     */
    @Value("${auth.jwt.secret:your-256-bit-secret-key-for-jwt-signing-change-in-production}")
    private String jwtSecret;

    /**
     * Access token expiration time in milliseconds (15 minutes).
     */
    @Value("${auth.jwt.access-token-validity:900000}")
    private long accessTokenValidityMs;

    /**
     * Refresh token expiration time in milliseconds (7 days).
     */
    @Value("${auth.jwt.refresh-token-validity:604800000}")
    private long refreshTokenValidityMs;

    private Key key;

    /**
     * Initialize the signing key after bean construction.
     */
    @PostConstruct
    public void init() {
        // Ensure the secret is at least 256 bits for HS256
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate an access token for a user.
     *
     * @param userId the user's UUID
     * @param username the user's username
     * @return the generated JWT access token
     */
    public String generateAccessToken(UUID userId, String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenValidityMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate a refresh token for a user.
     *
     * @param userId the user's UUID
     * @return the generated refresh token ID
     */
    public String generateRefreshToken(UUID userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenValidityMs);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract the user ID from a JWT token.
     *
     * @param token the JWT token
     * @return the user's UUID
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    /**
     * Extract the username from an access token.
     *
     * @param token the JWT access token
     * @return the username
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("username", String.class);
    }

    /**
     * Check if a token is an access token.
     *
     * @param token the JWT token
     * @return true if it's an access token
     */
    public boolean isAccessToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "access".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if a token is a refresh token.
     *
     * @param token the JWT token
     * @return true if it's a refresh token
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = getClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate a JWT token.
     *
     * @param token the JWT token to validate
     * @return true if the token is valid
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            // Invalid signature
            return false;
        } catch (MalformedJwtException e) {
            // Invalid token format
            return false;
        } catch (ExpiredJwtException e) {
            // Token has expired
            return false;
        } catch (UnsupportedJwtException e) {
            // Unsupported token type
            return false;
        } catch (IllegalArgumentException e) {
            // Empty token
            return false;
        }
    }

    /**
     * Check if a token is expired (without throwing exception).
     *
     * @param token the JWT token
     * @return true if the token is expired
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            return true; // Treat any error as expired
        }
    }

    /**
     * Get the expiration time of a token.
     *
     * @param token the JWT token
     * @return the expiration date, or null if invalid
     */
    public Date getExpirationDate(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the remaining time in seconds before token expires.
     *
     * @param token the JWT token
     * @return remaining seconds, or 0 if expired/invalid
     */
    public long getRemainingSeconds(String token) {
        Date expiration = getExpirationDate(token);
        if (expiration == null) {
            return 0;
        }
        long remaining = expiration.getTime() - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    /**
     * Extract claims from a JWT token.
     *
     * @param token the JWT token
     * @return the claims
     * @throws JwtException if the token is invalid
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get access token validity in milliseconds.
     *
     * @return validity in ms
     */
    public long getAccessTokenValidityMs() {
        return accessTokenValidityMs;
    }

    /**
     * Get refresh token validity in milliseconds.
     *
     * @return validity in ms
     */
    public long getRefreshTokenValidityMs() {
        return refreshTokenValidityMs;
    }
}
