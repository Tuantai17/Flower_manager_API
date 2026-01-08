package com.flower.manager.dto.order;

import com.flower.manager.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO cho yêu cầu Checkout (Đặt hàng)
 * Cấu trúc theo UI: Người gửi, Người nhận, Địa chỉ chi tiết, Lịch giao hàng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

    // ============ THÔNG TIN NGƯỜI GỬI ============
    @NotBlank(message = "Tên người gửi không được để trống")
    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    private String senderName;

    @NotBlank(message = "Số điện thoại người gửi không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String senderPhone;

    private String senderEmail;

    // ============ THÔNG TIN NGƯỜI NHẬN ============
    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    private String recipientName;

    @NotBlank(message = "Số điện thoại người nhận không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String recipientPhone;

    // ============ ĐỊA CHỈ GIAO HÀNG (CHUẨN HÓA) ============
    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Size(max = 300, message = "Địa chỉ không được quá 300 ký tự")
    private String addressDetail; // Số nhà, tên đường

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province; // VD: "TP.HCM - Nội thành"

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String district; // VD: "Quận 1"

    // ============ LỊCH GIAO HÀNG ============
    private LocalDate deliveryDate; // Ngày giao hàng

    private String deliveryTime; // Khung giờ giao (VD: "16:00 - 20:00")

    // ============ TỌA ĐỘ ĐỊA LÝ (từ OSM/Photon Autocomplete) ============
    private Double lat; // Latitude từ autocomplete selection

    private Double lng; // Longitude từ autocomplete selection

    private String geoProvider; // Provider: PHOTON, GOOGLE, MAPBOX

    private String placeId; // Place ID (for Google/Mapbox)

    // ============ GHI CHÚ & THANH TOÁN ============
    @Size(max = 500, message = "Ghi chú không được quá 500 ký tự")
    private String note; // Lời nhắn cho người nhận

    private String voucherCode;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    /**
     * Loại thanh toán MoMo (CARD hoặc WALLET)
     */
    private String momoType;

    // ============ HELPER METHOD ============
    /**
     * Tạo địa chỉ giao hàng đầy đủ từ các thành phần
     */
    public String getFullShippingAddress() {
        return String.format("%s, %s, %s", addressDetail, district, province);
    }
}
