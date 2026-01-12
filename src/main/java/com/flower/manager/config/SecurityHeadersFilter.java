package com.flower.manager.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter để thêm các security headers cần thiết cho Google Sign-In
 * 
 * Cross-Origin-Opener-Policy: same-origin-allow-popups
 * - Cho phép popup (Google Sign-In) giao tiếp với cửa sổ chính
 * 
 * Cross-Origin-Embedder-Policy: unsafe-none
 * - Cho phép embed resources từ bên ngoài (Google APIs)
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Cho phép popup Google Sign-In giao tiếp với cửa sổ cha
        // same-origin-allow-popups: cho phép postMessage từ popup
        httpResponse.setHeader("Cross-Origin-Opener-Policy", "same-origin-allow-popups");

        // Cho phép load resources từ Google APIs
        httpResponse.setHeader("Cross-Origin-Embedder-Policy", "unsafe-none");

        // Thêm header để tương thích với các trình duyệt cũ
        httpResponse.setHeader("Cross-Origin-Resource-Policy", "cross-origin");

        chain.doFilter(request, response);
    }
}
