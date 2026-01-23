package com.flower.manager.dto.shipping;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Response DTO cho API preview checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPreviewResponse {

    /**
     * Tổng tiền hàng gốc
     */
    private Integer subtotal;

    /**
     * Phí vận chuyển gốc (trước khi áp voucher ship)
     */
    private Integer shippingOriginal;

    /**
     * Số tiền giảm từ voucher ORDER
     */
    @Builder.Default
    private Integer orderDiscount = 0;

    /**
     * Tổng tiền hàng sau khi giảm
     */
    private Integer subtotalAfterDiscount;

    /**
     * Số tiền giảm từ voucher SHIPPING
     */
    @Builder.Default
    private Integer shippingDiscount = 0;

    /**
     * Phí vận chuyển cuối cùng (sau khi áp voucher)
     */
    private Integer shippingFinal;

    /**
     * Tổng thanh toán cuối cùng
     */
    private Integer grandTotal;

    /**
     * Danh sách voucher đã áp dụng
     */
    @Builder.Default
    private List<AppliedVoucher> appliedVouchers = new ArrayList<>();

    /**
     * Các cảnh báo (VD: ship đã free nên voucher ship không có tác dụng)
     */
    @Builder.Default
    private List<String> warnings = new ArrayList<>();

    /**
     * Thông tin voucher đã áp dụng
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedVoucher {
        private String code;
        private String type; // ORDER / SHIPPING
        private Integer discount;
        private String description;
    }
}
