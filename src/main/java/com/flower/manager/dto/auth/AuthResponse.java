package com.flower.manager.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO cho các API xác thực (login, register)
 * Trả về token JWT và thông tin user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private boolean success;

    private String message;

    private String token;

    private String tokenType;

    private UserDTO user;

    /**
     * Tạo response thành công với token và user info
     */
    public static AuthResponse success(String token, UserDTO user) {
        return AuthResponse.builder()
                .success(true)
                .message("Thành công")
                .token(token)
                .tokenType("Bearer")
                .user(user)
                .build();
    }

    /**
     * Tạo response thành công với message tùy chỉnh
     */
    public static AuthResponse success(String message, String token, UserDTO user) {
        return AuthResponse.builder()
                .success(true)
                .message(message)
                .token(token)
                .tokenType("Bearer")
                .user(user)
                .build();
    }

    /**
     * Tạo response lỗi
     */
    public static AuthResponse error(String message) {
        return AuthResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
