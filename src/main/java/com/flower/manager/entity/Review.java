package com.flower.manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity Review - Đánh giá sản phẩm
 * User chỉ có thể đánh giá sản phẩm đã mua
 */
@Entity
@Table(name = "reviews", indexes = {
        @Index(name = "idx_review_product", columnList = "product_id"),
        @Index(name = "idx_review_user", columnList = "user_id"),
        @Index(name = "idx_review_order", columnList = "order_id"),
        @Index(name = "idx_review_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sản phẩm được đánh giá
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    /**
     * Người đánh giá
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    /**
     * Đơn hàng liên quan (để xác minh đã mua)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /**
     * Số sao đánh giá (1-5)
     */
    @Column(nullable = false)
    private Integer rating;

    /**
     * Nội dung bình luận
     */
    @Column(columnDefinition = "TEXT")
    private String comment;

    /**
     * Ảnh đính kèm (JSON array của URLs)
     */
    @Column(name = "images", length = 2000)
    private String images;

    /**
     * Trạng thái: PENDING, APPROVED, REJECTED
     * Mặc định APPROVED để review hiển thị ngay
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ReviewStatus status = ReviewStatus.APPROVED;

    /**
     * Phản hồi từ Admin/Shop
     */
    @Column(name = "admin_reply", columnDefinition = "TEXT")
    private String adminReply;

    /**
     * Ảnh đính kèm trong phản hồi Admin (JSON array của URLs)
     */
    @Column(name = "admin_reply_images", length = 2000)
    private String adminReplyImages;

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * Enum trạng thái review
     */
    public enum ReviewStatus {
        PENDING("Chờ duyệt"),
        APPROVED("Đã duyệt"),
        REJECTED("Bị từ chối");

        private final String displayName;

        ReviewStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
