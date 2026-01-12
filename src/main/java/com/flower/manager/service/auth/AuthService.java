package com.flower.manager.service.auth;

import com.flower.manager.dto.auth.UserDTO;
import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.ChangePasswordRequest;
import com.flower.manager.dto.auth.ForgotPasswordRequest;
import com.flower.manager.dto.auth.LoginRequest;
import com.flower.manager.dto.auth.RegisterRequest;
import com.flower.manager.dto.auth.ResetPasswordRequest;

/**
 * Service interface cho các chức năng xác thực
 */
public interface AuthService {

    /**
     * Đăng nhập người dùng
     * 
     * @param loginRequest chứa username và password
     * @return AuthResponse với token và thông tin user
     */
    AuthResponse login(LoginRequest loginRequest);

    /**
     * Đăng ký người dùng mới
     * 
     * @param registerRequest chứa thông tin đăng ký
     * @return AuthResponse với token và thông tin user
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Xử lý yêu cầu quên mật khẩu
     * 
     * @param request chứa email
     * @return AuthResponse với thông báo
     */
    AuthResponse forgotPassword(ForgotPasswordRequest request);

    /**
     * Đặt lại mật khẩu với token
     * 
     * @param request chứa token, email và mật khẩu mới
     * @return AuthResponse với thông báo
     */
    AuthResponse resetPassword(ResetPasswordRequest request);

    /**
     * Đổi mật khẩu cho user đang đăng nhập
     * 
     * @param request chứa mật khẩu hiện tại và mật khẩu mới
     * @return AuthResponse với thông báo
     */
    AuthResponse changePassword(ChangePasswordRequest request);

    /**
     * Lấy thông tin user hiện tại
     * 
     * @return UserDTO
     */
    UserDTO getCurrentUser();
}
