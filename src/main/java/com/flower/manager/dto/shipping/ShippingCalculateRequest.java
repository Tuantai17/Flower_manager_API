package com.flower.manager.dto.shipping;

import com.flower.manager.enums.DeliveryType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho API tính phí vận chuyển
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCalculateRequest {

    /**
     * Thành phố (mặc định TPHCM)
     */
    @Builder.Default
    private String city = "TPHCM";

    /**
     * Quận/Huyện
     */
    @NotBlank(message = "Vui lòng chọn Quận/Huyện")
    private String district;

    /**
     * Tổng tiền hàng (VND)
     */
    @NotNull(message = "Tổng tiền hàng không được để trống")
    @Min(value = 0, message = "Tổng tiền hàng phải >= 0")
    private Integer subtotal;

    /**
     * Loại giao hàng (STANDARD / RUSH)
     */
    @Builder.Default
    private DeliveryType deliveryType = DeliveryType.STANDARD;

    /**
     * Thời gian yêu cầu giao (ISO format, optional)
     */
    private String requestedTime;
}
