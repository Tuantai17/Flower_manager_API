package com.flower.manager.dto.shipping;

import com.flower.manager.enums.ShippingZone;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO cho API tính phí vận chuyển
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCalculateResponse {

    /**
     * ID của rule được áp dụng
     */
    private Long ruleId;

    /**
     * Khu vực (INNER / OUTER)
     */
    private ShippingZone zone;

    /**
     * Tên khu vực hiển thị (Nội thành / Ngoại thành)
     */
    private String zoneName;

    /**
     * Phí vận chuyển gốc (trước khi áp miễn phí)
     */
    private Integer originalFee;

    /**
     * Phí vận chuyển thực tế (0 nếu miễn phí)
     */
    private Integer shippingFee;

    /**
     * Đơn vị tiền tệ
     */
    @Builder.Default
    private String currency = "VND";

    /**
     * Có được miễn phí ship không
     */
    private Boolean isFreeShip;

    /**
     * Ngưỡng miễn phí ship
     */
    private Integer freeShipThreshold;

    /**
     * Số tiền còn thiếu để được miễn phí
     */
    private Integer amountToFreeShip;

    /**
     * Thời gian giao dự kiến
     */
    private String estimatedTime;

    /**
     * Chi tiết breakdown phí (optional)
     */
    private ShippingBreakdown breakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingBreakdown {
        private Integer baseFee;
        private Integer peakFee;
        private Double holidayMultiplier;
        private Boolean isPeakHour;
        private Boolean isHoliday;
    }
}
