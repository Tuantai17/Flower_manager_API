package com.flower.manager.controller.auth;

import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.GoogleLoginRequest;
import com.flower.manager.service.auth.GoogleAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý đăng nhập bằng Google OAuth2
 * Endpoint: /api/auth/google
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;

    /**
     * API Đăng nhập bằng Google
     * POST /api/auth/google
     *
     * Body:
     * {
     * "idToken": "google-id-token-from-frontend"
     * }
     *
     * Flow:
     * 1. Frontend sử dụng Google Sign-In SDK để lấy idToken
     * 2. Frontend gửi idToken lên API này
     * 3. Backend xác thực token với Google
     * 4. Nếu hợp lệ, tạo hoặc lấy user và trả về JWT
     *
     * @param request chứa idToken từ Google
     * @return AuthResponse với JWT token
     */
    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@Valid @RequestBody GoogleLoginRequest request) {
        log.info("Google login request received");
        AuthResponse response = googleAuthService.loginWithGoogle(request);
        return ResponseEntity.ok(response);
    }
}
