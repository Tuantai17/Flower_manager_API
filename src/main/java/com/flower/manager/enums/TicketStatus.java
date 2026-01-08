package com.flower.manager.enums;

/**
 * Trạng thái ticket liên hệ
 */
public enum TicketStatus {
    OPEN("Mới"),
    IN_PROGRESS("Đang xử lý"),
    RESOLVED("Đã giải quyết"),
    CLOSED("Đã đóng");

    private final String displayName;

    TicketStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
