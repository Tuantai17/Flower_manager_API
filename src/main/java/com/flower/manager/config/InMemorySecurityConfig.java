package com.flower.manager.config;

import com.flower.manager.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Cấu hình Spring Security với InMemoryUserDetailsManager
 * - Chỉ active khi profile = "inmemory"
 * - Dùng cho demo/testing nhanh, không cần database
 * 
 * Cách chạy: mvn spring-boot:run -Dspring-boot.run.profiles=inmemory
 * 
 * Users mặc định:
 * - admin:admin123 (ROLE_ADMIN)
 * - user:user123 (ROLE_CUSTOMER)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
@Profile("inmemory")
public class InMemorySecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Mã hóa mật khẩu với BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Tạo InMemoryUserDetailsManager với users có sẵn
     * - admin:admin123 (ROLE_ADMIN)
     * - user:user123 (ROLE_CUSTOMER)
     */
    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager(PasswordEncoder passwordEncoder) {
        // Tạo admin user
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("ADMIN") // Spring tự thêm prefix "ROLE_"
                .build();

        // Tạo user bình thường
        UserDetails user = User.builder()
                .username("user")
                .password(passwordEncoder.encode("user123"))
                .roles("CUSTOMER")
                .build();

        // Tạo staff user
        UserDetails staff = User.builder()
                .username("staff")
                .password(passwordEncoder.encode("staff123"))
                .roles("STAFF")
                .build();

        return new InMemoryUserDetailsManager(admin, user, staff);
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
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Cấu hình Security Filter Chain (giống SecurityConfig nhưng dùng InMemory)
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/health", "/api").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vouchers/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/vouchers/check/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/banners").permitAll()
                        .requestMatchers("/api/chat/**").permitAll()
                        .requestMatchers("/api/geocode/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/index.html", "/static/**", "/favicon.ico", "/error").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**").permitAll()
                        .requestMatchers("/ws/**", "/ws/chat/**").permitAll()
                        .requestMatchers("/api/contact/tickets").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/contact/tickets").permitAll()
                        // H2 Console cho debug
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/chat/admin/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF")
                        .anyRequest().authenticated())
                // Cho phép H2 Console hiển thị trong iframe
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
