package com.flower.manager.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO hiển thị tin nhắn trong ticket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageDTO {

    private Long id;
    private String senderType; // USER, ADMIN, SYSTEM
    private Long senderId;
    private String senderName;
    private String content;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
