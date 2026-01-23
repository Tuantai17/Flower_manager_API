package com.flower.manager.dto.article;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de import bai viet tu URL ben ngoai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleImportRequest {

    /**
     * URL cua bai viet can import
     */
    @NotBlank(message = "URL khong duoc de trong")
    private String url;

    /**
     * Tac gia mac dinh (neu khong lay duoc tu trang goc)
     */
    private String defaultAuthor;

    /**
     * Tags mac dinh
     */
    private String defaultTags;

    /**
     * Co ghi de thumbnail khong
     */
    private String customThumbnail;
}
