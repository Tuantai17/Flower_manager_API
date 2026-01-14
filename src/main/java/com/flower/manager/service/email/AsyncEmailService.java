package com.flower.manager.service.email;

import com.flower.manager.entity.User;
import com.flower.manager.service.auth.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service helper để gửi email hoàn toàn async
 * Tách riêng để đảm bảo @Async hoạt động đúng (called from different bean)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncEmailService {

    private final EmailVerificationService emailVerificationService;
    private final EmailService emailService;

    /**
     * Gửi email xác thực hoàn toàn async
     * Chạy trong thread pool riêng, không block caller
     */
    @Async
    public void sendVerificationEmailAsync(User user) {
        log.info("🚀 [ASYNC] Starting to send verification email to: {}", user.getEmail());
        try {
            emailVerificationService.sendVerificationEmail(user);
            log.info("✅ [ASYNC] Verification email sent successfully to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("❌ [ASYNC] Failed to send verification email to {}: {}",
                    user.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * Gửi email reset password hoàn toàn async
     */
    @Async
    public void sendPasswordResetAsync(String to, String resetToken, String frontendUrl) {
        log.info("🚀 [ASYNC] Starting to send password reset email to: {}", to);
        try {
            emailService.sendPasswordResetEmail(to, resetToken, frontendUrl);
            log.info("✅ [ASYNC] Password reset email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("❌ [ASYNC] Failed to send password reset email to {}: {}",
                    to, e.getMessage(), e);
        }
    }
}
