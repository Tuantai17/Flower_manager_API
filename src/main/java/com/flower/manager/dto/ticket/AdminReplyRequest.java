package com.flower.manager.dto.ticket;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho admin phản hồi ticket
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReplyRequest {

    @NotBlank(message = "Nội dung phản hồi không được để trống")
    private String content;

    private String newStatus; // Optional: OPEN, IN_PROGRESS, RESOLVED, CLOSED
}
