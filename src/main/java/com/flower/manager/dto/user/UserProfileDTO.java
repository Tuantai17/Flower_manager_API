package com.flower.manager.dto.user;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO hiển thị thông tin profile của User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileDTO {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private String avatar;
    private String role;
    private Boolean isActive;
    private String authProvider; // LOCAL hoặc GOOGLE
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
