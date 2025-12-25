package com.flower.manager.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO chứa thống kê review của một sản phẩm
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductReviewStats {

    private Long productId;

    /**
     * Điểm trung bình (1.0 - 5.0)
     */
    private Double averageRating;

    /**
     * Tổng số reviews
     */
    private Long totalReviews;

    /**
     * Số reviews theo từng mức sao
     * Key: rating (1-5), Value: count
     */
    private Map<Integer, Long> ratingDistribution;
}
