package com.flower.manager.dto.shipping;

import com.flower.manager.enums.DeliveryType;
import com.flower.manager.enums.ShippingZone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho Shipping District Rule
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDistrictRuleDTO {

    private Long id;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district;

    @NotNull(message = "Loại giao hàng không được để trống")
    private DeliveryType deliveryType;

    @NotNull(message = "Phí cơ bản không được để trống")
    @PositiveOrZero(message = "Phí cơ bản phải >= 0")
    private Integer baseFee;

    @PositiveOrZero(message = "Phí giờ cao điểm phải >= 0")
    private Integer peakFee;

    @PositiveOrZero(message = "Ngưỡng miễn phí ship phải >= 0")
    private Integer freeShipThreshold;

    private String estimatedTime;

    @PositiveOrZero(message = "Hệ số ngày lễ phải >= 0")
    private java.math.BigDecimal holidayMultiplier;

    private ShippingZone zone;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
