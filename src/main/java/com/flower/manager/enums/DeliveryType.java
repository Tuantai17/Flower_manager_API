package com.flower.manager.enums;

/**
 * Enum định nghĩa loại giao hàng
 */
public enum DeliveryType {
    STANDARD("Giao tiêu chuẩn"),
    RUSH("Giao nhanh");

    private final String displayName;

    DeliveryType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
