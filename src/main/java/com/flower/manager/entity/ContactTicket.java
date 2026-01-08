package com.flower.manager.entity;

import com.flower.manager.enums.TicketCategory;
import com.flower.manager.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entity ContactTicket - Ticket liên hệ/hỗ trợ
 * Lưu trữ thông tin yêu cầu liên hệ từ khách hàng
 */
@Entity
@Table(name = "contact_tickets", indexes = {
        @Index(name = "idx_ticket_status", columnList = "status"),
        @Index(name = "idx_ticket_email", columnList = "email"),
        @Index(name = "idx_ticket_user", columnList = "user_id"),
        @Index(name = "idx_ticket_code", columnList = "ticket_code", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã ticket duy nhất (TCK + timestamp + random)
     */
    @Column(name = "ticket_code", unique = true, nullable = false, length = 20)
    private String ticketCode;

    /**
     * User tạo ticket (nullable cho guest)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Họ tên người gửi
     */
    @Column(nullable = false)
    private String name;

    /**
     * Email liên hệ
     */
    @Column(nullable = false)
    private String email;

    /**
     * Số điện thoại (optional)
     */
    @Column(length = 20)
    private String phone;

    /**
     * Tiêu đề ticket
     */
    @Column(nullable = false)
    private String subject;

    /**
     * Phân loại ticket
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketCategory category = TicketCategory.OTHER;

    /**
     * Trạng thái ticket
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private TicketStatus status = TicketStatus.OPEN;

    /**
     * Độ ưu tiên (1-5, mặc định 3)
     */
    @Column
    @Builder.Default
    private Integer priority = 3;

    /**
     * Admin được giao xử lý
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    /**
     * Danh sách tin nhắn trong ticket
     */
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ContactTicketMessage> messages = new ArrayList<>();

    /**
     * Thời gian tạo
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Thời gian đóng ticket
     */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (ticketCode == null) {
            ticketCode = generateTicketCode();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if (status == TicketStatus.CLOSED && closedAt == null) {
            closedAt = LocalDateTime.now();
        }
    }

    /**
     * Tạo mã ticket duy nhất
     */
    private String generateTicketCode() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        String uuid = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "TCK" + timestamp + uuid;
    }

    /**
     * Kiểm tra ticket còn mở
     */
    public boolean isOpen() {
        return status == TicketStatus.OPEN || status == TicketStatus.IN_PROGRESS;
    }

    /**
     * Đếm số tin nhắn chưa đọc từ user
     */
    public long getUnreadUserMessagesCount() {
        return messages.stream()
                .filter(m -> "USER".equals(m.getSenderType()) && !Boolean.TRUE.equals(m.getIsRead()))
                .count();
    }

    /**
     * Đếm số tin nhắn chưa đọc từ admin
     */
    public long getUnreadAdminMessagesCount() {
        return messages.stream()
                .filter(m -> "ADMIN".equals(m.getSenderType()) && !Boolean.TRUE.equals(m.getIsRead()))
                .count();
    }
}
