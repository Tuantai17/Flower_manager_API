package com.flower.manager.enums;

/**
 * Enum định nghĩa loại voucher
 */
public enum VoucherType {
    ORDER("Giảm giá đơn hàng"),
    SHIPPING("Giảm phí vận chuyển");

    private final String displayName;

    VoucherType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
