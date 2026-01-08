package com.flower.manager.dto.chat;

import lombok.*;

/**
 * DTO for sending messages via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebSocketMessageDTO {

    /**
     * Session/Conversation ID
     */
    private Long sessionId;

    /**
     * Sender user ID (null for guests)
     */
    private Long senderId;

    /**
     * Guest ID (for anonymous users)
     */
    private String guestId;

    /**
     * Sender type: USER, ADMIN, STAFF, BOT, SYSTEM
     */
    private String senderType;

    /**
     * Sender display name
     */
    private String senderName;

    /**
     * Message content
     */
    private String content;

    /**
     * Message type: TEXT, PRODUCT, IMAGE, TYPING, ONLINE
     */
    private String messageType;

    /**
     * Additional metadata (JSON)
     */
    private String metadata;

    /**
     * Timestamp
     */
    private String timestamp;
}
