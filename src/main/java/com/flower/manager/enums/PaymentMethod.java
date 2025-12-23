package com.flower.manager.enums;

/**
 * Phương thức thanh toán
 */
public enum PaymentMethod {
    COD("Thanh toán khi nhận hàng"),
    MOMO("Ví MoMo"),
    VNPAY("VNPay"),
    BANK_TRANSFER("Chuyển khoản ngân hàng");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
