package com.flower.manager.service.auth;

import com.flower.manager.entity.EmailVerificationToken;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.enums.ErrorCode;
import com.flower.manager.repository.EmailVerificationTokenRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service xử lý xác thực email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${email.verification.expiration-minutes:1440}")
    private int expirationMinutes;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Tạo token xác thực email và gửi email
     * Sử dụng REQUIRES_NEW để tách biệt transaction với parent
     * Không throw exception nếu gửi email thất bại để không block đăng ký
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void sendVerificationEmail(User user) {
        log.info("Sending verification email to: {}", user.getEmail());

        try {
            // Xóa token cũ nếu có
            tokenRepository.deleteByUser(user);

            // Tạo token mới
            String token = generateToken();
            EmailVerificationToken verificationToken = EmailVerificationToken.builder()
                    .token(token)
                    .user(user)
                    .expiresAt(LocalDateTime.now().plusMinutes(expirationMinutes))
                    .build();

            tokenRepository.save(verificationToken);

            // Gửi email (không throw exception nếu thất bại)
            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            sendEmailSafe(user, verificationLink);

            log.info("Verification email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", user.getEmail(), e.getMessage());
            // KHÔNG throw exception để không block đăng ký
        }
    }

    /**
     * Gửi lại email xác thực
     */
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "Email đã được xác thực trước đó");
        }

        sendVerificationEmail(user);
    }

    /**
     * Xác thực email bằng token
     */
    @Transactional
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);

        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "Token không tồn tại"));

        if (verificationToken.isConfirmed()) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Email đã được xác thực trước đó");
        }

        if (verificationToken.isExpired()) {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED,
                    "Token đã hết hạn. Vui lòng yêu cầu gửi lại email xác thực");
        }

        // Đánh dấu token đã sử dụng
        verificationToken.setConfirmedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);

        // Cập nhật user
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());
    }

    /**
     * Kiểm tra user đã xác thực email chưa
     */
    public boolean isEmailVerified(Long userId) {
        return userRepository.findById(userId)
                .map(user -> Boolean.TRUE.equals(user.getEmailVerified()))
                .orElse(false);
    }

    /**
     * Tạo token ngẫu nhiên
     */
    private String generateToken() {
        return UUID.randomUUID().toString().replace("-", "") +
                UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    /**
     * Gửi email xác thực - không throw exception nếu thất bại
     */
    private void sendEmailSafe(User user, String verificationLink) {
        String subject = "🌸 FlowerCorner - Xác thực địa chỉ email";

        String body = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; margin: 0; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 16px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
                        .header { background: linear-gradient(135deg, #ec4899, #f472b6); padding: 30px; text-align: center; }
                        .header h1 { color: white; margin: 0; font-size: 28px; }
                        .content { padding: 40px 30px; }
                        .greeting { font-size: 18px; color: #374151; margin-bottom: 20px; }
                        .message { color: #6b7280; line-height: 1.6; margin-bottom: 30px; }
                        .btn { display: inline-block; background: linear-gradient(135deg, #ec4899, #f472b6); color: white !important; padding: 14px 40px; text-decoration: none; border-radius: 8px; font-weight: 600; font-size: 16px; }
                        .btn:hover { opacity: 0.9; }
                        .link-text { color: #9ca3af; font-size: 12px; margin-top: 20px; word-break: break-all; }
                        .footer { background: #f9fafb; padding: 20px; text-align: center; color: #9ca3af; font-size: 12px; }
                        .warning { background: #fef3c7; border-left: 4px solid #f59e0b; padding: 12px; margin: 20px 0; border-radius: 4px; color: #92400e; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🌸 Xác thực Email</h1>
                        </div>
                        <div class="content">
                            <p class="greeting">Xin chào <strong>%s</strong>,</p>
                            <p class="message">
                                Cảm ơn bạn đã đăng ký tài khoản tại <strong>FlowerCorner</strong>!<br><br>
                                Vui lòng nhấn vào nút bên dưới để xác thực địa chỉ email của bạn:
                            </p>
                            <div style="text-align: center; margin: 30px 0;">
                                <a href="%s" class="btn">Xác thực Email</a>
                            </div>
                            <div class="warning">
                                ⏰ Link này sẽ hết hạn sau <strong>24 giờ</strong>. Nếu bạn không yêu cầu đăng ký, vui lòng bỏ qua email này.
                            </div>
                            <p class="link-text">
                                Nếu nút không hoạt động, copy và dán link này vào trình duyệt:<br>
                                <a href="%s" style="color: #ec4899;">%s</a>
                            </p>
                        </div>
                        <div class="footer">
                            © 2024 FlowerCorner. Tất cả quyền được bảo lưu.<br>
                            Email này được gửi tự động, vui lòng không trả lời.
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(
                        user.getFullName() != null ? user.getFullName() : user.getUsername(),
                        verificationLink,
                        verificationLink,
                        verificationLink);

        try {
            // Sử dụng ASYNC để không block đăng ký
            emailService.sendHtmlEmailAsync(user.getEmail(), subject, body);
        } catch (Exception e) {
            // CHỈ LOG, KHÔNG THROW - để không block đăng ký
            log.warn("Could not send verification email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
