package com.flower.manager.service.review;

import com.flower.manager.dto.review.*;
import com.flower.manager.entity.Review;
import org.springframework.data.domain.Page;

/**
 * Service interface cho Review
 */
public interface ReviewService {

    // ================= USER METHODS =================

    /**
     * Tạo review mới (User đã mua hàng)
     */
    ReviewDTO createReview(CreateReviewRequest request);

    /**
     * Cập nhật review của mình
     */
    ReviewDTO updateReview(Long reviewId, UpdateReviewRequest request);

    /**
     * Xóa review của mình
     */
    void deleteReview(Long reviewId);

    /**
     * Lấy danh sách review của user hiện tại
     */
    Page<ReviewDTO> getMyReviews(int page, int size);

    /**
     * Kiểm tra user có thể review sản phẩm trong đơn hàng không
     */
    boolean canReviewProduct(Long productId, Long orderId);

    // ================= PUBLIC METHODS =================

    /**
     * Lấy reviews của sản phẩm (chỉ APPROVED)
     */
    Page<ReviewDTO> getProductReviews(Long productId, int page, int size);

    /**
     * Lấy thống kê review của sản phẩm
     */
    ProductReviewStats getProductReviewStats(Long productId);

    // ================= ADMIN METHODS =================

    /**
     * Lấy tất cả reviews với filters (Admin)
     */
    Page<ReviewDTO> getAllReviews(ReviewFilterRequest filter);

    /**
     * Duyệt/từ chối review (Admin)
     */
    ReviewDTO updateReviewStatus(Long reviewId, ReviewActionRequest request);

    /**
     * Phản hồi review (Admin)
     */
    ReviewDTO replyToReview(Long reviewId, AdminReplyRequest request);

    /**
     * Xóa review (Admin)
     */
    void adminDeleteReview(Long reviewId);

    /**
     * Đếm reviews chờ duyệt
     */
    long countPendingReviews();

    // ================= MAPPER =================

    /**
     * Chuyển đổi Entity sang DTO
     */
    ReviewDTO mapToDTO(Review review);
}
