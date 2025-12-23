package com.flower.manager.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * DTO cho việc cập nhật Category (Update)
 * Tất cả field đều optional - chỉ update những field được gửi lên
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUpdateDTO {

    @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
    private String name;

    @Size(max = 100, message = "Slug tối đa 100 ký tự")
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    @Size(max = 255, message = "URL hình ảnh tối đa 255 ký tự")
    private String imageUrl;

    /**
     * ID của danh mục cha
     * null = chuyển thành danh mục cấp 1 (cha)
     * có giá trị = chuyển thành danh mục cấp 2 (con)
     */
    private Long parentId;

    private Boolean active;

    private Integer sortOrder;
}
