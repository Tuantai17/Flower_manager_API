package com.flower.manager.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO cho hiển thị Menu Category (đa cấp cha-con)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryMenuDTO {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;

    /**
     * Danh sách các danh mục con (cấp 2)
     */
    @Builder.Default
    private List<CategoryMenuDTO> children = new ArrayList<>();
}
