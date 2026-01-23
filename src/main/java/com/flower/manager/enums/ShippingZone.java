package com.flower.manager.enums;

/**
 * Enum định nghĩa khu vực giao hàng
 */
public enum ShippingZone {
    INNER("Nội thành"),
    OUTER("Ngoại thành");

    private final String displayName;

    ShippingZone(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
