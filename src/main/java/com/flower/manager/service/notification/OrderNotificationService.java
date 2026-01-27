package com.flower.manager.service.notification;

import com.flower.manager.dto.notification.NotificationPayload;
import com.flower.manager.entity.Notification;
import com.flower.manager.entity.Order;
import com.flower.manager.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Service để gửi realtime notifications cho đơn hàng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    /**
     * Thông báo cho admin khi có đơn hàng mới
     */
    @Transactional
    public void notifyAdminNewOrder(Order order) {
        log.info("Notifying admins about new order: {}", order.getOrderCode());

        String customerName = order.getSenderName() != null ? order.getSenderName() : "Khách hàng";
        String formattedPrice = formatCurrency(order.getFinalPrice());

        String title = "Đơn hàng mới #" + order.getOrderCode();
        String content = customerName + " - " + formattedPrice;
        String url = "/admin/orders/" + order.getId();

        // Save to DB for all admins
        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("ORDER_NEW")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(order.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime to all admins
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("ORDER_NEW")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/orders/new", payload);
        log.info("Broadcasted new order notification for: {}", order.getOrderCode());
    }

    /**
     * Thông báo cho user khi đặt hàng thành công
     */
    @Transactional
    public void notifyUserOrderCreated(Order order) {
        Long userId = order.getUser() != null ? order.getUser().getId() : null;
        if (userId == null)
            return;

        log.info("Notifying user {} about new order created: {}", userId, order.getOrderCode());

        String formattedPrice = formatCurrency(order.getFinalPrice());
        String title = "Đặt hàng thành công #" + order.getOrderCode();
        String content = "Đơn hàng " + formattedPrice + " đang chờ xử lý";
        String url = "/profile/orders/" + order.getId();

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("ORDER_CREATED")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(order.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("ORDER_CREATED")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
        log.info("Sent order created notification to user {} for order: {}", userId, order.getOrderCode());
    }

    /**
     * Thông báo cho user khi đơn hàng thay đổi trạng thái
     */
    @Transactional
    public void notifyUserOrderStatusChange(Order order, String newStatusDisplayName) {
        Long userId = order.getUser() != null ? order.getUser().getId() : null;
        if (userId == null)
            return;

        log.info("Notifying user {} about order status change: {}", userId, order.getOrderCode());

        String title = "Cập nhật đơn hàng #" + order.getOrderCode();
        String content = "Trạng thái: " + newStatusDisplayName;
        String url = "/profile/orders/" + order.getId();

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("ORDER_STATUS")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(order.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("ORDER_STATUS")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
    }

    /**
     * Thông báo cho admin khi đơn hàng bị hủy
     */
    @Transactional
    public void notifyAdminOrderCancelled(Order order, String reason) {
        log.info("Notifying admins about cancelled order: {}", order.getOrderCode());

        String title = "Đơn hàng bị hủy #" + order.getOrderCode();
        String content = reason != null ? reason : "Khách hàng hủy đơn";
        String url = "/admin/orders/" + order.getId();

        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("ORDER_CANCELLED")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(order.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("ORDER_CANCELLED")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/orders/cancelled", payload);
    }

    /**
     * Thông báo cho admin khi thanh toán thành công
     */
    @Transactional
    public void notifyAdminPaymentReceived(Order order) {
        log.info("Notifying admins about payment for order: {}", order.getOrderCode());

        String formattedPrice = formatCurrency(order.getFinalPrice());
        String title = "Thanh toán thành công #" + order.getOrderCode();
        String content = order.getPaymentMethod().getDisplayName() + " - " + formattedPrice;
        String url = "/admin/orders/" + order.getId();

        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("ORDER_PAYMENT")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(order.getId())
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("ORDER_PAYMENT")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/orders/payment", payload);
    }

    // ==================== HELPER ====================

    private String formatCurrency(BigDecimal amount) {
        if (amount == null)
            return "0 VNĐ";
        return String.format("%,.0f VNĐ", amount);
    }
}
