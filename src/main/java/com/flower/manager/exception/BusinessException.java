package com.flower.manager.exception;

import com.flower.manager.enums.ErrorCode;
import lombok.Getter;

/**
 * Exception cho các lỗi nghiệp vụ (business logic)
 * Ví dụ: xóa danh mục khi còn sản phẩm, tồn kho không đủ, voucher hết hạn
 * 
 * Sử dụng:
 * - throw new BusinessException(ErrorCode.STOCK_INSUFFICIENT);
 * - throw new BusinessException(ErrorCode.VOUCHER_EXPIRED, "Voucher ABC đã hết
 * hạn");
 * - throw new BusinessException("CUSTOM_CODE", "Custom message");
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final ErrorCode errorCodeEnum;

    /**
     * Constructor với ErrorCode enum (recommended)
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
        this.errorCodeEnum = errorCode;
    }

    /**
     * Constructor với ErrorCode enum và custom message
     */
    public BusinessException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode.getCode();
        this.errorCodeEnum = errorCode;
    }

    /**
     * Constructor với string errorCode (backward compatible)
     */
    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorCodeEnum = ErrorCode.fromCode(errorCode);
    }

    /**
     * Constructor với message only (backward compatible)
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCode.BUSINESS_ERROR.getCode();
        this.errorCodeEnum = ErrorCode.BUSINESS_ERROR;
    }

    /**
     * Lấy HTTP Status từ ErrorCode
     */
    public int getHttpStatus() {
        return errorCodeEnum != null ? errorCodeEnum.getHttpStatus().value() : 422;
    }
}
