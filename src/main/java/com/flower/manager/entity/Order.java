package com.flower.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity đại diện cho Đơn hàng
 */
@Entity
@Table(name = "orders", indexes = {
        @Index(name = "idx_order_user", columnList = "user_id"),
        @Index(name = "idx_order_status", columnList = "status"),
        @Index(name = "idx_order_code", columnList = "order_code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false, length = 20)
    private String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    // ============ THÔNG TIN NGƯỜI GỬI ============
    @Column(name = "sender_name", nullable = false)
    private String senderName;

    @Column(name = "sender_phone", nullable = false)
    private String senderPhone;

    @Column(name = "sender_email")
    private String senderEmail;

    // ============ THÔNG TIN NGƯỜI NHẬN ============
    @Column(name = "recipient_name", nullable = false)
    private String recipientName;

    @Column(name = "recipient_phone", nullable = false)
    private String recipientPhone;

    // ============ ĐỊA CHỈ GIAO HÀNG (CHUẨN HÓA) ============
    @Column(name = "province", nullable = false)
    private String province; // Tỉnh/Thành phố

    @Column(name = "district", nullable = false)
    private String district; // Quận/Huyện

    @Column(name = "address_detail", nullable = false)
    private String addressDetail; // Số nhà, tên đường

    // ============ LỊCH GIAO HÀNG ============
    @Column(name = "delivery_date")
    private LocalDate deliveryDate; // Ngày giao hàng

    @Column(name = "delivery_time")
    private String deliveryTime; // Khung giờ giao (VD: "16:00 - 20:00")

    // ============ GHI CHÚ & ĐỊA CHỈ GỘP ============
    @Column(name = "shipping_address")
    private String shippingAddress; // Địa chỉ đầy đủ (auto-generate từ các trường trên)

    // ============ TỌA ĐỘ ĐỊA LÝ (OSM/PHOTON) ============
    @Column(name = "lat")
    private Double lat; // Latitude

    @Column(name = "lng")
    private Double lng; // Longitude

    @Column(name = "geo_provider", length = 20)
    private String geoProvider; // Provider: PHOTON, GOOGLE, MAPBOX

    @Column(name = "place_id", length = 120)
    private String placeId; // Place ID (for Google/Mapbox)

    @Column(name = "note")
    private String note; // Lời nhắn cho người nhận

    // Giá trị đơn hàng
    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "final_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal finalPrice;

    // Voucher
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    @JsonIgnore
    private Voucher voucher;

    // Thanh toán
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Column(name = "is_paid")
    @Builder.Default
    private Boolean isPaid = false;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "transaction_id")
    private String transactionId;

    // Trạng thái
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "cancelled_reason")
    private String cancelledReason;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Thời gian
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Quan hệ với OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (orderCode == null) {
            orderCode = generateOrderCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Tạo mã đơn hàng duy nhất
     */
    private String generateOrderCode() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD" + timestamp + uuid;
    }

    /**
     * Tính tổng số lượng sản phẩm
     */
    public int getTotalQuantity() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /**
     * Kiểm tra có thể hủy đơn không
     * Chỉ cho phép hủy khi đơn hàng đang ở trạng thái PENDING (chờ xác nhận)
     * Sau khi đã xác nhận (CONFIRMED), không thể hủy nữa
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING;
    }
}
