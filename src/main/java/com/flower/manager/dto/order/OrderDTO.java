package com.flower.manager.dto.order;

import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho thông tin Đơn hàng
 * Cấu trúc theo UI: Người gửi, Người nhận, Địa chỉ chi tiết, Lịch giao hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDTO {

    private Long id;
    private String orderCode;
    private Long userId;

    // ============ THÔNG TIN NGƯỜI GỬI ============
    private String senderName;
    private String senderPhone;
    private String senderEmail;

    // ============ THÔNG TIN NGƯỜI NHẬN ============
    private String recipientName;
    private String recipientPhone;

    // ============ ĐỊA CHỈ GIAO HÀNG (CHUẨN HÓA) ============
    private String province; // Tỉnh/Thành phố
    private String district; // Quận/Huyện
    private String addressDetail; // Số nhà, tên đường
    private String shippingAddress; // Địa chỉ đầy đủ

    // ============ TỌA ĐỘ ĐỊA LÝ (OSM/PHOTON) ============
    private Double lat; // Latitude
    private Double lng; // Longitude
    private String geoProvider; // Provider: PHOTON, GOOGLE, MAPBOX
    private String placeId; // Place ID (for Google/Mapbox)

    // ============ LỊCH GIAO HÀNG ============
    private LocalDate deliveryDate;
    private String deliveryTime;

    // ============ GHI CHÚ ============
    private String note;

    // ============ GIÁ TRỊ ============
    private BigDecimal totalPrice;
    private BigDecimal discountAmount;
    private BigDecimal shippingFee;
    private BigDecimal finalPrice;

    // ============ VOUCHER ============
    private String voucherCode;

    // ============ THANH TOÁN ============
    private PaymentMethod paymentMethod;
    private Boolean isPaid;
    private LocalDateTime paidAt;
    private String paymentUrl; // URL thanh toán (MoMo, VNPay)

    // ============ TRẠNG THÁI ============
    private OrderStatus status;
    private String statusDisplayName;
    private String cancelledReason;

    // ============ THỜI GIAN ============
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ============ CHI TIẾT SẢN PHẨM ============
    private List<OrderItemDTO> items;
    private Integer totalQuantity;

    // ============ ACTIONS ============
    private Boolean cancellable;
}
