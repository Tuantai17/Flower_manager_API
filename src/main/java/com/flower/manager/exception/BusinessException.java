package com.flower.manager.exception;

/**
 * Exception khi thao tác không hợp lệ
 * Ví dụ: xóa danh mục khi còn sản phẩm, chuyển cha thành con khi còn children
 */
public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(String message) {
        super(message);
        this.errorCode = "BUSINESS_ERROR";
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
