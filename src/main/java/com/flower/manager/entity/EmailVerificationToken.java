package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity chứa token xác thực email khi đăng ký
 */
@Entity
@Table(name = "email_verification_tokens", indexes = {
        @Index(name = "idx_verification_token", columnList = "token", unique = true),
        @Index(name = "idx_verification_user", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", unique = true, nullable = false, length = 100)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Kiểm tra token còn hiệu lực không
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Kiểm tra token đã được sử dụng chưa
     */
    public boolean isConfirmed() {
        return confirmedAt != null;
    }

    /**
     * Kiểm tra token có hợp lệ không (chưa hết hạn và chưa được sử dụng)
     */
    public boolean isValid() {
        return !isExpired() && !isConfirmed();
    }
}
