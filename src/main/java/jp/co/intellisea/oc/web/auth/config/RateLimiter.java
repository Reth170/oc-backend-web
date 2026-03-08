package jp.co.intellisea.oc.web.auth.config;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * In-memory rate limiter for development environment.
 * Uses a sliding window algorithm to track request counts.
 *
 * For production, consider using Redis-based rate limiting.
 */
@Component
public class RateLimiter {

    /**
     * Stores rate limit data per key (e.g., IP address or username).
     * Key: identifier (IP/username)
     * Value: RateLimitEntry with count and window start time
     */
    private final Map<String, RateLimitEntry> rateLimitStore = new ConcurrentHashMap<>();

    /**
     * Lock for thread-safe updates to rate limit entries.
     */
    private final ReentrantLock lock = new ReentrantLock();

    /**
     * Default rate limit settings.
     */
    private static final int DEFAULT_MAX_REQUESTS = 5;
    private static final int DEFAULT_WINDOW_MINUTES = 15;

    /**
     * Check if a request is allowed under the rate limit.
     * Uses default settings: 5 requests per 15 minutes.
     *
     * @param key the identifier (e.g., IP address or username)
     * @return true if the request is allowed, false if rate limited
     */
    public boolean isAllowed(String key) {
        return isAllowed(key, DEFAULT_MAX_REQUESTS, DEFAULT_WINDOW_MINUTES);
    }

    /**
     * Check if a request is allowed under the rate limit.
     *
     * @param key the identifier (e.g., IP address or username)
     * @param maxRequests maximum number of requests allowed in the window
     * @param windowMinutes the time window in minutes
     * @return true if the request is allowed, false if rate limited
     */
    public boolean isAllowed(String key, int maxRequests, int windowMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(windowMinutes);

        lock.lock();
        try {
            RateLimitEntry entry = rateLimitStore.get(key);

            if (entry == null || entry.windowStart.isAfter(windowStart)) {
                // New window or first request
                if (entry == null) {
                    entry = new RateLimitEntry(0, now);
                    rateLimitStore.put(key, entry);
                } else {
                    // Reset counter for new window
                    entry.requestCount = 0;
                    entry.windowStart = now;
                }
            }

            if (entry.requestCount < maxRequests) {
                entry.requestCount++;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the remaining number of requests allowed in the current window.
     *
     * @param key the identifier
     * @param maxRequests maximum number of requests allowed in the window
     * @param windowMinutes the time window in minutes
     * @return remaining requests
     */
    public int getRemainingRequests(String key, int maxRequests, int windowMinutes) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(windowMinutes);

        lock.lock();
        try {
            RateLimitEntry entry = rateLimitStore.get(key);

            if (entry == null || entry.windowStart.isAfter(windowStart)) {
                return maxRequests;
            }

            return Math.max(0, maxRequests - entry.requestCount);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Reset the rate limit for a specific key.
     *
     * @param key the identifier to reset
     */
    public void reset(String key) {
        lock.lock();
        try {
            rateLimitStore.remove(key);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clear all rate limit entries.
     * Should be used cautiously, mainly for testing.
     */
    public void clearAll() {
        lock.lock();
        try {
            rateLimitStore.clear();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get the retry-after time in seconds.
     *
     * @param key the identifier
     * @param windowMinutes the time window in minutes
     * @return seconds until the rate limit resets
     */
    public long getRetryAfterSeconds(String key, int windowMinutes) {
        lock.lock();
        try {
            RateLimitEntry entry = rateLimitStore.get(key);
            if (entry == null) {
                return 0;
            }

            LocalDateTime windowEnd = entry.windowStart.plusMinutes(windowMinutes);
            return Math.max(0, java.time.Duration.between(LocalDateTime.now(), windowEnd).getSeconds());
        } finally {
            lock.unlock();
        }
    }

    /**
     * Internal class to store rate limit entry data.
     */
    private static class RateLimitEntry {
        int requestCount;
        LocalDateTime windowStart;

        RateLimitEntry(int requestCount, LocalDateTime windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}
