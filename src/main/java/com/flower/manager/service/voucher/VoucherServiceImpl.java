package com.flower.manager.service.voucher;

import com.flower.manager.dto.voucher.VoucherDTO;
import com.flower.manager.dto.voucher.VoucherRequest;
import com.flower.manager.entity.Voucher;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation của VoucherService
 * 
 * Các quy tắc:
 * - Voucher hết hạn: endDate < now
 * - Voucher bị ẩn: isActive = false
 * - Voucher hết lượt: usageCount >= usageLimit
 * - Voucher còn hiệu lực: isActive = true AND chưa hết hạn AND còn lượt
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    // ================= READ METHODS =================

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getAllVouchers(String keyword, Pageable pageable) {
        log.info("Getting ALL vouchers (including expired/hidden) with keyword: {}", keyword);
        return voucherRepository.searchVouchers(keyword, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getActiveVouchersForAdmin(String keyword, Pageable pageable) {
        log.info("Getting ACTIVE vouchers for admin with keyword: {}", keyword);
        LocalDateTime now = LocalDateTime.now();
        return voucherRepository.searchActiveVouchers(keyword, now, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> getValidVouchers() {
        log.info("Getting valid vouchers for public");
        return voucherRepository.findValidVouchers(LocalDateTime.now())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getVoucherById(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));
        return mapToDTO(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getVoucherByCode(String code) {
        Voucher voucher = voucherRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new BusinessException("VOUCHER_NOT_FOUND", "Mã giảm giá không tồn tại"));
        return mapToDTO(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getVoucherStats() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Long> stats = new HashMap<>();

        long activeCount = voucherRepository.countActiveVouchers(now);
        long hiddenCount = voucherRepository.countExpiredOrHiddenVouchers(now);
        long totalCount = voucherRepository.count();

        stats.put("activeCount", activeCount);
        stats.put("hiddenCount", hiddenCount);
        stats.put("totalCount", totalCount);

        log.info("Voucher stats: active={}, hidden={}, total={}", activeCount, hiddenCount, totalCount);
        return stats;
    }

    // ================= CREATE & UPDATE METHODS =================

    @Override
    public VoucherDTO createVoucher(VoucherRequest request) {
        String code = request.getCode().trim().toUpperCase();

        if (voucherRepository.existsByCode(code)) {
            throw new BusinessException("VOUCHER_EXISTS", "Mã voucher '" + code + "' đã tồn tại");
        }

        Voucher voucher = Voucher.builder()
                .code(code)
                .description(request.getDescription())
                .isPercent(request.getIsPercent() != null ? request.getIsPercent() : false)
                .discountValue(request.getDiscountValue())
                .minOrderValue(
                        request.getMinOrderValue() != null ? request.getMinOrderValue() : java.math.BigDecimal.ZERO)
                .maxDiscount(request.getMaxDiscount())
                .usageLimit(request.getUsageLimit())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .usageCount(0)
                .build();

        Voucher savedVoucher = voucherRepository.save(voucher);
        log.info("Created new voucher: {}", savedVoucher.getCode());
        return mapToDTO(savedVoucher);
    }

    @Override
    public VoucherDTO updateVoucher(Long id, VoucherRequest request) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));

        String newCode = request.getCode().trim().toUpperCase();

        // Kiểm tra mã voucher mới có trùng với voucher khác không
        if (!voucher.getCode().equalsIgnoreCase(newCode) && voucherRepository.existsByCode(newCode)) {
            throw new BusinessException("VOUCHER_EXISTS", "Mã voucher '" + newCode + "' đã tồn tại");
        }

        voucher.setCode(newCode);
        voucher.setDescription(request.getDescription());
        voucher.setIsPercent(request.getIsPercent());
        voucher.setDiscountValue(request.getDiscountValue());
        voucher.setMinOrderValue(request.getMinOrderValue());
        voucher.setMaxDiscount(request.getMaxDiscount());
        voucher.setUsageLimit(request.getUsageLimit());
        voucher.setStartDate(request.getStartDate());
        voucher.setEndDate(request.getEndDate());
        voucher.setIsActive(request.getIsActive());

        Voucher updatedVoucher = voucherRepository.save(voucher);
        log.info("Updated voucher: {}", updatedVoucher.getCode());
        return mapToDTO(updatedVoucher);
    }

    // ================= DELETE METHODS =================

    @Override
    public void hideVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));

        // Soft delete: Đánh dấu voucher là không hoạt động
        voucher.setIsActive(false);
        voucherRepository.save(voucher);
        log.info("Hidden voucher: {} (id: {}) - set isActive = false", voucher.getCode(), id);
    }

    @Override
    public void deleteVoucher(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));

        // Hard delete - CHỈ dùng khi thực sự cần xóa vĩnh viễn
        // Cảnh báo: Có thể gây lỗi Foreign Key nếu voucher đã được sử dụng trong đơn
        // hàng
        voucherRepository.delete(voucher);
        log.warn("PERMANENTLY deleted voucher: {} (id: {})", voucher.getCode(), id);
    }

    // ================= STATUS METHODS =================

    @Override
    public VoucherDTO toggleStatus(Long id) {
        Voucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher", "id", id));

        boolean newStatus = !voucher.getIsActive();
        voucher.setIsActive(newStatus);
        Voucher updatedVoucher = voucherRepository.save(voucher);

        log.info("Toggled voucher {} status to {}", voucher.getCode(), newStatus ? "ACTIVE" : "HIDDEN");
        return mapToDTO(updatedVoucher);
    }

    // ================= HELPER METHODS =================

    /**
     * Map Entity to DTO
     * Tự động tính:
     * - isValid: voucher có thể sử dụng được không
     * - isExpired: voucher đã hết hạn chưa
     */
    private VoucherDTO mapToDTO(Voucher voucher) {
        LocalDateTime now = LocalDateTime.now();

        // Kiểm tra hết hạn
        boolean isExpired = voucher.getEndDate() != null && voucher.getEndDate().isBefore(now);

        // Kiểm tra hết lượt sử dụng
        boolean isUsageFull = voucher.getUsageLimit() != null &&
                voucher.getUsageCount() >= voucher.getUsageLimit();

        return VoucherDTO.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .description(voucher.getDescription())
                .isPercent(voucher.getIsPercent())
                .discountValue(voucher.getDiscountValue())
                .minOrderValue(voucher.getMinOrderValue())
                .maxDiscount(voucher.getMaxDiscount())
                .usageLimit(voucher.getUsageLimit())
                .usageCount(voucher.getUsageCount())
                .startDate(voucher.getStartDate())
                .endDate(voucher.getEndDate())
                .isActive(voucher.getIsActive())
                .isValid(voucher.isValid()) // Tính từ entity method
                .isExpired(isExpired || isUsageFull) // Hết hạn HOẶC hết lượt
                .createdAt(voucher.getCreatedAt())
                .build();
    }
}
