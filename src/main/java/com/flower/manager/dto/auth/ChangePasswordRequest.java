package com.flower.manager.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho yêu cầu đổi mật khẩu (user đã đăng nhập)
 * 
 * Lưu ý: currentPassword là optional cho user đăng ký qua Google
 * Validation chi tiết được thực hiện trong AuthServiceImpl
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    /**
     * Mật khẩu hiện tại
     * - BẮT BUỘC với user đăng ký thường (LOCAL)
     * - KHÔNG cần với user đăng ký qua Google (GOOGLE)
     */
    private String currentPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6, max = 50, message = "Mật khẩu phải từ 6-50 ký tự")
    private String newPassword;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;
}
