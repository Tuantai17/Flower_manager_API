package com.flower.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.flower.manager.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response wrapper chuẩn cho tất cả API
 * Giúp response đồng nhất và dễ xử lý ở frontend
 * 
 * @param <T> Kiểu dữ liệu của data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Trạng thái thành công hay thất bại
     */
    private boolean success;

    /**
     * HTTP status code
     */
    private int status;

    /**
     * Mã lỗi (dành cho Frontend xử lý logic)
     * Ví dụ: "AUTH_001", "ORDER_002", "STOCK_001"
     */
    private String errorCode;

    /**
     * Thông báo cho người dùng (human readable)
     */
    private String message;

    /**
     * Dữ liệu trả về (có thể là object, list, ...)
     */
    private T data;

    /**
     * Thời gian response
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Thông tin phân trang (nếu có)
     */
    private PageInfo pagination;

    // ============ Static factory methods - SUCCESS ============

    /**
     * Tạo response thành công với data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Thành công")
                .data(data)
                .build();
    }

    /**
     * Tao response thanh cong voi data va message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo response thành công khi tạo mới (201 Created)
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message("Tạo thành công")
                .data(data)
                .build();
    }

    /**
     * Tạo response thành công khi tạo mới với message
     */
    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message(message)
                .data(data)
                .build();
    }

    // ============ Static factory methods - ERROR (with errorCode) ============

    /**
     * Tạo response lỗi với ErrorCode enum
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    /**
     * Tạo response lỗi với ErrorCode enum và message tùy chỉnh
     */
    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getCode())
                .message(customMessage)
                .build();
    }

    /**
     * Tạo response lỗi với mã lỗi string và thông tin chi tiết
     */
    public static <T> ApiResponse<T> error(String errorCode, int status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    /**
     * Tạo response lỗi (backward compatible - không có errorCode)
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }

    // ============ Convenience methods ============

    /**
     * Tạo response lỗi 400 Bad Request
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(ErrorCode.VALIDATION_ERROR, message);
    }

    /**
     * Tạo response lỗi 401 Unauthorized
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return error(ErrorCode.AUTH_LOGIN_REQUIRED, message);
    }

    /**
     * Tạo response lỗi 403 Forbidden
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return error(ErrorCode.AUTH_ACCESS_DENIED, message);
    }

    /**
     * Tạo response lỗi 404 Not Found
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(ErrorCode.RESOURCE_NOT_FOUND, message);
    }

    /**
     * Tạo response lỗi 500 Internal Server Error
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(ErrorCode.SYSTEM_ERROR, message);
    }

    // ============ Inner class for pagination ============

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
