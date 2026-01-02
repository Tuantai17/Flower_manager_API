package com.flower.manager.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for ChatMessage response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private Long sessionId;
    private String senderType; // USER, BOT, STAFF, SYSTEM
    private Long senderId;
    private String senderName;
    private String content;
    private String messageType; // TEXT, PRODUCT, QUICK_REPLY
    private String metadata; // JSON for product suggestions, etc.
    private LocalDateTime sentAt;
    private Boolean isRead;
}
