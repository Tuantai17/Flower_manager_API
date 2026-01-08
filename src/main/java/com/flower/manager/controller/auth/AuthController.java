package com.flower.manager.controller.auth;

import com.flower.manager.dto.auth.UserDTO;
import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.ForgotPasswordRequest;
import com.flower.manager.dto.auth.LoginRequest;
import com.flower.manager.dto.auth.RegisterRequest;
import com.flower.manager.dto.auth.ResetPasswordRequest;
import com.flower.manager.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API xác thực
 * Endpoint: /api/auth/**
 * 
 * Hỗ trợ:
 * - Đăng ký với username, email, phoneNumber, password, confirmPassword
 * - Đăng nhập bằng username / email / số điện thoại
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * API Đăng nhập
     * POST /api/auth/login
     * 
     * Body:
     * {
     * "identifier": "username hoặc email hoặc số điện thoại",
     * "password": "password"
     * }
     * 
     * @param loginRequest chứa identifier và password
     * @return AuthResponse với token JWT và thông tin user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest loginRequest,
            jakarta.servlet.http.HttpServletResponse response) {
        log.info("Login request for: {}", loginRequest.getIdentifier());
        AuthResponse authResponse = authService.login(loginRequest);

        // Set JWT as HttpOnly cookie (more secure against XSS)
        if (authResponse.isSuccess() && authResponse.getToken() != null) {
            jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("access_token",
                    authResponse.getToken());
            cookie.setHttpOnly(true); // Cannot be accessed by JavaScript
            cookie.setSecure(false); // Set to true in production with HTTPS
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(cookie);

            log.info("JWT cookie set for user: {}", loginRequest.getIdentifier());
        }

        return ResponseEntity.ok(authResponse);
    }

    /**
     * API Đăng ký
     * POST /api/auth/register
     * 
     * Body:
     * {
     * "username": "johndoe",
     * "email": "john@example.com",
     * "phoneNumber": "0912345678",
     * "password": "Password123",
     * "confirmPassword": "Password123"
     * }
     * 
     * @param registerRequest chứa thông tin đăng ký
     * @return AuthResponse với token JWT và thông tin user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Register request for username: {}", registerRequest.getUsername());
        AuthResponse response = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API Quên mật khẩu
     * POST /api/auth/forgot-password
     * 
     * Body:
     * {
     * "email": "john@example.com"
     * }
     * 
     * @param request chứa email
     * @return AuthResponse với thông báo và reset token
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<AuthResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Forgot password request for email: {}", request.getEmail());
        AuthResponse response = authService.forgotPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API Đặt lại mật khẩu
     * POST /api/auth/reset-password
     * 
     * Body:
     * {
     * "token": "reset-token-from-email",
     * "email": "john@example.com",
     * "newPassword": "NewPassword123",
     * "confirmPassword": "NewPassword123"
     * }
     * 
     * @param request chứa token, email và mật khẩu mới
     * @return AuthResponse với thông báo
     */
    @PostMapping("/reset-password")
    public ResponseEntity<AuthResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Reset password request for email: {}", request.getEmail());
        AuthResponse response = authService.resetPassword(request);
        return ResponseEntity.ok(response);
    }

    /**
     * API Lấy thông tin user hiện tại
     * GET /api/auth/me
     * 
     * Header: Authorization: Bearer {token}
     * 
     * @return UserDTO với thông tin user
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        log.info("Get current user request");
        UserDTO user = authService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * API Kiểm tra token còn hợp lệ không
     * GET /api/auth/validate
     * 
     * Header: Authorization: Bearer {token}
     * 
     * @return success nếu token hợp lệ
     */
    @GetMapping("/validate")
    public ResponseEntity<AuthResponse> validateToken() {
        UserDTO user = authService.getCurrentUser();
        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Token hợp lệ")
                .user(user)
                .build());
    }

    /**
     * API Đăng xuất
     * POST /api/auth/logout
     * 
     * Clear JWT cookie
     * 
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(jakarta.servlet.http.HttpServletResponse response) {
        log.info("Logout request");

        // Clear the access_token cookie
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("access_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete cookie
        response.addCookie(cookie);

        return ResponseEntity.ok(AuthResponse.builder()
                .success(true)
                .message("Đăng xuất thành công")
                .build());
    }
}
