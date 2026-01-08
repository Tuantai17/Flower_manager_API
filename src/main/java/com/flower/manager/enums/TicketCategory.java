package com.flower.manager.enums;

/**
 * Phân loại ticket liên hệ
 */
public enum TicketCategory {
    ORDER("Đặt hàng"),
    SUPPORT("Hỗ trợ"),
    FEEDBACK("Góp ý"),
    PARTNERSHIP("Hợp tác"),
    OTHER("Khác");

    private final String displayName;

    TicketCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
