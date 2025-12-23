package com.flower.manager.dto.voucher;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho phản hồi thông tin Voucher
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoucherDTO {

    private Long id;
    private String code;
    private String description;
    private Boolean isPercent;
    private BigDecimal discountValue;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isActive;
    private Boolean isValid; // Trạng thái hợp lệ tại thời điểm gọi
    private Boolean isExpired; // Đã hết hạn (endDate < now)
    private LocalDateTime createdAt;
}
