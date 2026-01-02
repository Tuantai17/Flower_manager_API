package com.flower.manager.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Request DTO for sending a chat message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {

    /**
     * Session ID (required if continuing existing session)
     */
    private Long sessionId;

    /**
     * Guest ID for non-authenticated users
     */
    private String guestId;

    /**
     * Message content
     */
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    @Size(max = 2000, message = "Tin nhắn không được vượt quá 2000 ký tự")
    private String content;
}
