package com.flower.manager.dto.review;

import com.flower.manager.entity.Review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Review - dùng trong response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;

    // Product info
    private Long productId;
    private String productName;
    private String productSlug;
    private String productThumbnail;

    // User info
    private Long userId;
    private String username;
    private String userFullName;

    // Order info
    private Long orderId;
    private String orderCode;

    // Review content
    private Integer rating;
    private String comment;
    private List<String> images;

    // Status
    private ReviewStatus status;
    private String statusDisplayName;

    // Admin reply
    private String adminReply;
    private LocalDateTime repliedAt;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
