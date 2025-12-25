package com.flower.manager.repository;

import com.flower.manager.entity.Review;
import com.flower.manager.entity.Review.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Review entity
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Tìm reviews theo product ID (chỉ lấy APPROVED)
     */
    @Query("SELECT r FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' ORDER BY r.createdAt DESC")
    Page<Review> findApprovedByProductId(@Param("productId") Long productId, Pageable pageable);

    /**
     * Tìm tất cả reviews của một sản phẩm (Admin)
     */
    Page<Review> findByProductIdOrderByCreatedAtDesc(Long productId, Pageable pageable);

    /**
     * Tìm reviews của user
     */
    Page<Review> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Kiểm tra user đã review sản phẩm trong đơn hàng chưa
     */
    boolean existsByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    /**
     * Kiểm tra user đã review sản phẩm chưa (bất kể đơn hàng nào)
     */
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    /**
     * Tìm review theo user, product và order
     */
    Optional<Review> findByUserIdAndProductIdAndOrderId(Long userId, Long productId, Long orderId);

    /**
     * Lấy reviews theo trạng thái (Admin)
     */
    Page<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status, Pageable pageable);

    /**
     * Tìm kiếm reviews (Admin)
     */
    @Query("SELECT r FROM Review r WHERE " +
            "(:status IS NULL OR r.status = :status) AND " +
            "(:productId IS NULL OR r.product.id = :productId) AND " +
            "(:keyword IS NULL OR LOWER(r.comment) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(r.user.username) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY r.createdAt DESC")
    Page<Review> findWithFilters(
            @Param("status") ReviewStatus status,
            @Param("productId") Long productId,
            @Param("keyword") String keyword,
            Pageable pageable);

    /**
     * Tính điểm trung bình của sản phẩm
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    Double getAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * Đếm số reviews của sản phẩm (APPROVED)
     */
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED'")
    Long countApprovedByProductId(@Param("productId") Long productId);

    /**
     * Thống kê số reviews theo rating của sản phẩm
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.status = 'APPROVED' GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> countByRatingForProduct(@Param("productId") Long productId);

    /**
     * Đếm reviews chờ duyệt (Admin dashboard)
     */
    long countByStatus(ReviewStatus status);
}
