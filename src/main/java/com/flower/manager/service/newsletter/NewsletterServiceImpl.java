package com.flower.manager.service.newsletter;

import com.flower.manager.dto.newsletter.NewsletterSubscribeResponse;
import com.flower.manager.entity.NewsletterSubscriber;
import com.flower.manager.entity.SavedVoucher;
import com.flower.manager.entity.User;
import com.flower.manager.entity.Voucher;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.repository.NewsletterSubscriberRepository;
import com.flower.manager.repository.SavedVoucherRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation của NewsletterService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final SavedVoucherRepository savedVoucherRepository;

    // Cấu hình voucher welcome
    private static final int WELCOME_DISCOUNT_PERCENT = 30;
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("100000");
    private static final int VOUCHER_VALID_DAYS = 30;

    @Override
    @Transactional
    public NewsletterSubscribeResponse subscribe(String email) {
        // Normalize email
        String normalizedEmail = email.trim().toLowerCase();

        // Kiểm tra email đã đăng ký chưa
        if (subscriberRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException("Email này đã đăng ký nhận tin trước đó!");
        }

        // Tạo mã voucher unique
        String voucherCode = generateUniqueVoucherCode();

        // Tính ngày hết hạn
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryDate = now.plusDays(VOUCHER_VALID_DAYS);

        // Tạo voucher trong database
        Voucher voucher = Voucher.builder()
                .code(voucherCode)
                .description("Voucher chào mừng - Giảm " + WELCOME_DISCOUNT_PERCENT + "% cho đơn hàng đầu tiên")
                .isPercent(true)
                .discountValue(new BigDecimal(WELCOME_DISCOUNT_PERCENT))
                .minOrderValue(BigDecimal.ZERO) // Không yêu cầu đơn tối thiểu
                .maxDiscount(MAX_DISCOUNT)
                .usageLimit(1) // Chỉ dùng 1 lần
                .usageCount(0)
                .startDate(now)
                .endDate(expiryDate)
                .isActive(true)
                .build();

        voucherRepository.save(voucher);
        log.info("Created welcome voucher: {} for email: {}", voucherCode, normalizedEmail);

        // Lưu thông tin subscriber
        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .email(normalizedEmail)
                .voucherCode(voucherCode)
                .isActive(true)
                .build();

        subscriberRepository.save(subscriber);
        log.info("New newsletter subscriber: {}", normalizedEmail);

        // Tự động lưu voucher vào kho nếu user đã có tài khoản với email này
        boolean autoSaved = autoSaveVoucherToUserWallet(normalizedEmail, voucher);

        String message = "Đăng ký thành công! Mã giảm giá " + WELCOME_DISCOUNT_PERCENT + "% của bạn: " + voucherCode;
        if (autoSaved) {
            message += " (Đã lưu vào Kho Voucher của bạn)";
        }

        // Trả về response
        return NewsletterSubscribeResponse.builder()
                .email(normalizedEmail)
                .voucherCode(voucherCode)
                .discountPercent(WELCOME_DISCOUNT_PERCENT)
                .maxDiscount(formatCurrency(MAX_DISCOUNT))
                .expiryDate(expiryDate)
                .message(message)
                .build();
    }

    /**
     * Tự động lưu voucher vào kho của user nếu email đã đăng ký tài khoản
     * 
     * @return true nếu đã lưu thành công, false nếu không tìm thấy user
     */
    private boolean autoSaveVoucherToUserWallet(String email, Voucher voucher) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Kiểm tra chưa lưu voucher này
            if (!savedVoucherRepository.existsByUserIdAndVoucherId(user.getId(), voucher.getId())) {
                SavedVoucher savedVoucher = SavedVoucher.builder()
                        .user(user)
                        .voucher(voucher)
                        .isAvailable(true)
                        .build();

                savedVoucherRepository.save(savedVoucher);
                log.info("Auto-saved welcome voucher {} to user {}'s wallet", voucher.getCode(), user.getEmail());
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isEmailSubscribed(String email) {
        return subscriberRepository.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    @Transactional
    public void unsubscribe(String email) {
        String normalizedEmail = email.trim().toLowerCase();
        subscriberRepository.findByEmail(normalizedEmail).ifPresent(subscriber -> {
            subscriber.setIsActive(false);
            subscriber.setUnsubscribedAt(LocalDateTime.now());
            subscriberRepository.save(subscriber);
            log.info("Unsubscribed email: {}", normalizedEmail);
        });
    }

    /**
     * Tạo mã voucher unique với format WELCOME-XXXX
     */
    private String generateUniqueVoucherCode() {
        String code;
        int attempts = 0;
        do {
            // Format: WELCOME-XXXX (4 ký tự random)
            String randomPart = UUID.randomUUID().toString()
                    .replace("-", "")
                    .substring(0, 4)
                    .toUpperCase();
            code = "WELCOME-" + randomPart;
            attempts++;

            if (attempts > 10) {
                // Nếu quá nhiều lần thử, dùng format dài hơn
                code = "WELCOME-" + UUID.randomUUID().toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();
                break;
            }
        } while (voucherRepository.existsByCode(code));

        return code;
    }

    /**
     * Format số tiền thành chuỗi hiển thị
     */
    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f", amount) + "đ";
    }
}
