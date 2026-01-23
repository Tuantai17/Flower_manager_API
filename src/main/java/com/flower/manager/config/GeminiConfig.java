package com.flower.manager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * B2) Cấu hình Gemini AI
 * 
 * application.yml:
 * gemini:
 * api-key: ${GEMINI_API_KEY:your-api-key}
 * model: gemini-2.0-flash
 * max-tokens: 4096
 * timeout: 30
 */
@Configuration
@ConfigurationProperties(prefix = "gemini")
@Getter
@Setter
public class GeminiConfig {

    /**
     * API Key từ Google AI Studio
     */
    private String apiKey;

    /**
     * Model sử dụng (gemini-2.0-flash, gemini-1.5-pro, etc.)
     */
    private String model = "gemini-2.0-flash";

    /**
     * Max output tokens
     */
    private int maxTokens = 4096;

    /**
     * Timeout (seconds)
     */
    private int timeout = 30;

    /**
     * Base URL cho Gemini API
     */
    private String baseUrl = "https://generativelanguage.googleapis.com/v1beta";

    /**
     * Temperature (0.0 - 1.0)
     * Cao hơn = sáng tạo hơn
     */
    private double temperature = 0.7;
}
