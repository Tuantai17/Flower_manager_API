package com.flower.manager.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho đăng nhập
 * Hỗ trợ đăng nhập bằng: username / email / số điện thoại
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /**
     * Có thể là username, email hoặc số điện thoại
     */
    @NotBlank(message = "Tên đăng nhập/Email/Số điện thoại không được để trống")
    @Size(max = 100, message = "Tên đăng nhập/Email/Số điện thoại không hợp lệ")
    private String identifier;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6-100 ký tự")
    private String password;
}
