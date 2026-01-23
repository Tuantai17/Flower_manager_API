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

/**
 * Implementation của NewsletterService
 * Sử dụng MỘT voucher chung cho tất cả thành viên mới
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NewsletterServiceImpl implements NewsletterService {

    private final NewsletterSubscriberRepository subscriberRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final SavedVoucherRepository savedVoucherRepository;

    // Cấu hình voucher welcome - MỘT MÃ CHUNG
    private static final String WELCOME_VOUCHER_CODE = "WELCOME30";
    private static final int WELCOME_DISCOUNT_PERCENT = 30;
    private static final BigDecimal MAX_DISCOUNT = new BigDecimal("100000");

    @Override
    @Transactional
    public NewsletterSubscribeResponse subscribe(String email) {
        // Normalize email
        String normalizedEmail = email.trim().toLowerCase();

        // Kiểm tra email đã đăng ký chưa
        if (subscriberRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException("Email này đã đăng ký nhận tin trước đó!");
        }

        // Lấy hoặc tạo voucher Welcome chung
        Voucher welcomeVoucher = getOrCreateWelcomeVoucher();

        // Lưu thông tin subscriber
        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .email(normalizedEmail)
                .voucherCode(WELCOME_VOUCHER_CODE)
                .isActive(true)
                .build();

        subscriberRepository.save(subscriber);
        log.info("New newsletter subscriber: {} - assigned voucher: {}", normalizedEmail, WELCOME_VOUCHER_CODE);

        // Tự động lưu voucher vào kho nếu user đã có tài khoản với email này
        boolean autoSaved = autoSaveVoucherToUserWallet(normalizedEmail, welcomeVoucher);

        String message = "Đăng ký thành công! Dùng mã " + WELCOME_VOUCHER_CODE + " để giảm " + WELCOME_DISCOUNT_PERCENT
                + "% cho đơn hàng đầu tiên!";
        if (autoSaved) {
            message += " (Đã lưu vào Kho Voucher của bạn)";
        }

        // Trả về response
        return NewsletterSubscribeResponse.builder()
                .email(normalizedEmail)
                .voucherCode(WELCOME_VOUCHER_CODE)
                .discountPercent(WELCOME_DISCOUNT_PERCENT)
                .maxDiscount(formatCurrency(MAX_DISCOUNT))
                .expiryDate(welcomeVoucher.getEndDate())
                .message(message)
                .build();
    }

    /**
     * Lấy hoặc tạo voucher Welcome chung
     * Voucher này dùng chung cho tất cả thành viên mới
     */
    private Voucher getOrCreateWelcomeVoucher() {
        // Tìm voucher Welcome trong database
        Optional<Voucher> existingVoucher = voucherRepository.findByCode(WELCOME_VOUCHER_CODE);

        if (existingVoucher.isPresent()) {
            log.debug("Using existing welcome voucher: {}", WELCOME_VOUCHER_CODE);
            return existingVoucher.get();
        }

        // Nếu chưa có, tạo mới voucher Welcome
        log.info("Creating new shared welcome voucher: {}", WELCOME_VOUCHER_CODE);

        LocalDateTime now = LocalDateTime.now();
        // Voucher có hiệu lực 1 năm, không giới hạn số lần sử dụng
        LocalDateTime expiryDate = now.plusYears(1);

        Voucher voucher = Voucher.builder()
                .code(WELCOME_VOUCHER_CODE)
                .description("Voucher chào mừng thành viên mới - Giảm " + WELCOME_DISCOUNT_PERCENT
                        + "% cho đơn hàng đầu tiên")
                .isPercent(true)
                .discountValue(new BigDecimal(WELCOME_DISCOUNT_PERCENT))
                .minOrderValue(BigDecimal.ZERO) // Không yêu cầu đơn tối thiểu
                .maxDiscount(MAX_DISCOUNT)
                .usageLimit(null) // Không giới hạn số lần sử dụng chung
                .usageCount(0)
                .startDate(now)
                .endDate(expiryDate)
                .isActive(true)
                .build();

        return voucherRepository.save(voucher);
    }

    /**
     * Tự động lưu voucher vào kho của user nếu email đã đăng ký tài khoản
     * Mỗi user chỉ có thể sử dụng voucher này MỘT LẦN
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
            } else {
                log.debug("User {} already has voucher {} in wallet", user.getEmail(), voucher.getCode());
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
     * Format số tiền thành chuỗi hiển thị
     */
    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f", amount) + "đ";
    }
}
