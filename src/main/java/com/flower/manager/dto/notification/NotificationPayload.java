package com.flower.manager.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for realtime notification payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload {

    private Long id;

    /**
     * Type: TICKET_NEW, TICKET_MESSAGE, TICKET_STATUS
     */
    private String type;

    private String title;

    private String content;

    /**
     * URL to navigate when clicked
     */
    private String url;

    private boolean isRead;

    private LocalDateTime createdAt;
}
