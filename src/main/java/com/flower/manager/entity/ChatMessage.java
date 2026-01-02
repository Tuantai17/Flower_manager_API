package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity ChatMessage - Tin nhắn trong chat
 * Lưu trữ từng tin nhắn của user/bot/staff
 */
@Entity
@Table(name = "chat_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Phiên chat chứa tin nhắn
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSession session;

    /**
     * Loại người gửi:
     * USER - Khách hàng
     * BOT - AI Chatbot (Gemini)
     * STAFF - Nhân viên hỗ trợ
     * SYSTEM - Thông báo hệ thống
     */
    @Column(name = "sender_type", nullable = false, length = 10)
    private String senderType;

    /**
     * ID người gửi (userId hoặc staffId)
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
     * Loại tin nhắn:
     * TEXT - Văn bản thuần
     * PRODUCT - Gợi ý sản phẩm
     * QUICK_REPLY - Các lựa chọn nhanh
     * IMAGE - Hình ảnh (Phase 2)
     */
    @Column(name = "message_type", length = 20)
    @Builder.Default
    private String messageType = "TEXT";

    /**
     * Metadata bổ sung (JSON) - VD: product recommendations
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /**
     * Thời gian gửi
     */
    @Column(name = "sent_at", updatable = false)
    private LocalDateTime sentAt;

    /**
     * Đã đọc chưa
     */
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isFromUser() {
        return "USER".equals(senderType);
    }

    public boolean isFromBot() {
        return "BOT".equals(senderType);
    }

    public boolean isFromStaff() {
        return "STAFF".equals(senderType);
    }
}
