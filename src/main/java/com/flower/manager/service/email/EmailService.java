package com.flower.manager.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    /**
     * Gửi email HTML (Sync - để có thể bắt exception)
     * 
     * @param to       Email người nhận
     * @param subject  Tiêu đề email
     * @param htmlBody Nội dung HTML
     */
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true); // true = isHtml

        mailSender.send(mimeMessage);
        log.info("Sent HTML email to: {}", to);
    }

    /**
     * Gửi email HTML (Async - không throw exception)
     * 
     * @param to       Email người nhận
     * @param subject  Tiêu đề email
     * @param htmlBody Nội dung HTML
     */
    @Async
    public void sendHtmlEmailAsync(String to, String subject, String htmlBody) {
        try {
            sendHtmlEmail(to, subject, htmlBody);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
        }
    }

    /**
     * Gửi email đặt lại mật khẩu (SYNC - Throw exception nếu thất bại)
     * Sử dụng method này khi cần biết chắc email đã gửi thành công hay không
     * 
     * @param to          Email người nhận
     * @param resetToken  Token reset password
     * @param frontendUrl URL frontend (vd: http://localhost:3000)
     * @throws MessagingException nếu gửi email thất bại
     */
    public void sendPasswordResetEmailSync(String to, String resetToken, String frontendUrl) throws MessagingException {
        log.info("Attempting to send password reset email to: {}", to);

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken + "&email=" + to;

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Đặt lại mật khẩu - Flower Shop");

        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background: linear-gradient(135deg, #ff6b6b, #ee5a5a); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                        .content { background: #f9f9f9; padding: 30px; border: 1px solid #ddd; }
                        .button { display: inline-block; background: #ff6b6b; color: white !important; text-decoration: none; padding: 12px 30px; border-radius: 5px; margin: 20px 0; font-weight: bold; }
                        .button:hover { background: #ee5a5a; }
                        .footer { text-align: center; padding: 20px; color: #666; font-size: 12px; }
                        .warning { background: #fff3cd; border: 1px solid #ffc107; padding: 10px; border-radius: 5px; margin-top: 15px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🌸 Flower Shop</h1>
                        </div>
                        <div class="content">
                            <h2>Xin chào!</h2>
                            <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản Flower Shop của mình.</p>
                            <p>Vui lòng click vào nút bên dưới để đặt lại mật khẩu:</p>
                            <center>
                                <a href="%s" class="button">Đặt Lại Mật Khẩu</a>
                            </center>
                            <div class="warning">
                                <strong>⚠️ Lưu ý:</strong>
                                <ul>
                                    <li>Link này sẽ hết hạn sau <strong>30 phút</strong></li>
                                    <li>Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</li>
                                </ul>
                            </div>
                            <p style="margin-top: 20px; font-size: 12px; color: #666;">
                                Nếu nút không hoạt động, hãy copy link sau vào trình duyệt:<br>
                                <a href="%s">%s</a>
                            </p>
                        </div>
                        <div class="footer">
                            <p>Trân trọng,<br><strong>Flower Shop Team</strong></p>
                            <p>© 2024 Flower Shop. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(resetLink, resetLink, resetLink);

        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
        log.info("Password reset email sent successfully to: {}", to);
    }
}
