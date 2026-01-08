package com.flower.manager.service.notification;

import com.flower.manager.dto.notification.NotificationPayload;
import com.flower.manager.dto.review.ReviewDTO;
import com.flower.manager.entity.Notification;
import com.flower.manager.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service để gửi realtime notifications cho Review
 * - Thông báo Admin khi có đánh giá mới
 * - Thông báo User khi Admin phản hồi
 * - Broadcast realtime updates cho review
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    // ==================== ADMIN NOTIFICATIONS ====================

    /**
     * Thông báo cho admin khi có đánh giá mới
     */
    @Transactional
    public void notifyAdminNewReview(ReviewDTO review) {
        log.info("🔔 Notifying admins about new review from: {}", review.getUserFullName());

        String title = "Đánh giá mới từ "
                + (review.getUserFullName() != null ? review.getUserFullName() : review.getUsername());
        String content = review.getProductName() + " - " + review.getRating() + " sao";
        String url = "/admin/products/" + review.getProductId();

        // Save to DB for all admins
        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("REVIEW_NEW")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(review.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("REVIEW_NEW")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/reviews/new", payload);
        log.info("✅ Admin notification sent for new review: {}", review.getId());
    }

    /**
     * Thông báo cho admin khi có cập nhật trạng thái review
     */
    @Transactional
    public void notifyAdminReviewStatusChanged(ReviewDTO review, String action) {
        log.info("🔔 Broadcasting review status change: {} - {}", review.getId(), action);

        // Broadcast to admin review list for realtime update
        Map<String, Object> payload = Map.of(
                "type", "REVIEW_STATUS_CHANGED",
                "reviewId", review.getId(),
                "status", review.getStatus().name(),
                "statusDisplayName", review.getStatusDisplayName(),
                "action", action,
                "timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/admin/reviews/update", payload);
    }

    // ==================== USER NOTIFICATIONS ====================

    /**
     * Thông báo cho user khi admin phản hồi đánh giá
     */
    @Transactional
    public void notifyUserAdminReply(Long userId, ReviewDTO review) {
        if (userId == null)
            return;

        log.info("🔔 Notifying user {} about admin reply to review: {}", userId, review.getId());

        String title = "Shop đã phản hồi đánh giá của bạn";
        String content = "Sản phẩm: " + review.getProductName();
        String url = "/profile/orders"; // Redirect to order history where they can see reviews

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("REVIEW_REPLY")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(review.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime to specific user
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("REVIEW_REPLY")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
        log.info("✅ User notification sent for review reply: {}", review.getId());
    }

    /**
     * Thông báo cho user khi review được duyệt
     */
    @Transactional
    public void notifyUserReviewApproved(Long userId, ReviewDTO review) {
        if (userId == null)
            return;

        log.info("🔔 Notifying user {} about review approval: {}", userId, review.getId());

        String title = "Đánh giá của bạn đã được duyệt!";
        String content = "Đánh giá cho " + review.getProductName() + " đã được hiển thị";
        String url = "/product/" + review.getProductSlug();

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("REVIEW_APPROVED")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(review.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("REVIEW_APPROVED")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
    }

    // ==================== PRODUCT REALTIME UPDATE ====================

    /**
     * Broadcast cập nhật review cho trang sản phẩm (hiển thị review mới realtime)
     */
    public void broadcastProductReviewUpdate(Long productId, ReviewDTO review, String action) {
        log.info("📡 Broadcasting product review update: productId={}, action={}", productId, action);

        Map<String, Object> payload = Map.of(
                "type", "PRODUCT_REVIEW_UPDATE",
                "productId", productId,
                "action", action, // "NEW", "UPDATED", "REPLY"
                "review", review,
                "timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/products/" + productId + "/reviews", payload);
    }
}
