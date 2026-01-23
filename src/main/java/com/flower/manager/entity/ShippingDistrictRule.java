package com.flower.manager.entity;

import com.flower.manager.enums.DeliveryType;
import com.flower.manager.enums.ShippingZone;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đại diện cho quy tắc phí vận chuyển theo quận/huyện
 * Mỗi quận/huyện có 1 rule riêng với các thông số: phí, ngưỡng miễn phí, thời
 * gian giao
 */
@Entity
@Table(name = "shipping_district_rules", indexes = {
        @Index(name = "idx_active_city", columnList = "active, city"),
        @Index(name = "idx_zone", columnList = "zone")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_district_delivery", columnNames = { "city", "district", "delivery_type" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingDistrictRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false, length = 50)
    @Builder.Default
    private String city = "TPHCM";

    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone", nullable = false)
    private ShippingZone zone;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type", nullable = false)
    @Builder.Default
    private DeliveryType deliveryType = DeliveryType.STANDARD;

    /**
     * Phí vận chuyển cơ bản (VND)
     */
    @Column(name = "base_fee", nullable = false)
    private Integer baseFee;

    /**
     * Ngưỡng miễn phí vận chuyển (VND)
     * Nếu đơn hàng >= giá trị này thì miễn phí ship
     */
    @Column(name = "free_ship_threshold", nullable = false)
    private Integer freeShipThreshold;

    /**
     * Thời gian giao hàng dự kiến (VD: "2-4 giờ", "1 ngày")
     */
    @Column(name = "estimated_time", nullable = false, length = 30)
    private String estimatedTime;

    /**
     * Phí cao điểm (cộng thêm vào base_fee)
     */
    @Column(name = "peak_fee")
    @Builder.Default
    private Integer peakFee = 0;

    /**
     * Hệ số nhân ngày lễ (VD: 1.5 = tăng 50%)
     */
    @Column(name = "holiday_multiplier", precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal holidayMultiplier = BigDecimal.ONE;

    /**
     * Trạng thái hoạt động
     */
    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Tính phí vận chuyển dựa trên tổng tiền đơn hàng
     * 
     * @param subtotal   Tổng tiền hàng
     * @param isPeakHour Có phải giờ cao điểm không
     * @param isHoliday  Có phải ngày lễ không
     * @return Phí vận chuyển (0 nếu miễn phí)
     */
    public int calculateShippingFee(int subtotal, boolean isPeakHour, boolean isHoliday) {
        // Nếu đạt ngưỡng miễn phí
        if (subtotal >= freeShipThreshold) {
            return 0;
        }

        // Tính phí cơ bản
        int fee = baseFee;

        // Cộng phí cao điểm
        if (isPeakHour && peakFee != null) {
            fee += peakFee;
        }

        // Nhân hệ số ngày lễ
        if (isHoliday && holidayMultiplier != null) {
            fee = BigDecimal.valueOf(fee)
                    .multiply(holidayMultiplier)
                    .intValue();
        }

        return fee;
    }

    /**
     * Kiểm tra xem có được miễn phí ship không
     */
    public boolean isFreeShip(int subtotal) {
        return subtotal >= freeShipThreshold;
    }

    /**
     * Tính số tiền còn thiếu để được miễn phí ship
     */
    public int getAmountToFreeShip(int subtotal) {
        return Math.max(0, freeShipThreshold - subtotal);
    }
}
