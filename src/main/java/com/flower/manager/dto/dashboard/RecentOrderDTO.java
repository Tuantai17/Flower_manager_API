package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO đơn hàng gần đây (dùng cho dashboard)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentOrderDTO {

    private Long id;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private BigDecimal finalPrice;
    private String status;
    private String statusDisplayName;
    private Boolean isPaid;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private Integer totalItems;
}
