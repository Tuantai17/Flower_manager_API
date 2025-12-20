package com.flower.manager.enums;

/**
 * Enum trạng thái sản phẩm
 * Sử dụng thay vì magic number để code rõ ràng hơn
 */
public enum ProductStatus {

    /**
     * Sản phẩm ngừng bán
     */
    INACTIVE(0, "Ngừng bán"),

    /**
     * Sản phẩm đang bán
     */
    ACTIVE(1, "Đang bán"),

    /**
     * Sản phẩm hết hàng
     */
    OUT_OF_STOCK(2, "Hết hàng");

    private final int code;
    private final String description;

    ProductStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Chuyển từ code sang enum
     */
    public static ProductStatus fromCode(int code) {
        for (ProductStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Không tìm thấy ProductStatus với code: " + code);
    }

    /**
     * Kiểm tra sản phẩm có thể bán được không
     */
    public boolean isSellable() {
        return this == ACTIVE;
    }
}
