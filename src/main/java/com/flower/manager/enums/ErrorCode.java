package com.flower.manager.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Enum chứa tất cả mã lỗi của hệ thống
 * Frontend có thể dựa vào errorCode để hiển thị UI phù hợp
 * 
 * Quy ước đặt tên:
 * - AUTH_xxx: Lỗi xác thực
 * - USER_xxx: Lỗi liên quan user
 * - PRODUCT_xxx: Lỗi liên quan sản phẩm
 * - ORDER_xxx: Lỗi liên quan đơn hàng
 * - CART_xxx: Lỗi liên quan giỏ hàng
 * - PAYMENT_xxx: Lỗi thanh toán
 * - VOUCHER_xxx: Lỗi mã giảm giá
 * - CATEGORY_xxx: Lỗi danh mục
 * - STOCK_xxx: Lỗi tồn kho
 * - FILE_xxx: Lỗi upload file
 * - VALIDATION_xxx: Lỗi validate dữ liệu
 * - SYSTEM_xxx: Lỗi hệ thống
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =============== AUTHENTICATION ===============
    AUTH_INVALID_CREDENTIALS("AUTH_001", "Tên đăng nhập hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_EXPIRED("AUTH_002", "Token đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    AUTH_TOKEN_INVALID("AUTH_003", "Token không hợp lệ", HttpStatus.UNAUTHORIZED),
    AUTH_ACCESS_DENIED("AUTH_004", "Bạn không có quyền truy cập tài nguyên này", HttpStatus.FORBIDDEN),
    AUTH_LOGIN_REQUIRED("AUTH_005", "Vui lòng đăng nhập để thực hiện chức năng này", HttpStatus.UNAUTHORIZED),

    // =============== USER ===============
    USER_NOT_FOUND("USER_001", "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXISTS("USER_002", "Email đã được sử dụng", HttpStatus.CONFLICT),
    USER_USERNAME_EXISTS("USER_003", "Tên đăng nhập đã tồn tại", HttpStatus.CONFLICT),
    USER_PHONE_EXISTS("USER_004", "Số điện thoại đã được sử dụng", HttpStatus.CONFLICT),
    USER_INACTIVE("USER_005", "Tài khoản đã bị vô hiệu hóa", HttpStatus.FORBIDDEN),

    // =============== PRODUCT ===============
    PRODUCT_NOT_FOUND("PRODUCT_001", "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    PRODUCT_SLUG_EXISTS("PRODUCT_002", "Slug sản phẩm đã tồn tại", HttpStatus.CONFLICT),
    PRODUCT_UNAVAILABLE("PRODUCT_003", "Sản phẩm không còn bán", HttpStatus.UNPROCESSABLE_ENTITY),

    // =============== CATEGORY ===============
    CATEGORY_NOT_FOUND("CATEGORY_001", "Không tìm thấy danh mục", HttpStatus.NOT_FOUND),
    CATEGORY_SLUG_EXISTS("CATEGORY_002", "Slug danh mục đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_HAS_CHILDREN("CATEGORY_003", "Không thể xóa danh mục còn chứa danh mục con",
            HttpStatus.UNPROCESSABLE_ENTITY),
    CATEGORY_HAS_PRODUCTS("CATEGORY_004", "Không thể xóa danh mục còn chứa sản phẩm", HttpStatus.UNPROCESSABLE_ENTITY),

    // =============== CART ===============
    CART_NOT_FOUND("CART_001", "Giỏ hàng không tồn tại", HttpStatus.NOT_FOUND),
    CART_EMPTY("CART_002", "Giỏ hàng trống", HttpStatus.UNPROCESSABLE_ENTITY),
    CART_ITEM_NOT_FOUND("CART_003", "Sản phẩm không có trong giỏ hàng", HttpStatus.NOT_FOUND),

    // =============== ORDER ===============
    ORDER_NOT_FOUND("ORDER_001", "Không tìm thấy đơn hàng", HttpStatus.NOT_FOUND),
    ORDER_CANNOT_CANCEL("ORDER_002", "Không thể hủy đơn hàng ở trạng thái này", HttpStatus.UNPROCESSABLE_ENTITY),
    ORDER_ALREADY_PAID("ORDER_003", "Đơn hàng đã được thanh toán", HttpStatus.UNPROCESSABLE_ENTITY),
    ORDER_INVALID_STATUS_TRANSITION("ORDER_004", "Không thể chuyển đổi trạng thái đơn hàng",
            HttpStatus.UNPROCESSABLE_ENTITY),
    ORDER_ACCESS_DENIED("ORDER_005", "Bạn không có quyền xem đơn hàng này", HttpStatus.FORBIDDEN),

    // =============== STOCK ===============
    STOCK_INSUFFICIENT("STOCK_001", "Số lượng tồn kho không đủ", HttpStatus.UNPROCESSABLE_ENTITY),
    STOCK_INVALID_QUANTITY("STOCK_002", "Số lượng không hợp lệ", HttpStatus.BAD_REQUEST),

    // =============== VOUCHER ===============
    VOUCHER_NOT_FOUND("VOUCHER_001", "Mã giảm giá không tồn tại", HttpStatus.NOT_FOUND),
    VOUCHER_EXPIRED("VOUCHER_002", "Mã giảm giá đã hết hạn", HttpStatus.UNPROCESSABLE_ENTITY),
    VOUCHER_USAGE_LIMIT("VOUCHER_003", "Mã giảm giá đã hết lượt sử dụng", HttpStatus.UNPROCESSABLE_ENTITY),
    VOUCHER_MIN_ORDER("VOUCHER_004", "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã",
            HttpStatus.UNPROCESSABLE_ENTITY),
    VOUCHER_INVALID("VOUCHER_005", "Mã giảm giá không hợp lệ", HttpStatus.UNPROCESSABLE_ENTITY),

    // =============== PAYMENT ===============
    PAYMENT_MOMO_ERROR("PAYMENT_001", "Lỗi thanh toán MoMo", HttpStatus.UNPROCESSABLE_ENTITY),
    PAYMENT_SIGNATURE_INVALID("PAYMENT_002", "Chữ ký thanh toán không hợp lệ", HttpStatus.BAD_REQUEST),
    PAYMENT_FAILED("PAYMENT_003", "Thanh toán thất bại", HttpStatus.UNPROCESSABLE_ENTITY),
    PAYMENT_TIMEOUT("PAYMENT_004", "Thanh toán đã hết hạn", HttpStatus.UNPROCESSABLE_ENTITY),

    // =============== FILE UPLOAD ===============
    FILE_UPLOAD_FAILED("FILE_001", "Không thể tải file lên", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_TYPE_NOT_ALLOWED("FILE_002", "Định dạng file không được hỗ trợ", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("FILE_003", "Kích thước file vượt quá giới hạn", HttpStatus.BAD_REQUEST),
    FILE_NOT_FOUND("FILE_004", "Không tìm thấy file", HttpStatus.NOT_FOUND),

    // =============== REVIEW ===============
    REVIEW_NOT_FOUND("REVIEW_001", "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),
    REVIEW_ALREADY_EXISTS("REVIEW_002", "Bạn đã đánh giá sản phẩm này", HttpStatus.CONFLICT),

    // =============== VALIDATION ===============
    VALIDATION_ERROR("VALIDATION_001", "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    VALIDATION_MISSING_FIELD("VALIDATION_002", "Thiếu trường bắt buộc", HttpStatus.BAD_REQUEST),
    VALIDATION_INVALID_FORMAT("VALIDATION_003", "Định dạng dữ liệu không đúng", HttpStatus.BAD_REQUEST),

    // =============== SYSTEM ===============
    SYSTEM_ERROR("SYSTEM_001", "Đã xảy ra lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),
    SYSTEM_MAINTENANCE("SYSTEM_002", "Hệ thống đang bảo trì", HttpStatus.SERVICE_UNAVAILABLE),

    // =============== RESOURCE ===============
    RESOURCE_NOT_FOUND("RESOURCE_001", "Không tìm thấy tài nguyên", HttpStatus.NOT_FOUND),
    RESOURCE_ALREADY_EXISTS("RESOURCE_002", "Tài nguyên đã tồn tại", HttpStatus.CONFLICT),

    // =============== BUSINESS GENERIC ===============
    BUSINESS_ERROR("BUSINESS_001", "Thao tác không hợp lệ", HttpStatus.UNPROCESSABLE_ENTITY),

    // =============== EMAIL VERIFICATION ===============
    INVALID_TOKEN("TOKEN_001", "Token không hợp lệ hoặc không tồn tại", HttpStatus.BAD_REQUEST),
    TOKEN_EXPIRED("TOKEN_002", "Token đã hết hạn", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAILED("EMAIL_001", "Không thể gửi email", HttpStatus.INTERNAL_SERVER_ERROR),
    EMAIL_ALREADY_VERIFIED("EMAIL_002", "Email đã được xác thực", HttpStatus.CONFLICT);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    /**
     * Tìm ErrorCode từ mã lỗi string (dùng cho BusinessException)
     */
    public static ErrorCode fromCode(String errorCode) {
        if (errorCode == null) {
            return BUSINESS_ERROR;
        }

        for (ErrorCode ec : values()) {
            if (ec.code.equals(errorCode)) {
                return ec;
            }
        }

        // Fallback: tìm theo tên enum
        try {
            return valueOf(errorCode);
        } catch (IllegalArgumentException e) {
            return BUSINESS_ERROR;
        }
    }
}
