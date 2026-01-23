package com.flower.manager.service.voucher;

import com.flower.manager.dto.voucher.SavedVoucherDTO;
import com.flower.manager.entity.SavedVoucher;
import com.flower.manager.entity.User;
import com.flower.manager.entity.Voucher;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.SavedVoucherRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation của SavedVoucherService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SavedVoucherServiceImpl implements SavedVoucherService {

    private final SavedVoucherRepository savedVoucherRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;

    @Override
    public SavedVoucherDTO saveVoucher(Long voucherId) {
        User user = getCurrentUser();
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", voucherId));

        // Kiểm tra voucher còn hợp lệ
        if (!voucher.isValid()) {
            throw new BusinessException("VOUCHER_INVALID", "Voucher này không còn hiệu lực");
        }

        // Kiểm tra đã lưu chưa
        if (savedVoucherRepository.existsByUserIdAndVoucherId(user.getId(), voucherId)) {
            throw new BusinessException("VOUCHER_ALREADY_SAVED", "Bạn đã lưu voucher này rồi");
        }

        // Tạo saved voucher mới
        SavedVoucher savedVoucher = SavedVoucher.builder()
                .user(user)
                .voucher(voucher)
                .isAvailable(true)
                .build();

        savedVoucher = savedVoucherRepository.save(savedVoucher);
        log.info("User {} saved voucher {}", user.getUsername(), voucher.getCode());

        return mapToDTO(savedVoucher);
    }

    @Override
    public void unsaveVoucher(Long voucherId) {
        User user = getCurrentUser();
        savedVoucherRepository.deleteByUserIdAndVoucherId(user.getId(), voucherId);
        log.info("User {} unsaved voucher {}", user.getUsername(), voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isVoucherSaved(Long voucherId) {
        User user = getCurrentUser();
        return savedVoucherRepository.existsByUserIdAndVoucherId(user.getId(), voucherId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMySavedVouchers() {
        User user = getCurrentUser();
        return savedVoucherRepository.findByUserIdOrderBySavedAtDesc(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMyAvailableVouchers() {
        User user = getCurrentUser();
        return savedVoucherRepository.findAvailableByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMyExpiringVouchers() {
        User user = getCurrentUser();
        return savedVoucherRepository.findExpiringByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMyExpiredVouchers() {
        User user = getCurrentUser();
        return savedVoucherRepository.findExpiredByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMyUsedVouchers() {
        User user = getCurrentUser();
        return savedVoucherRepository.findUsedByUserId(user.getId())
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavedVoucherDTO> getMyVouchersByFilter(String filter) {
        return switch (filter != null ? filter.toLowerCase() : "all") {
            case "available" -> getMyAvailableVouchers();
            case "expiring" -> getMyExpiringVouchers();
            case "expired" -> getMyExpiredVouchers();
            case "used" -> getMyUsedVouchers();
            default -> getMySavedVouchers();
        };
    }

    @Override
    public void markVoucherAsUsed(Long userId, Long voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", voucherId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        savedVoucherRepository.findByUserAndVoucher(user, voucher)
                .ifPresent(sv -> {
                    // Sử dụng 1 lượt thay vì đánh dấu đã dùng hoàn toàn
                    if (sv.useOne()) {
                        savedVoucherRepository.save(sv);
                        log.info("Used 1 voucher {} for user {}. Remaining: {}",
                                voucher.getCode(), user.getUsername(), sv.getRemainingUses());
                    } else {
                        log.warn("Voucher {} has no remaining uses for user {}",
                                voucher.getCode(), user.getUsername());
                    }
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getVoucherCounts() {
        User user = getCurrentUser();
        Map<String, Long> counts = new HashMap<>();

        counts.put("available", savedVoucherRepository.countAvailableByUserId(user.getId()));
        counts.put("total", (long) savedVoucherRepository.findByUserIdOrderBySavedAtDesc(user.getId()).size());

        return counts;
    }

    // ============ HELPER METHODS ============

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Không tìm thấy thông tin người dùng"));
    }

    private SavedVoucherDTO mapToDTO(SavedVoucher sv) {
        Voucher v = sv.getVoucher();
        LocalDateTime now = LocalDateTime.now();

        boolean isExpired = v.getEndDate() != null && v.getEndDate().isBefore(now);

        // Tính số lượt còn lại
        int quantity = sv.getQuantity() != null ? sv.getQuantity() : 1;
        int usedCount = sv.getUsedCount() != null ? sv.getUsedCount() : 0;
        int remainingUses = Math.max(0, quantity - usedCount);

        // Đã sử dụng hết khi remainingUses = 0 hoặc isAvailable = false
        boolean isUsed = remainingUses == 0 || !sv.getIsAvailable();

        // Tính số ngày còn lại
        Integer daysRemaining = null;
        if (v.getEndDate() != null && !isExpired) {
            daysRemaining = (int) ChronoUnit.DAYS.between(now, v.getEndDate());
        }

        // Xác định status
        String status;
        if (isUsed) {
            status = "USED";
        } else if (isExpired) {
            status = "EXPIRED";
        } else if (daysRemaining != null && daysRemaining <= 3) {
            status = "EXPIRING";
        } else {
            status = "AVAILABLE";
        }

        return SavedVoucherDTO.builder()
                .id(sv.getId())
                .voucherId(v.getId())
                .code(v.getCode())
                .description(v.getDescription())
                .voucherType(v.getVoucherType() != null ? v.getVoucherType().name() : "ORDER")
                .isPercent(v.getIsPercent())
                .discountValue(v.getDiscountValue())
                .minOrderValue(v.getMinOrderValue())
                .maxDiscount(v.getMaxDiscount())
                .startDate(v.getStartDate())
                .endDate(v.getEndDate())
                .quantity(quantity)
                .usedCount(usedCount)
                .remainingUses(remainingUses)
                .isAvailable(!isUsed && !isExpired && v.isValid())
                .isExpired(isExpired)
                .isUsed(isUsed)
                .status(status)
                .savedAt(sv.getSavedAt())
                .usedAt(sv.getUsedAt())
                .daysRemaining(daysRemaining)
                .build();
    }
}
