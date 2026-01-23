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

    /**
     * Loại voucher: ORDER hoặc SHIPPING
     */
    private String voucherType;

    private Boolean isPercent;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    /**
     * Số lượng voucher đã lưu (có thể dùng)
     */
    private Integer quantity;

    /**
     * Số lần đã sử dụng
     */
    private Integer usedCount;

    /**
     * Số lượt còn lại có thể sử dụng
     */
    private Integer remainingUses;

    /**
     * Trạng thái của saved voucher
     */
    private Boolean isAvailable; // Còn sử dụng được
    private Boolean isExpired; // Đã hết hạn
    private Boolean isUsed; // Đã sử dụng hết

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
