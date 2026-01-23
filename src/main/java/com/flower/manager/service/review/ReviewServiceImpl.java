package com.flower.manager.service.review;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.manager.dto.review.*;
import com.flower.manager.entity.*;
import com.flower.manager.entity.Review.ReviewStatus;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.*;
import com.flower.manager.service.notification.ReviewNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation của ReviewService
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ReviewNotificationService reviewNotificationService;

    // ================= USER METHODS =================

    @Override
    public ReviewDTO createReview(CreateReviewRequest request) {
        User user = getCurrentUser();

        // 1. Validate product exists
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        // 2. Validate order exists and belongs to user
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new BusinessException("ACCESS_DENIED", "Đơn hàng không thuộc về bạn");
        }

        // 3. Check order is completed/delivered
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            throw new BusinessException("ORDER_NOT_COMPLETED",
                    "Bạn chỉ có thể đánh giá sau khi đơn hàng đã giao thành công");
        }

        // 4. Check product was in the order
        boolean productInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(request.getProductId()));
        if (!productInOrder) {
            throw new BusinessException("PRODUCT_NOT_IN_ORDER", "Sản phẩm không có trong đơn hàng này");
        }

        // 5. Check if already reviewed
        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(user.getId(), request.getProductId(),
                request.getOrderId())) {
            throw new BusinessException("ALREADY_REVIEWED", "Bạn đã đánh giá sản phẩm này trong đơn hàng này rồi");
        }

        // 6. Create review - Auto approve for immediate display
        Review review = Review.builder()
                .product(product)
                .user(user)
                .order(order)
                .rating(request.getRating())
                .comment(request.getComment())
                .images(convertImagesToJson(request.getImages()))
                .status(ReviewStatus.APPROVED) // Auto approve - hiển thị ngay lập tức
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("User {} created review for product {} in order {} - Auto APPROVED",
                user.getUsername(), product.getId(), order.getOrderCode());

        ReviewDTO reviewDTO = mapToDTO(savedReview);

        // Send notification to admin about new review
        try {
            reviewNotificationService.notifyAdminNewReview(reviewDTO);
            // Broadcast to product page for realtime display immediately
            reviewNotificationService.broadcastProductReviewUpdate(product.getId(), reviewDTO, "NEW");
        } catch (Exception e) {
            log.error("Failed to send review notification: {}", e.getMessage());
        }

        return reviewDTO;
    }

    @Override
    public ReviewDTO updateReview(Long reviewId, UpdateReviewRequest request) {
        User user = getCurrentUser();
        Review review = getReviewById(reviewId);

        // Check ownership
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("ACCESS_DENIED", "Bạn không có quyền sửa đánh giá này");
        }

        // Only allow update if still PENDING
        if (review.getStatus() != ReviewStatus.PENDING) {
            throw new BusinessException("CANNOT_UPDATE", "Chỉ có thể sửa đánh giá chưa được duyệt");
        }

        // Update fields
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        if (request.getImages() != null) {
            review.setImages(convertImagesToJson(request.getImages()));
        }

        Review savedReview = reviewRepository.save(review);
        log.info("User {} updated review {}", user.getUsername(), reviewId);

        return mapToDTO(savedReview);
    }

    @Override
    public void deleteReview(Long reviewId) {
        User user = getCurrentUser();
        Review review = getReviewById(reviewId);

        // Check ownership
        if (!review.getUser().getId().equals(user.getId())) {
            throw new BusinessException("ACCESS_DENIED", "Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
        log.info("User {} deleted review {}", user.getUsername(), reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getMyReviews(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canReviewProduct(Long productId, Long orderId) {
        User user = getCurrentUser();

        // Check order exists and belongs to user
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty() || !orderOpt.get().getUser().getId().equals(user.getId())) {
            return false;
        }

        Order order = orderOpt.get();

        // Check order is completed
        if (order.getStatus() != OrderStatus.DELIVERED && order.getStatus() != OrderStatus.COMPLETED) {
            return false;
        }

        // Check product in order
        boolean productInOrder = order.getItems().stream()
                .anyMatch(item -> item.getProduct().getId().equals(productId));
        if (!productInOrder) {
            return false;
        }

        // Check not already reviewed
        return !reviewRepository.existsByUserIdAndProductIdAndOrderId(user.getId(), productId, orderId);
    }

    // ================= PUBLIC METHODS =================

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getProductReviews(Long productId, int page, int size) {
        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        Pageable pageable = PageRequest.of(page, size);
        return reviewRepository.findApprovedByProductId(productId, pageable)
                .map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductReviewStats getProductReviewStats(Long productId) {
        // Verify product exists
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        Long totalReviews = reviewRepository.countApprovedByProductId(productId);
        List<Object[]> ratingCounts = reviewRepository.countByRatingForProduct(productId);

        Map<Integer, Long> distribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            distribution.put(i, 0L);
        }
        for (Object[] row : ratingCounts) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            distribution.put(rating, count);
        }

        return ProductReviewStats.builder()
                .productId(productId)
                .averageRating(avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0)
                .totalReviews(totalReviews != null ? totalReviews : 0L)
                .ratingDistribution(distribution)
                .build();
    }

    // ================= ADMIN METHODS =================

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviews(ReviewFilterRequest filter) {
        Pageable pageable = PageRequest.of(
                filter.getPage(),
                filter.getSize(),
                Sort.by(
                        filter.getSortDir().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                        filter.getSortBy()));

        return reviewRepository.findWithFilters(
                filter.getStatus(),
                filter.getProductId(),
                filter.getKeyword(),
                pageable).map(this::mapToDTO);
    }

    @Override
    public ReviewDTO updateReviewStatus(Long reviewId, ReviewActionRequest request) {
        Review review = getReviewById(reviewId);
        Long userId = review.getUser().getId();

        review.setStatus(request.getStatus());

        Review savedReview = reviewRepository.save(review);
        log.info("Admin updated review {} status to {}", reviewId, request.getStatus());

        ReviewDTO reviewDTO = mapToDTO(savedReview);

        // Send notifications
        try {
            String action = request.getStatus().name();
            reviewNotificationService.notifyAdminReviewStatusChanged(reviewDTO, action);

            // Notify user if review is approved
            if (request.getStatus() == ReviewStatus.APPROVED) {
                reviewNotificationService.notifyUserReviewApproved(userId, reviewDTO);
                // Broadcast to product page for realtime update
                reviewNotificationService.broadcastProductReviewUpdate(
                        review.getProduct().getId(), reviewDTO, "NEW");
            }
        } catch (Exception e) {
            log.error("Failed to send review status notification: {}", e.getMessage());
        }

        return reviewDTO;
    }

    @Override
    public ReviewDTO replyToReview(Long reviewId, AdminReplyRequest request) {
        Review review = getReviewById(reviewId);
        Long userId = review.getUser().getId();
        Long productId = review.getProduct().getId();

        review.setAdminReply(request.getReply());
        review.setAdminReplyImages(convertImagesToJson(request.getImages()));
        review.setRepliedAt(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        log.info("Admin replied to review {} with {} images", reviewId,
                request.getImages() != null ? request.getImages().size() : 0);

        ReviewDTO reviewDTO = mapToDTO(savedReview);

        // Send notifications
        try {
            // Notify user about admin reply
            reviewNotificationService.notifyUserAdminReply(userId, reviewDTO);
            // Broadcast to product page for realtime update
            reviewNotificationService.broadcastProductReviewUpdate(productId, reviewDTO, "REPLY");
            // Broadcast to admin for realtime update
            reviewNotificationService.notifyAdminReviewStatusChanged(reviewDTO, "REPLY");
        } catch (Exception e) {
            log.error("Failed to send review reply notification: {}", e.getMessage());
        }

        return reviewDTO;
    }

    @Override
    public void adminDeleteReview(Long reviewId) {
        Review review = getReviewById(reviewId);
        reviewRepository.delete(review);
        log.info("Admin deleted review {}", reviewId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingReviews() {
        return reviewRepository.countByStatus(ReviewStatus.PENDING);
    }

    // ================= MAPPER =================

    @Override
    public ReviewDTO mapToDTO(Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .productId(review.getProduct().getId())
                .productName(review.getProduct().getName())
                .productSlug(review.getProduct().getSlug())
                .productThumbnail(review.getProduct().getThumbnail())
                .userId(review.getUser().getId())
                .username(review.getUser().getUsername())
                .userFullName(review.getUser().getFullName())
                .orderId(review.getOrder().getId())
                .orderCode(review.getOrder().getOrderCode())
                .rating(review.getRating())
                .comment(review.getComment())
                .images(convertJsonToImages(review.getImages()))
                .status(review.getStatus())
                .statusDisplayName(review.getStatus().getDisplayName())
                .adminReply(review.getAdminReply())
                .adminReplyImages(convertJsonToImages(review.getAdminReplyImages()))
                .repliedAt(review.getRepliedAt())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    // ================= HELPER METHODS =================

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("Vui lòng đăng nhập để thực hiện chức năng này");
        }
        String identifier = authentication.getName();
        return userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", reviewId));
    }

    private String convertImagesToJson(List<String> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(images);
        } catch (JsonProcessingException e) {
            log.error("Error converting images to JSON: {}", e.getMessage());
            return null;
        }
    }

    private List<String> convertJsonToImages(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error parsing images JSON: {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
