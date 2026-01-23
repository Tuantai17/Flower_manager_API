package com.flower.manager.dto.article;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cập nhật bài viết
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleUpdateRequest {

    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @Size(max = 500, message = "Tóm tắt tối đa 500 ký tự")
    private String summary;

    private String content;

    @Size(max = 500, message = "URL thumbnail tối đa 500 ký tự")
    private String thumbnail;

    @Size(max = 255, message = "Tags tối đa 255 ký tự")
    private String tags;

    @Size(max = 100, message = "Tên tác giả tối đa 100 ký tự")
    private String author;

    /**
     * Option: true = cập nhật slug theo title mới
     * false = giữ slug cũ (mặc định)
     */
    private Boolean updateSlug;
}
