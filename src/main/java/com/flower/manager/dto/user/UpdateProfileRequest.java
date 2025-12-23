package com.flower.manager.dto.user;

import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO cho yêu cầu cập nhật thông tin profile
 * Chỉ cho phép cập nhật: fullName, phoneNumber, address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {

    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Size(min = 10, max = 15, message = "Số điện thoại phải từ 10-15 ký tự")
    private String phoneNumber;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;
}
