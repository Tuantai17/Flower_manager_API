package com.flower.manager.dto.article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * B3) Response từ AI generate (trước khi lưu vào DB)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAIGenerateResponse {

    /**
     * Tiêu đề được AI generate
     */
    private String title;

    /**
     * Gợi ý slug (từ title)
     */
    private String slugSuggestion;

    /**
     * Tóm tắt bài viết
     */
    private String summary;

    /**
     * Nội dung HTML đầy đủ
     */
    private String content;

    /**
     * Gợi ý tags
     */
    private List<String> tagsSuggestion;

    /**
     * Gợi ý prompt để tạo hình ảnh thumbnail (optional)
     */
    private String thumbnailPrompt;

    /**
     * ID bài viết sau khi lưu (nếu saveToDraft = true)
     */
    private Long savedArticleId;
}
