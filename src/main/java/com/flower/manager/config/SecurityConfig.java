package com.flower.manager.config;

import com.flower.manager.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Cấu hình Spring Security với JWT
 * - Bảo vệ các API /api/admin/** cho ADMIN
 * - Cho phép truy cập công khai:
 * + /api/public/** - Public endpoints
 * + /api/auth/** - Authentication endpoints
 * + /api/products/** - Products (GET only)
 * + /api/categories/** - Categories (GET only)
 * - Xác thực tất cả các request khác
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Mã hóa mật khẩu với BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider xác thực với UserDetailsService và PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager để xử lý đăng nhập
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Cấu hình CORS để cho phép frontend gọi API
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Cho phép tất cả origins (trong production nên giới hạn)
        configuration.setAllowedOriginPatterns(List.of("*"));
        // Cho phép các HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cho phép các headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        // Expose Authorization header để frontend có thể đọc
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        // Cho phép credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        // Cache preflight request trong 1 giờ
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Cấu hình Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Bật CORS với cấu hình đã định nghĩa
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Tắt CSRF vì dùng JWT (stateless)
                .csrf(csrf -> csrf.disable())
                // Session policy: STATELESS vì dùng JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Cấu hình phân quyền URL
                .authorizeHttpRequests(auth -> auth
                        // Cho phép OPTIONS requests (CORS preflight)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Home, health, api info - không cần xác thực
                        .requestMatchers("/", "/health", "/api").permitAll()
                        // Public endpoints - không cần xác thực
                        .requestMatchers("/api/public/**").permitAll()
                        // Products & Categories - công khai cho tất cả (GET)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        // Reviews - công khai cho xem sản phẩm reviews (GET)
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()
                        // Vouchers - công khai cho xem danh sách voucher active
                        .requestMatchers(HttpMethod.GET, "/api/vouchers/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vouchers/check/**").permitAll()
                        // Chat - public endpoints for chatbot (allow all methods)
                        .requestMatchers("/api/chat/**").permitAll()
                        // Auth endpoints - không cần xác thực
                        .requestMatchers("/api/auth/**").permitAll()
                        // Static resources
                        .requestMatchers("/index.html", "/static/**", "/favicon.ico", "/error").permitAll()
                        // Uploaded files - public access for images
                        .requestMatchers("/uploads/**").permitAll()
                        // Swagger/OpenAPI (nếu có)
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        // Admin endpoints - chỉ ADMIN mới có quyền
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Admin chat endpoints - ADMIN và STAFF đều có quyền
                        .requestMatchers("/api/chat/admin/**").hasAnyRole("ADMIN", "STAFF")
                        // Staff endpoints - ADMIN và STAFF đều có quyền
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF")
                        // Tất cả các request khác cần xác thực
                        .anyRequest().authenticated())
                // Sử dụng custom authentication provider
                .authenticationProvider(authenticationProvider())
                // Thêm JWT filter trước UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
