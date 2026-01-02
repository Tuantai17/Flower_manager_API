package com.flower.manager.dto.chat;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for ChatSession response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatSessionDTO {
    private Long id;
    private Long userId;
    private String guestId;
    private String status; // ACTIVE, WAITING_STAFF, WITH_STAFF, CLOSED
    private String title;
    private Long staffId;
    private String staffName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private LocalDateTime updatedAt;
    private List<ChatMessageDTO> messages;
    private Long unreadCount;
}
