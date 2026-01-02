package com.flower.manager.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho SavedVoucher - Voucher đã lưu của user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedVoucherDTO {

    private Long id;
    private Long voucherId;
    private String code;
    private String description;

    private Boolean isPercent;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Trạng thái của saved voucher
     */
    private Boolean isAvailable; // Chưa sử dụng
    private Boolean isExpired; // Đã hết hạn
    private Boolean isUsed; // Đã sử dụng

    /**
     * Status text để hiển thị: AVAILABLE, EXPIRING, EXPIRED, USED
     */
    private String status;

    private LocalDateTime savedAt;
    private LocalDateTime usedAt;

    /**
     * Số ngày còn lại trước khi hết hạn (-1 nếu không có hạn)
     */
    private Integer daysRemaining;
}
