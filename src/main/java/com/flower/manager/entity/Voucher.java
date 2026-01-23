package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho Mã giảm giá (Voucher)
 */
@Entity
@Table(name = "vouchers", indexes = {
        @Index(name = "idx_voucher_code", columnList = "code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "description")
    private String description;

    /**
     * Loại voucher: ORDER (giảm tiền hàng) hoặc SHIPPING (giảm phí ship)
     * Mặc định là ORDER
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "voucher_type", length = 20)
    @Builder.Default
    private com.flower.manager.enums.VoucherType voucherType = com.flower.manager.enums.VoucherType.ORDER;

    /**
     * Loại giảm giá: true = phần trăm, false = số tiền cố định
     */
    @Column(name = "is_percent", nullable = false)
    @Builder.Default
    private Boolean isPercent = false;

    /**
     * Giá trị giảm (VD: 10 = 10% hoặc 50000 = 50,000đ)
     */
    @Column(name = "discount_value", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountValue;

    /**
     * Giá trị đơn hàng tối thiểu để áp dụng
     */
    @Column(name = "min_order_value", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    /**
     * Số tiền giảm tối đa (cho voucher %)
     */
    @Column(name = "max_discount", precision = 12, scale = 2)
    private BigDecimal maxDiscount;

    /**
     * Số lượt sử dụng tối đa
     */
    @Column(name = "usage_limit")
    private Integer usageLimit;

    /**
     * Số lượt đã sử dụng
     */
    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 0;

    /**
     * Ngày bắt đầu hiệu lực
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Ngày hết hạn
     */
    @Column(name = "end_date")
    private LocalDateTime endDate;

    /**
     * Trạng thái hoạt động
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra voucher còn hiệu lực
     */
    public boolean isValid() {
        if (!isActive)
            return false;

        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate))
            return false;
        if (endDate != null && now.isAfter(endDate))
            return false;
        if (usageLimit != null && usageCount >= usageLimit)
            return false;

        return true;
    }

    /**
     * Tính số tiền giảm giá
     */
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        if (!isValid())
            return BigDecimal.ZERO;
        if (minOrderValue != null && orderTotal.compareTo(minOrderValue) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount;
        if (Boolean.TRUE.equals(isPercent)) {
            discount = orderTotal.multiply(discountValue).divide(BigDecimal.valueOf(100));
            if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
                discount = maxDiscount;
            }
        } else {
            discount = discountValue;
        }

        return discount.min(orderTotal);
    }

    /**
     * Sử dụng voucher (tăng usageCount)
     */
    public void use() {
        this.usageCount++;
    }
}
