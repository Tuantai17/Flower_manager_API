package com.flower.manager.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO for online status updates via WebSocket
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OnlineStatusDTO {

    /**
     * User ID (null for guests)
     */
    private Long userId;

    /**
     * Guest ID (for anonymous users)
     */
    private String guestId;

    /**
     * User type: USER, ADMIN, STAFF
     */
    private String userType;

    /**
     * Display name
     */
    private String displayName;

    /**
     * Online status: true = online, false = offline
     */
    private boolean online;

    /**
     * Session ID currently active
     */
    private Long sessionId;

    /**
     * Last seen timestamp
     */
    private LocalDateTime lastSeen;
}
