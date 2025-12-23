package com.flower.manager.service.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service gửi email
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@flowershop.com}")
    private String fromEmail;

    /**
     * Gửi email xác nhận đơn hàng (Async)
     */
    @Async
    public void sendOrderEmail(String to, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Xác nhận đơn hàng - Flower Shop");
            message.setText(content);
            mailSender.send(message);
            log.info("Sent order confirmation email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gửi email thông báo cập nhật trạng thái đơn hàng
     */
    @Async
    public void sendOrderStatusEmail(String to, String orderCode, String newStatus) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Cập nhật đơn hàng " + orderCode + " - Flower Shop");
            message.setText("Đơn hàng " + orderCode + " của bạn đã được cập nhật sang trạng thái: " + newStatus);
            mailSender.send(message);
            log.info("Sent status update email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send status email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gửi email xác nhận thanh toán
     */
    @Async
    public void sendPaymentConfirmationEmail(String to, String orderCode, String amount) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Xác nhận thanh toán - Flower Shop");
            message.setText("Thanh toán đơn hàng " + orderCode + " với số tiền " + amount
                    + " VNĐ đã được xác nhận. Cảm ơn bạn!");
            mailSender.send(message);
            log.info("Sent payment confirmation email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send payment email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gửi email đặt lại mật khẩu (Async)
     * 
     * @param to          Email người nhận
     * @param resetToken  Token reset password
     * @param frontendUrl URL frontend (vd: http://localhost:3000)
     */
    @Async
    public void sendPasswordResetEmail(String to, String resetToken, String frontendUrl) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + resetToken + "&email=" + to;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Đặt lại mật khẩu - Flower Shop");
            message.setText(
                    "Xin chào,\n\n" +
                            "Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Flower Shop.\n\n" +
                            "Vui lòng click vào link sau để đặt lại mật khẩu:\n" +
                            resetLink + "\n\n" +
                            "Link này sẽ hết hạn sau 30 phút.\n\n" +
                            "Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.\n\n" +
                            "Trân trọng,\n" +
                            "Flower Shop Team");
            mailSender.send(message);
            log.info("Sent password reset email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gửi email xác nhận đã đổi mật khẩu thành công (Async)
     * 
     * @param to Email người nhận
     */
    @Async
    public void sendPasswordChangedEmail(String to) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Mật khẩu đã được thay đổi - Flower Shop");
            message.setText(
                    "Xin chào,\n\n" +
                            "Mật khẩu tài khoản Flower Shop của bạn đã được thay đổi thành công.\n\n" +
                            "Nếu bạn không thực hiện thay đổi này, vui lòng liên hệ ngay với chúng tôi.\n\n" +
                            "Trân trọng,\n" +
                            "Flower Shop Team");
            mailSender.send(message);
            log.info("Sent password changed confirmation email to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password changed email to {}: {}", to, e.getMessage());
        }
    }
}
