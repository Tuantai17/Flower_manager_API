package com.flower.manager.dto.review;

import com.flower.manager.entity.Review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho filter reviews
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewFilterRequest {

    private ReviewStatus status;
    private Long productId;
    private String keyword;

    private int page = 0;
    private int size = 10;
    private String sortBy = "createdAt";
    private String sortDir = "desc";
}
