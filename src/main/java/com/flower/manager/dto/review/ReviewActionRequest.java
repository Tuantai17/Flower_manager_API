package com.flower.manager.dto.review;

import com.flower.manager.entity.Review.ReviewStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO cho Admin duyệt/từ chối review
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewActionRequest {

    /**
     * Trạng thái mới: APPROVED hoặc REJECTED
     */
    private ReviewStatus status;

    /**
     * Lý do từ chối (nếu REJECTED)
     */
    private String reason;
}
