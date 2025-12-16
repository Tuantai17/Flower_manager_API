package com.flower.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO cho việc tạo/cập nhật Category
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục tối đa 100 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 100, message = "Slug tối đa 100 ký tự")
    private String slug;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    private String imageUrl;

    /**
     * ID của danh mục cha
     * null = danh mục cấp 1 (cha)
     * có giá trị = danh mục cấp 2 (con)
     */
    private Long parentId;

    private String parentName;

    private Boolean active = true;

    private Integer sortOrder = 0;
}
