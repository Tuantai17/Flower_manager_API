package com.flower.manager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Cau hinh Web MVC
 * - CORS da duoc cau hinh trong SecurityConfig
 * - Cau hinh serve static files tu thu muc uploads
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    // Inject rate limit interceptor
    private final com.flower.manager.security.RateLimitInterceptor rateLimitInterceptor;

    public WebConfig(com.flower.manager.security.RateLimitInterceptor rateLimitInterceptor) {
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    /**
     * Cau hinh resource handler de serve file tu thu muc uploads
     * URL: /uploads/** -> file:uploads/
     */
    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadLocation = "file:" + uploadPath.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation)
                .setCachePeriod(3600); // Cache 1 hour
    }

    /**
     * Apply rate limiting to auth endpoints
     */
    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/api/auth/login", "/api/auth/register");
    }
}
