package com.flower.manager.dto.ticket;

import com.flower.manager.enums.TicketCategory;
import com.flower.manager.enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO hiển thị thông tin ticket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketDTO {

    private Long id;
    private String ticketCode;

    // Thông tin người gửi
    private Long userId;
    private String name;
    private String email;
    private String phone;

    // Thông tin ticket
    private String subject;
    private TicketCategory category;
    private String categoryDisplayName;
    private TicketStatus status;
    private String statusDisplayName;
    private Integer priority;

    // Admin xử lý
    private Long assignedAdminId;
    private String assignedAdminName;

    // Tin nhắn
    private List<TicketMessageDTO> messages;
    private int messageCount;
    private int unreadCount;
    private String lastMessage;
    private LocalDateTime lastMessageAt;

    // Thời gian
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
}
