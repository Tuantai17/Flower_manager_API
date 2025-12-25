package com.flower.manager.controller.review;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.review.*;
import com.flower.manager.service.review.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho User quản lý reviews
 * Endpoint: /api/reviews/**
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // ================= PUBLIC ENDPOINTS =================

    /**
     * Lấy danh sách reviews của sản phẩm (Public)
     * GET /api/reviews/product/{productId}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get reviews for product: {}", productId);
        Page<ReviewDTO> reviews = reviewService.getProductReviews(productId, page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá thành công"));
    }

    /**
     * Lấy thống kê reviews của sản phẩm (Public)
     * GET /api/reviews/product/{productId}/stats
     */
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<ApiResponse<ProductReviewStats>> getProductReviewStats(
            @PathVariable Long productId) {
        log.info("Get review stats for product: {}", productId);
        ProductReviewStats stats = reviewService.getProductReviewStats(productId);
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê đánh giá thành công"));
    }

    // ================= USER ENDPOINTS (Authenticated) =================

    /**
     * Tạo review mới
     * POST /api/reviews
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewDTO>> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        log.info("Create review for product: {} in order: {}", request.getProductId(), request.getOrderId());
        ReviewDTO review = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(review, "Đánh giá đã được gửi và đang chờ duyệt"));
    }

    /**
     * Cập nhật review của mình
     * PUT /api/reviews/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewDTO>> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReviewRequest request) {
        log.info("Update review: {}", id);
        ReviewDTO review = reviewService.updateReview(id, request);
        return ResponseEntity.ok(ApiResponse.success(review, "Cập nhật đánh giá thành công"));
    }

    /**
     * Xóa review của mình
     * DELETE /api/reviews/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        log.info("Delete review: {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa đánh giá thành công"));
    }

    /**
     * Lấy danh sách reviews của tôi
     * GET /api/reviews/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<ReviewDTO>>> getMyReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Get my reviews");
        Page<ReviewDTO> reviews = reviewService.getMyReviews(page, size);
        return ResponseEntity.ok(ApiResponse.success(reviews, "Lấy danh sách đánh giá của bạn thành công"));
    }

    /**
     * Kiểm tra có thể review sản phẩm không
     * GET /api/reviews/can-review?productId=...&orderId=...
     */
    @GetMapping("/can-review")
    public ResponseEntity<ApiResponse<Boolean>> canReviewProduct(
            @RequestParam Long productId,
            @RequestParam Long orderId) {
        log.info("Check can review product: {} in order: {}", productId, orderId);
        boolean canReview = reviewService.canReviewProduct(productId, orderId);
        return ResponseEntity.ok(ApiResponse.success(canReview));
    }
}
