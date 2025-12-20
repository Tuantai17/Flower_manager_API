package com.flower.manager.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
     * Thông báo cho người dùng
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

    // ============ Static factory methods ============

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
     * Tạo response thành công với data và message
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

    /**
     * Tạo response lỗi
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .build();
    }

    /**
     * Tạo response lỗi 400 Bad Request
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * Tạo response lỗi 404 Not Found
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }

    /**
     * Tạo response lỗi 500 Internal Server Error
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
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
