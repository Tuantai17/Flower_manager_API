package com.flower.manager.dto.chat;

import lombok.*;

/**
 * DTO for typing indicator via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingIndicatorDTO {

    /**
     * Session/Conversation ID
     */
    private Long sessionId;

    /**
     * User ID who is typing
     */
    private Long userId;

    /**
     * Guest ID (for anonymous users)
     */
    private String guestId;

    /**
     * User type: USER, ADMIN
     */
    private String userType;

    /**
     * Display name
     */
    private String displayName;

    /**
     * Is typing: true = typing, false = stopped
     */
    private boolean typing;
}
