package com.flower.manager.dto.order;

import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho thông tin Đơn hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private String orderCode;

    // Thông tin khách hàng
    private Long userId;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String shippingAddress;
    private String note;

    // Giá trị
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalPrice;

    // Voucher
    private String voucherCode;

    // Thanh toán
    private PaymentMethod paymentMethod;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    private String paymentUrl; // URL thanh toán (MoMo, VNPay)

    // Trạng thái
    private OrderStatus status;
    private String statusDisplayName;
    private String cancelledReason;

    // Thời gian
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Chi tiết sản phẩm
    private List<OrderItemDTO> items;
    private Integer totalQuantity;

    // Có thể hủy đơn không
    private Boolean cancellable;
}
