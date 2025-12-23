package com.flower.manager.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO cho request đăng nhập bằng Google
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleLoginRequest {

    /**
     * Google ID Token từ Frontend
     */
    @NotBlank(message = "Token không được để trống")
    private String idToken;
}
