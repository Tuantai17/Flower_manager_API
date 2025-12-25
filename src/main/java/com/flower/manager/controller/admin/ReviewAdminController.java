package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.review.*;
import com.flower.manager.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller cho Admin quản lý reviews
 * Endpoint: /api/admin/reviews/**
 */
@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewAdminController {

    private final ReviewService reviewService;

    /**
     * Lấy danh sách tất cả reviews với filter
     * GET /api/admin/reviews
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getAllReviews(
            @ModelAttribute ReviewFilterRequest filter) {
        log.info("Admin get all reviews with filter: status={}, productId={}",
                filter.getStatus(), filter.getProductId());
        Page<ReviewDTO> reviews = reviewService.getAllReviews(filter);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }

    /**
     * Duyệt/Từ chối review
     * PUT /api/admin/reviews/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReviewStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReviewActionRequest request) {
        log.info("Admin update review {} status to {}", id, request.getStatus());
        ReviewDTO review = reviewService.updateReviewStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Cập nhật trạng thái đánh giá thành công"));
    }

    /**
     * Phản hồi review
     * POST /api/admin/reviews/{id}/reply
     */
    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse<ReviewDTO>> replyToReview(
            @PathVariable Long id,
            @Valid @RequestBody AdminReplyRequest request) {
        log.info("Admin reply to review {}", id);
        ReviewDTO review = reviewService.replyToReview(id, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Phản hồi đánh giá thành công"));
    }

    /**
     * Xóa review (Admin)
     * DELETE /api/admin/reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        log.info("Admin delete review {}", id);
        reviewService.adminDeleteReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }

    /**
     * Đếm số reviews chờ duyệt (cho Dashboard)
     * GET /api/admin/reviews/pending-count
     */
    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPendingCount() {
        long count = reviewService.countPendingReviews();
        return ResponseEntity.ok(ApiResponse.success(
                Map.of("pendingReviews", count),
                "Lấy số đánh giá chờ duyệt thành công"));
    }
}
