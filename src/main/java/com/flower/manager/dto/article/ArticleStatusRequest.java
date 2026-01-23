package com.flower.manager.dto.article;

import com.flower.manager.enums.ArticleStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO thay đổi trạng thái bài viết
 * 
 * Rules:
 * - DRAFT -> PUBLISHED: publish ngay
 * - DRAFT -> SCHEDULED: scheduledAt bắt buộc và > now
 * - SCHEDULED -> PUBLISHED: publish ngay (bỏ qua schedule)
 * - PUBLISHED -> ARCHIVED: archive
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleStatusRequest {

    @NotNull(message = "Status không được để trống")
    private ArticleStatus status;

    /**
     * Bắt buộc khi status = SCHEDULED
     * Phải lớn hơn thời điểm hiện tại
     */
    private LocalDateTime scheduledAt;
}
