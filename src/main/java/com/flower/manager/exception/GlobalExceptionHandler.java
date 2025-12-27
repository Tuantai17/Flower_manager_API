package com.flower.manager.exception;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.enums.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global Exception Handler cho toàn bộ ứng dụng
 * Xử lý và format response error với errorCode rõ ràng cho Frontend
 * 
 * Response format:
 * {
 * "success": false,
 * "status": 422,
 * "errorCode": "STOCK_001",
 * "message": "Số lượng tồn kho không đủ",
 * "timestamp": "2024-12-27T14:30:00"
 * }
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // =============== AUTHENTICATION & AUTHORIZATION ===============

    /**
     * Xử lý BadCredentialsException (401 Unauthorized)
     * Khi đăng nhập sai username hoặc password
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("[AUTH] Bad credentials attempt: {}", ex.getMessage());
        return buildResponse(ErrorCode.AUTH_INVALID_CREDENTIALS);
    }

    /**
     * Xử lý AuthenticationException (401 Unauthorized)
     * Các lỗi xác thực chung
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("[AUTH] Authentication failed: {}", ex.getMessage());
        return buildResponse(ErrorCode.AUTH_LOGIN_REQUIRED, ex.getMessage());
    }

    /**
     * Xử lý AccessDeniedException (403 Forbidden)
     * Khi user không có quyền truy cập
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("[AUTH] Access denied: {}", ex.getMessage());
        return buildResponse(ErrorCode.AUTH_ACCESS_DENIED);
    }

    // =============== BUSINESS LOGIC ERRORS ===============

    /**
     * Xử lý BusinessException (với errorCode mapping)
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex) {
        log.warn("[BUSINESS] Error [{}]: {}", ex.getErrorCode(), ex.getMessage());

        ApiResponse<Object> response = ApiResponse.error(
                ex.getErrorCode(),
                ex.getHttpStatus(),
                ex.getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    // =============== RESOURCE ERRORS ===============

    /**
     * Xử lý ResourceNotFoundException (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("[RESOURCE] Not found: {}", ex.getMessage());
        return buildResponse(ErrorCode.RESOURCE_NOT_FOUND, ex.getMessage());
    }

    /**
     * Xử lý DuplicateResourceException (409 Conflict)
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("[RESOURCE] Duplicate: {}", ex.getMessage());
        return buildResponse(ErrorCode.RESOURCE_ALREADY_EXISTS, ex.getMessage());
    }

    /**
     * Xử lý ResourceAlreadyExistsException (409 Conflict)
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceAlreadyExists(ResourceAlreadyExistsException ex) {
        log.warn("[RESOURCE] Already exists: {}", ex.getMessage());
        return buildResponse(ErrorCode.RESOURCE_ALREADY_EXISTS, ex.getMessage());
    }

    // =============== VALIDATION ERRORS ===============

    /**
     * Xử lý Validation errors (@Valid) (400)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String errorSummary = errors.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue())
                .collect(Collectors.joining("; "));

        log.warn("[VALIDATION] Errors: {}", errorSummary);

        ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
                .success(false)
                .status(HttpStatus.BAD_REQUEST.value())
                .errorCode(ErrorCode.VALIDATION_ERROR.getCode())
                .message("Dữ liệu không hợp lệ: " + errorSummary)
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý IllegalArgumentException (400)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("[VALIDATION] Invalid argument: {}", ex.getMessage());
        return buildResponse(ErrorCode.VALIDATION_ERROR, ex.getMessage());
    }

    /**
     * Xử lý thiếu request parameter (400)
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = String.format("Thiếu tham số bắt buộc: %s", ex.getParameterName());
        log.warn("[VALIDATION] Missing parameter: {}", ex.getParameterName());
        return buildResponse(ErrorCode.VALIDATION_MISSING_FIELD, message);
    }

    /**
     * Xử lý sai kiểu dữ liệu tham số (400)
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Tham số '%s' có giá trị không hợp lệ: %s",
                ex.getName(), ex.getValue());
        log.warn("[VALIDATION] Type mismatch: {} = {}", ex.getName(), ex.getValue());
        return buildResponse(ErrorCode.VALIDATION_INVALID_FORMAT, message);
    }

    // =============== FILE UPLOAD ERRORS ===============

    /**
     * Xử lý lỗi kích thước file quá lớn
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        log.warn("[FILE] Upload size exceeded: {}", ex.getMessage());
        return buildResponse(ErrorCode.FILE_SIZE_EXCEEDED);
    }

    // =============== STATIC RESOURCE ERRORS ===============

    /**
     * Xử lý NoResourceFoundException (404)
     * Xử lý các request tới static resources không tồn tại như favicon.ico
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(NoResourceFoundException ex) {
        String resourcePath = ex.getResourcePath();

        // Chỉ log debug cho favicon.ico và các static resources
        if (resourcePath != null && (resourcePath.equals("favicon.ico") || resourcePath.isEmpty())) {
            log.debug("[STATIC] Resource not found: {}", resourcePath);
        } else {
            log.warn("[STATIC] Resource not found: {}", resourcePath);
        }

        return buildResponse(ErrorCode.RESOURCE_NOT_FOUND, "Resource not found: " + resourcePath);
    }

    // =============== SYSTEM ERRORS ===============

    /**
     * Xử lý RuntimeException chung (500)
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex) {
        log.error("[SYSTEM] Runtime exception: ", ex);
        return buildResponse(ErrorCode.SYSTEM_ERROR, ex.getMessage());
    }

    /**
     * Xử lý tất cả các Exception khác (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
        log.error("[SYSTEM] Unexpected exception: ", ex);
        return buildResponse(ErrorCode.SYSTEM_ERROR, "Đã xảy ra lỗi hệ thống: " + ex.getMessage());
    }

    // =============== HELPER METHODS ===============

    /**
     * Build ResponseEntity từ ErrorCode
     */
    private ResponseEntity<ApiResponse<Object>> buildResponse(ErrorCode errorCode) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Build ResponseEntity từ ErrorCode với custom message
     */
    private ResponseEntity<ApiResponse<Object>> buildResponse(ErrorCode errorCode, String customMessage) {
        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode, customMessage));
    }
}
