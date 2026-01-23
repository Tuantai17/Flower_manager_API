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
 * Request DTO cho API preview checkout (tính tổng tiền với voucher)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPreviewRequest {

    /**
     * Quận/Huyện giao hàng
     */
    @NotBlank(message = "Vui lòng chọn Quận/Huyện")
    private String district;

    /**
     * Loại giao hàng
     */
    @Builder.Default
    private DeliveryType deliveryType = DeliveryType.STANDARD;

    /**
     * Tổng tiền hàng (VND)
     */
    @NotNull(message = "Tổng tiền hàng không được để trống")
    @Min(value = 0, message = "Tổng tiền hàng phải >= 0")
    private Integer subtotal;

    /**
     * Các mã voucher
     */
    private VoucherCodes vouchers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VoucherCodes {
        /**
         * Mã voucher giảm giá đơn hàng
         */
        private String orderVoucherCode;

        /**
         * Mã voucher giảm phí vận chuyển
         */
        private String shippingVoucherCode;
    }
}
