package com.flower.manager.security;

import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

/**
 * Rate Limiting Interceptor
 * 
 * Chặn các requests vượt quá giới hạn rate limit
 * Apply cho /api/auth/login và /api/auth/register
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> rateLimitBuckets;
    private final com.flower.manager.config.RateLimitConfig rateLimitConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String clientIp = getClientIP(request);

        // Get or create bucket for this IP
        Bucket bucket = rateLimitBuckets.computeIfAbsent(clientIp, k -> rateLimitConfig.createNewBucket());

        // Try to consume 1 token
        if (bucket.tryConsume(1)) {
            // Request allowed
            return true;
        } else {
            // Rate limit exceeded
            log.warn("Rate limit exceeded for IP: {}", clientIp);
            response.setStatus(429); // Too Many Requests
            response.setContentType("application/json");
            response.getWriter()
                    .write("{\"success\": false, \"message\": \"Quá nhiều yêu cầu. Vui lòng thử lại sau 1 phút.\"}");
            return false;
        }
    }

    /**
     * Lấy IP thật của client (support reverse proxy)
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
