package com.flower.manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

/**
 * CSRF Configuration for Enhanced Security
 * 
 * Khi dùng HttpOnly cookie cho JWT, cần enable CSRF protection
 * để chống CSRF attacks.
 */
@Configuration
public class CsrfConfig {

    /**
     * CSRF Token Repository - lưu token trong cookie
     * Cookie này KHÔNG HttpOnly để JavaScript có thể đọc và gửi trong header
     */
    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieName("XSRF-TOKEN");
        repository.setHeaderName("X-XSRF-TOKEN");
        repository.setCookiePath("/");
        return repository;
    }

    /**
     * CSRF Token Handler - xử lý CSRF token trong request
     */
    @Bean
    public CsrfTokenRequestAttributeHandler csrfTokenRequestHandler() {
        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName("_csrf");
        return handler;
    }
}
