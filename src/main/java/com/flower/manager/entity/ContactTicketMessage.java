package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity ContactTicketMessage - Tin nhắn trong ticket
 * Lưu trữ từng tin nhắn của user/admin/system
 */
@Entity
@Table(name = "contact_ticket_messages", indexes = {
        @Index(name = "idx_ticket_message_ticket", columnList = "ticket_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactTicketMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Ticket chứa tin nhắn
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private ContactTicket ticket;

    /**
     * Loại người gửi:
     * USER - Khách hàng
     * ADMIN - Nhân viên hỗ trợ
     * SYSTEM - Thông báo hệ thống
     */
    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;

    /**
     * ID người gửi (userId hoặc adminId)
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * Tên hiển thị người gửi
     */
    @Column(name = "sender_name", length = 100)
    private String senderName;

    /**
     * Nội dung tin nhắn
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Đã đọc chưa
     */
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    /**
     * Thời gian gửi
     */
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isFromUser() {
        return "USER".equals(senderType);
    }

    public boolean isFromAdmin() {
        return "ADMIN".equals(senderType);
    }

    public boolean isFromSystem() {
        return "SYSTEM".equals(senderType);
    }
}
