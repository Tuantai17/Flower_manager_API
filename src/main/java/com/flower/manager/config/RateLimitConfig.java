package com.flower.manager.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate Limiting Configuration
 * 
 * Bảo vệ login/register endpoints khỏi brute force attacks
 * Sử dụng Bucket4j token bucket algorithm
 */
@Configuration
public class RateLimitConfig {

    /**
     * Cache chứa rate limit buckets cho mỗi IP
     */
    @Bean
    public Map<String, Bucket> rateLimitBuckets() {
        return new ConcurrentHashMap<>();
    }

    /**
     * Tạo bucket với rate limit rules:
     * - Max 5 requests
     * - Trong 1 phút
     * - Refill 5 tokens/minute
     */
    public Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
