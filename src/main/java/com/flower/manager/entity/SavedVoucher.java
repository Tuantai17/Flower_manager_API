package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity đại diện cho Voucher đã lưu của User
 * Quan hệ: User N-N Voucher thông qua bảng này
 */
@Entity
@Table(name = "saved_vouchers", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id",
        "voucher_id" }), indexes = {
                @Index(name = "idx_saved_voucher_user", columnList = "user_id"),
                @Index(name = "idx_saved_voucher_voucher", columnList = "voucher_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    /**
     * Trạng thái: true = chưa sử dụng, false = đã sử dụng
     */
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Ngày sử dụng voucher (nếu đã dùng)
     */
    @Column(name = "used_at")
    private LocalDateTime usedAt;

    /**
     * Ngày lưu voucher
     */
    @Column(name = "saved_at", updatable = false)
    private LocalDateTime savedAt;

    @PrePersist
    protected void onCreate() {
        savedAt = LocalDateTime.now();
    }

    /**
     * Đánh dấu voucher đã sử dụng
     */
    public void markAsUsed() {
        this.isAvailable = false;
        this.usedAt = LocalDateTime.now();
    }
}
