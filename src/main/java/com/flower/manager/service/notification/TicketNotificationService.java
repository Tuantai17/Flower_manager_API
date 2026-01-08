package com.flower.manager.service.notification;

import com.flower.manager.dto.notification.NotificationPayload;
import com.flower.manager.dto.ticket.TicketMessageDTO;
import com.flower.manager.dto.ticket.TicketRealtimePayload;
import com.flower.manager.entity.Notification;
import com.flower.manager.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service để gửi realtime notifications qua WebSocket và lưu vào DB
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TicketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;

    // ==================== TICKET CHAT REALTIME ====================

    /**
     * Broadcast message mới đến ticket channel
     * Cả admin và user đang xem ticket đều nhận được
     */
    public void broadcastNewMessage(Long ticketId, TicketMessageDTO message) {
        log.info("Broadcasting new message to /topic/tickets/{}", ticketId);

        TicketRealtimePayload payload = TicketRealtimePayload.builder()
                .ticketId(ticketId)
                .type("MESSAGE")
                .data(message)
                .build();

        messagingTemplate.convertAndSend("/topic/tickets/" + ticketId, payload);
    }

    /**
     * Broadcast thay đổi trạng thái ticket
     */
    public void broadcastStatusChange(Long ticketId, String newStatus, String statusDisplayName) {
        log.info("Broadcasting status change to /topic/tickets/{}: {}", ticketId, newStatus);

        TicketRealtimePayload payload = TicketRealtimePayload.builder()
                .ticketId(ticketId)
                .type("STATUS")
                .data(Map.of(
                        "status", newStatus,
                        "statusDisplayName", statusDisplayName,
                        "timestamp", LocalDateTime.now().toString()))
                .build();

        messagingTemplate.convertAndSend("/topic/tickets/" + ticketId, payload);
    }

    // ==================== ADMIN NOTIFICATIONS ====================

    /**
     * Thông báo cho admin khi có ticket mới
     */
    @Transactional
    public void notifyAdminNewTicket(Long ticketId, String ticketCode, String customerName, String subject) {
        log.info("Notifying admins about new ticket: {}", ticketCode);

        String title = "Ticket mới từ " + customerName;
        String content = subject != null ? subject : "Yêu cầu hỗ trợ mới";
        String url = "/admin/tickets/" + ticketId;

        // Save to DB for all admins
        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("TICKET_NEW")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(ticketId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("TICKET_NEW")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/tickets/new", payload);
    }

    /**
     * Thông báo cho admin khi user gửi message
     */
    @Transactional
    public void notifyAdminNewMessage(Long ticketId, String ticketCode, String senderName) {
        log.info("Notifying admins about new message in ticket: {}", ticketCode);

        String title = "Tin nhắn mới - " + ticketCode;
        String content = "Tin nhắn từ " + senderName;
        String url = "/admin/tickets/" + ticketId;

        // Save to DB for all admins
        Notification notification = Notification.builder()
                .recipientRole("ALL_ADMINS")
                .type("TICKET_MESSAGE")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(ticketId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("TICKET_MESSAGE")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/admin/tickets/message", payload);
    }

    // ==================== USER NOTIFICATIONS ====================

    /**
     * Thông báo cho user khi admin reply
     */
    @Transactional
    public void notifyUserAdminReply(Long userId, Long ticketId, String ticketCode, String adminName) {
        if (userId == null)
            return;

        log.info("Notifying user {} about admin reply in ticket: {}", userId, ticketCode);

        String title = "Phản hồi từ " + adminName;
        String content = "Ticket " + ticketCode + " có phản hồi mới";
        String url = "/tickets/" + ticketId;

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("TICKET_REPLY")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(ticketId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("TICKET_REPLY")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
    }

    /**
     * Thông báo cho user khi ticket đổi trạng thái
     */
    @Transactional
    public void notifyUserStatusChange(Long userId, Long ticketId, String ticketCode, String statusDisplayName) {
        if (userId == null)
            return;

        log.info("Notifying user {} about status change in ticket: {}", userId, ticketCode);

        String title = "Trạng thái ticket thay đổi";
        String content = ticketCode + " → " + statusDisplayName;
        String url = "/tickets/" + ticketId;

        // Save to DB
        Notification notification = Notification.builder()
                .recipientId(userId)
                .recipientRole("USER")
                .type("TICKET_STATUS")
                .title(title)
                .content(content)
                .url(url)
                .referenceId(ticketId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);

        // Broadcast realtime
        NotificationPayload payload = NotificationPayload.builder()
                .id(notification.getId())
                .type("TICKET_STATUS")
                .title(title)
                .content(content)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/user/" + userId + "/notifications", payload);
    }

    // ==================== READ NOTIFICATIONS ====================

    /**
     * Lấy danh sách notifications cho admin
     */
    @Transactional(readOnly = true)
    public Page<NotificationPayload> getAdminNotifications(Long adminId, Pageable pageable) {
        return notificationRepository.findForAdmin(adminId, pageable)
                .map(this::toPayload);
    }

    /**
     * Lấy notifications chưa đọc cho admin
     */
    @Transactional(readOnly = true)
    public List<NotificationPayload> getUnreadAdminNotifications(Long adminId) {
        return notificationRepository.findUnreadForAdmin(adminId).stream()
                .map(this::toPayload)
                .collect(Collectors.toList());
    }

    /**
     * Đếm notifications chưa đọc cho admin
     */
    @Transactional(readOnly = true)
    public Long countUnreadForAdmin(Long adminId) {
        return notificationRepository.countUnreadForAdmin(adminId);
    }

    /**
     * Đánh dấu tất cả đã đọc cho admin
     */
    @Transactional
    public void markAllReadForAdmin(Long adminId) {
        notificationRepository.markAllReadForAdmin(adminId);
    }

    /**
     * Đánh dấu 1 notification đã đọc
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    /**
     * Lấy notifications cho user
     */
    @Transactional(readOnly = true)
    public Page<NotificationPayload> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findForUser(userId, pageable)
                .map(this::toPayload);
    }

    /**
     * Đếm notifications chưa đọc cho user
     */
    @Transactional(readOnly = true)
    public Long countUnreadForUser(Long userId) {
        return notificationRepository.countUnreadForUser(userId);
    }

    /**
     * Đánh dấu tất cả đã đọc cho user
     */
    @Transactional
    public void markAllReadForUser(Long userId) {
        notificationRepository.markAllReadForUser(userId);
    }

    // ==================== DELETE NOTIFICATIONS ====================

    /**
     * Xóa 1 notification
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteNotification(notificationId);
        log.info("Deleted notification: {}", notificationId);
    }

    /**
     * Xóa nhiều notifications
     */
    @Transactional
    public void deleteNotifications(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            notificationRepository.deleteNotifications(ids);
            log.info("Deleted {} notifications", ids.size());
        }
    }

    /**
     * Xóa tất cả notifications cho admin
     */
    @Transactional
    public void deleteAllForAdmin(Long adminId) {
        notificationRepository.deleteAllForAdmin(adminId);
        log.info("Deleted all notifications for admin: {}", adminId);
    }

    /**
     * Xóa tất cả notifications cho user
     */
    @Transactional
    public void deleteAllForUser(Long userId) {
        notificationRepository.deleteAllForUser(userId);
        log.info("Deleted all notifications for user: {}", userId);
    }

    /**
     * Xóa các notifications đã đọc cho admin
     */
    @Transactional
    public void deleteReadForAdmin(Long adminId) {
        notificationRepository.deleteReadForAdmin(adminId);
        log.info("Deleted read notifications for admin: {}", adminId);
    }

    /**
     * Xóa các notifications đã đọc cho user
     */
    @Transactional
    public void deleteReadForUser(Long userId) {
        notificationRepository.deleteReadForUser(userId);
        log.info("Deleted read notifications for user: {}", userId);
    }

    // ==================== HELPER ====================

    private NotificationPayload toPayload(Notification n) {
        return NotificationPayload.builder()
                .id(n.getId())
                .type(n.getType())
                .title(n.getTitle())
                .content(n.getContent())
                .url(n.getUrl())
                .isRead(n.getIsRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
