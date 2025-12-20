package com.flower.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Cấu hình Web MVC
 * CORS đã được cấu hình trong SecurityConfig
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration đã chuyển sang SecurityConfig để tích hợp với Spring
    // Security
    // Không cần cấu hình thêm ở đây
}
