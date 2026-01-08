package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity lưu trữ thông báo cho admin và user
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_recipient", columnList = "recipientId, recipientRole"),
        @Index(name = "idx_notification_unread", columnList = "recipientId, isRead"),
        @Index(name = "idx_notification_created", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ID người nhận (userId hoặc adminId)
     */
    @Column(nullable = true)
    private Long recipientId;

    /**
     * Role người nhận: ADMIN, USER, ALL_ADMINS
     */
    @Column(nullable = false, length = 20)
    private String recipientRole;

    /**
     * Loại thông báo: TICKET_NEW, TICKET_MESSAGE, TICKET_STATUS, ORDER_NEW,
     * ORDER_STATUS, STOCK_LOW...
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Tiêu đề thông báo
     */
    @Column(nullable = false, length = 255)
    private String title;

    /**
     * Nội dung chi tiết
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * URL đích khi click vào thông báo
     */
    @Column(length = 500)
    private String url;

    /**
     * Reference ID liên quan (ticketId, orderId...)
     */
    private Long referenceId;

    /**
     * Đã đọc chưa
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
