package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entity lưu thông tin người đăng ký nhận tin khuyến mãi
 */
@Entity
@Table(name = "newsletter_subscribers", indexes = {
        @Index(name = "idx_newsletter_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewsletterSubscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    /**
     * Mã voucher được tạo cho subscriber này
     */
    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    /**
     * Trạng thái subscription (true = đang active)
     */
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Ngày đăng ký
     */
    @Column(name = "subscribed_at", updatable = false)
    private LocalDateTime subscribedAt;

    /**
     * Ngày hủy đăng ký (nếu có)
     */
    @Column(name = "unsubscribed_at")
    private LocalDateTime unsubscribedAt;

    @PrePersist
    protected void onCreate() {
        subscribedAt = LocalDateTime.now();
    }
}
