package com.flower.manager.dto.article;

import com.flower.manager.entity.Article;
import com.flower.manager.enums.ArticleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response đầy đủ thông tin bài viết
 * Dùng cho: Admin list, detail; Public detail
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleResponse {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String content;
    private String thumbnail;
    private String tags;
    private String author;
    private ArticleStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime publishedAt;
    private Boolean aiGenerated;

    /**
     * Chuyển từ Entity sang DTO
     */
    public static ArticleResponse fromEntity(Article article) {
        if (article == null)
            return null;

        return ArticleResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .summary(article.getSummary())
                .content(article.getContent())
                .thumbnail(article.getThumbnail())
                .tags(article.getTags())
                .author(article.getAuthor())
                .status(article.getStatus())
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .scheduledAt(article.getScheduledAt())
                .publishedAt(article.getPublishedAt())
                .aiGenerated(article.getAiGenerated())
                .build();
    }
}
