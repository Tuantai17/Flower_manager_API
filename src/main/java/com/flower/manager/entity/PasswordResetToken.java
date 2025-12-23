package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ token đặt lại mật khẩu
 * Token có thời hạn (expiry) và chỉ được sử dụng 1 lần
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    /**
     * Thời gian hết hạn mặc định: 30 phút
     */
    public static final int EXPIRATION_MINUTES = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token ngẫu nhiên (UUID)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String token;

    /**
     * User sở hữu token này
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Thời điểm tạo token
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Thời điểm hết hạn token
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * Đánh dấu token đã được sử dụng chưa
     * Token chỉ được dùng 1 lần
     */
    @Column(name = "used", nullable = false)
    @Builder.Default
    private Boolean used = false;

    /**
     * Constructor tạo token mới với thời hạn mặc định
     */
    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.createdAt = LocalDateTime.now();
        this.expiryDate = createdAt.plusMinutes(EXPIRATION_MINUTES);
        this.used = false;
    }

    /**
     * Kiểm tra token đã hết hạn chưa
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiryDate);
    }

    /**
     * Kiểm tra token có hợp lệ không (chưa dùng + chưa hết hạn)
     */
    public boolean isValid() {
        return !this.used && !isExpired();
    }

    /**
     * Đánh dấu token đã được sử dụng
     */
    public void markAsUsed() {
        this.used = true;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiryDate == null) {
            expiryDate = createdAt.plusMinutes(EXPIRATION_MINUTES);
        }
    }
}
