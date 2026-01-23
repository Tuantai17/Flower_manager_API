package com.flower.manager.dto.article;

import com.flower.manager.entity.Article;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO response rút gọn cho danh sách bài viết
 * Dùng cho: Public list, Admin list
 * Không chứa content để giảm payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListItemResponse {

    private Long id;
    private String title;
    private String slug;
    private String summary;
    private String thumbnail;
    private String tags;
    private String author;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;

    /**
     * Chuyển từ Entity sang DTO
     */
    public static ArticleListItemResponse fromEntity(Article article) {
        if (article == null)
            return null;

        return ArticleListItemResponse.builder()
                .id(article.getId())
                .title(article.getTitle())
                .slug(article.getSlug())
                .summary(article.getSummary())
                .thumbnail(article.getThumbnail())
                .tags(article.getTags())
                .author(article.getAuthor())
                .publishedAt(article.getPublishedAt())
                .createdAt(article.getCreatedAt())
                .build();
    }
}
