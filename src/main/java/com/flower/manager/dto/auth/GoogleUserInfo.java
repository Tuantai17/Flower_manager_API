package com.flower.manager.dto.auth;

import lombok.*;

/**
 * DTO chứa thông tin người dùng từ Google
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoogleUserInfo {

    /**
     * Google User ID
     */
    private String googleId;

    /**
     * Email từ Google
     */
    private String email;

    /**
     * Tên đầy đủ
     */
    private String fullName;

    /**
     * URL ảnh đại diện
     */
    private String pictureUrl;

    /**
     * Email đã được xác minh chưa
     */
    private Boolean emailVerified;
}
