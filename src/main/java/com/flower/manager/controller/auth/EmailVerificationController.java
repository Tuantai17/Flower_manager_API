package com.flower.manager.controller.auth;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.service.auth.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller xử lý xác thực email
 */
@RestController
@RequestMapping("/api/auth/email")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Email Verification", description = "API xác thực email người dùng")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    /**
     * Xác thực email bằng token
     * GET /api/auth/email/verify?token=xxx
     */
    @GetMapping("/verify")
    @Operation(summary = "Xác thực email", description = "Xác thực email bằng token được gửi qua email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestParam String token) {
        log.info("Verifying email with token");

        emailVerificationService.verifyEmail(token);

        return ResponseEntity.ok(ApiResponse.success(
                "Email đã được xác thực thành công! Bạn có thể đăng nhập ngay bây giờ."));
    }

    /**
     * Gửi lại email xác thực
     * POST /api/auth/email/resend
     */
    @PostMapping("/resend")
    @Operation(summary = "Gửi lại email xác thực", description = "Gửi lại email xác thực cho người dùng")
    public ResponseEntity<ApiResponse<String>> resendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        log.info("Resending verification email to: {}", email);

        emailVerificationService.resendVerificationEmail(email);

        return ResponseEntity.ok(ApiResponse.success(
                "Email xác thực đã được gửi lại. Vui lòng kiểm tra hộp thư của bạn."));
    }

    /**
     * Kiểm tra trạng thái xác thực email
     * GET /api/auth/email/status/{userId}
     */
    @GetMapping("/status/{userId}")
    @Operation(summary = "Kiểm tra trạng thái xác thực", description = "Kiểm tra user đã xác thực email chưa")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailVerificationStatus(@PathVariable Long userId) {
        boolean isVerified = emailVerificationService.isEmailVerified(userId);

        return ResponseEntity.ok(ApiResponse.success(
                isVerified,
                isVerified ? "Email đã được xác thực" : "Email chưa được xác thực"));
    }
}
