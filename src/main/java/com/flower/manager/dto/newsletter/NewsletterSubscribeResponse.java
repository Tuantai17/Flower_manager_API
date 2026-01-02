package com.flower.manager.dto.newsletter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO cho đăng ký newsletter thành công
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsletterSubscribeResponse {

    private String email;

    /**
     * Mã voucher được tạo
     */
    private String voucherCode;

    /**
     * Phần trăm giảm giá
     */
    private Integer discountPercent;

    /**
     * Số tiền giảm tối đa
     */
    private String maxDiscount;

    /**
     * Ngày hết hạn voucher
     */
    private LocalDateTime expiryDate;

    /**
     * Thông báo cho user
     */
    private String message;
}
