package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity ChatSession - Phiên chat
 * Lưu trữ thông tin phiên chat của user hoặc guest
 */
@Entity
@Table(name = "chat_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User đang chat (null nếu là guest)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * UUID cho guest user (khi user chưa đăng nhập)
     */
    @Column(name = "guest_id", length = 100)
    private String guestId;

    /**
     * Trạng thái phiên chat:
     * ACTIVE - Đang chat với bot
     * WAITING_STAFF - Chờ nhân viên hỗ trợ
     * WITH_STAFF - Đang chat với nhân viên
     * CLOSED - Đã đóng
     */
    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * ID nhân viên đang hỗ trợ (Phase 2)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private User staff;

    /**
     * Tiêu đề phiên chat (tóm tắt nội dung)
     */
    @Column(length = 200)
    private String title;

    /**
     * Thời gian bắt đầu
     */
    @Column(name = "started_at", updatable = false)
    private LocalDateTime startedAt;

    /**
     * Thời gian kết thúc
     */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /**
     * Thời gian cập nhật cuối
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Danh sách tin nhắn trong phiên chat
     */
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ChatMessage> messages = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        startedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public boolean isActive() {
        return "ACTIVE".equals(status) || "WITH_STAFF".equals(status);
    }

    public boolean isWaitingStaff() {
        return "WAITING_STAFF".equals(status);
    }

    public void close() {
        this.status = "CLOSED";
        this.endedAt = LocalDateTime.now();
    }
}
