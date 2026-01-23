package com.flower.manager.dto.article;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * B3) Request để AI generate bài viết
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleAIGenerateRequest {

    /**
     * Chủ đề bài viết
     * Ví dụ: "Ý nghĩa hoa hồng đỏ ngày Valentine"
     */
    @NotBlank(message = "Chủ đề không được để trống")
    @Size(max = 500, message = "Chủ đề tối đa 500 ký tự")
    private String topic;

    /**
     * Giọng văn
     * Ví dụ: "ấm áp, tư vấn", "chuyên nghiệp", "trẻ trung"
     */
    @Size(max = 100, message = "Giọng văn tối đa 100 ký tự")
    private String tone;

    /**
     * Từ khóa SEO
     * Ví dụ: ["hoa hồng", "valentine", "quà tặng"]
     */
    private List<String> keywords;

    /**
     * Độ dài bài viết
     * Giá trị: "ngắn" (200-300 từ), "vừa" (400-600 từ), "dài" (800-1000 từ)
     */
    @Builder.Default
    private String length = "vừa";

    /**
     * Có thêm Call-to-Action (CTA) không
     */
    @Builder.Default
    private Boolean callToAction = true;

    /**
     * Tên tác giả (optional)
     */
    private String author;
}
