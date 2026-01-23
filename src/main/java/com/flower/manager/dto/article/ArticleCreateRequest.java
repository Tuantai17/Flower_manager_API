package com.flower.manager.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO tạo bài viết mới
 * Mặc định status = DRAFT
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleCreateRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 255, message = "Tiêu đề tối đa 255 ký tự")
    private String title;

    @Size(max = 500, message = "Tóm tắt tối đa 500 ký tự")
    private String summary;

    @NotBlank(message = "Nội dung không được để trống")
    private String content;

    @Size(max = 500, message = "URL thumbnail tối đa 500 ký tự")
    private String thumbnail;

    @Size(max = 255, message = "Tags tối đa 255 ký tự")
    private String tags;

    @Size(max = 100, message = "Tên tác giả tối đa 100 ký tự")
    private String author;
}
