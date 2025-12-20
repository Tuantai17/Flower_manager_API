package com.flower.manager.mapper;

import com.flower.manager.dto.CategoryCreateDTO;
import com.flower.manager.dto.CategoryDTO;
import com.flower.manager.dto.CategoryMenuDTO;
import com.flower.manager.dto.CategoryUpdateDTO;
import com.flower.manager.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper chuyển đổi giữa Entity và DTO
 */
@Component
public class CategoryMapper {

    // ============ Entity -> DTO ============

    /**
     * Chuyển Category entity sang CategoryDTO
     */
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .active(category.getActive())
                .sortOrder(category.getSortOrder())
                .build();
    }

    /**
     * Chuyển danh sách Category entity sang danh sách CategoryDTO
     */
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển Category entity sang CategoryMenuDTO (đệ quy cho children)
     */
    public CategoryMenuDTO toMenuDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryMenuDTO dto = CategoryMenuDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .slug(category.getSlug())
                .description(category.getDescription())
                .imageUrl(category.getImageUrl())
                .build();

        // Đệ quy chuyển đổi children
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            List<CategoryMenuDTO> childrenDTOs = category.getChildren().stream()
                    .filter(Category::getActive) // Chỉ lấy children active
                    .map(this::toMenuDTO)
                    .collect(Collectors.toList());
            dto.setChildren(childrenDTOs);
        }

        return dto;
    }

    /**
     * Chuyển danh sách Category entity sang danh sách CategoryMenuDTO
     */
    public List<CategoryMenuDTO> toMenuDTOList(List<Category> categories) {
        return categories.stream()
                .map(this::toMenuDTO)
                .collect(Collectors.toList());
    }

    // ============ DTO -> Entity ============

    /**
     * Chuyển CategoryCreateDTO sang Category entity (dùng cho create)
     */
    public Category toEntity(CategoryCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setImageUrl(dto.getImageUrl());
        category.setActive(dto.getActive() != null ? dto.getActive() : true);
        category.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        // parent sẽ được set trong service layer
        return category;
    }

    /**
     * Cập nhật Category entity từ CategoryUpdateDTO (dùng cho update)
     */
    public void updateEntity(Category category, CategoryUpdateDTO dto) {
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getSlug() != null) {
            category.setSlug(dto.getSlug());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        if (dto.getImageUrl() != null) {
            category.setImageUrl(dto.getImageUrl());
        }
        if (dto.getActive() != null) {
            category.setActive(dto.getActive());
        }
        if (dto.getSortOrder() != null) {
            category.setSortOrder(dto.getSortOrder());
        }
        // parent sẽ được update trong service layer
    }
}
