package com.flower.manager.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO cho yêu cầu tạo thanh toán MoMo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MomoPaymentRequest {
    private Long orderId;
    private BigDecimal amount;
    private String orderInfo;
    private String momoType;
}
