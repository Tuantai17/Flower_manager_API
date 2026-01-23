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
     * Số lượng voucher còn lại có thể sử dụng
     * Mặc định = 1 (mỗi voucher dùng 1 lần)
     */
    @Column(name = "quantity")
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Số lần đã sử dụng voucher này
     */
    @Column(name = "used_count")
    @Builder.Default
    private Integer usedCount = 0;

    /**
     * Trạng thái: true = còn sử dụng được, false = đã hết lượt
     */
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;

    /**
     * Ngày sử dụng voucher lần cuối (nếu đã dùng)
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
        if (quantity == null)
            quantity = 1;
        if (usedCount == null)
            usedCount = 0;
    }

    /**
     * Sử dụng 1 lượt voucher
     * 
     * @return true nếu còn lượt để dùng, false nếu hết
     */
    public boolean useOne() {
        if (quantity > usedCount) {
            this.usedCount++;
            this.usedAt = LocalDateTime.now();

            // Nếu đã dùng hết lượt, đánh dấu không khả dụng
            if (usedCount >= quantity) {
                this.isAvailable = false;
            }
            return true;
        }
        return false;
    }

    /**
     * Đánh dấu voucher đã sử dụng hết (backward compatibility)
     */
    public void markAsUsed() {
        this.isAvailable = false;
        this.usedCount = this.quantity;
        this.usedAt = LocalDateTime.now();
    }

    /**
     * Số lượt còn lại có thể sử dụng
     */
    public int getRemainingUses() {
        return Math.max(0, quantity - usedCount);
    }
}
