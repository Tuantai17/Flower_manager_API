package com.flower.manager.dto.order;

import com.flower.manager.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO cho yêu cầu Checkout (Đặt hàng)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên không được quá 100 ký tự")
    private String customerName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Số điện thoại không hợp lệ")
    private String customerPhone;

    private String customerEmail;

    @NotBlank(message = "Địa chỉ giao hàng không được để trống")
    @Size(max = 500, message = "Địa chỉ không được quá 500 ký tự")
    private String shippingAddress;

    @Size(max = 500, message = "Ghi chú không được quá 500 ký tự")
    private String note;

    private String voucherCode;

    @NotNull(message = "Phương thức thanh toán không được để trống")
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.COD;
}
