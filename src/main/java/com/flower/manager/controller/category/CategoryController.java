package com.flower.manager.controller.category;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.category.CategoryDTO;
import com.flower.manager.dto.category.CategoryMenuDTO;
import com.flower.manager.service.category.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller công khai cho Category
 * Chỉ hỗ trợ các thao tác đọc (GET)
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {

    private final CategoryService service;

    /**
     * Lấy tất cả danh mục
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    /**
     * Lấy menu danh mục (đa cấp cha-con)
     * GET /api/categories/menu
     */
    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<CategoryMenuDTO>>> getMenu() {
        return ResponseEntity.ok(ApiResponse.success(service.getMenu()));
    }

    /**
     * Lấy danh mục theo slug
     * GET /api/categories/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(service.getBySlug(slug)));
    }

    /**
     * Lấy danh mục theo ID
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    /**
     * Lấy tất cả danh mục cha (cấp 1)
     * GET /api/categories/parents
     */
    @GetMapping("/parents")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllParents() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllParentCategories()));
    }

    /**
     * Lấy danh mục con theo ID cha
     * GET /api/categories/children/{parentId}
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(service.getChildrenByParentId(parentId)));
    }

    /**
     * Lấy menu theo ID danh mục cha
     * GET /api/categories/menu/{parentId}
     */
    @GetMapping("/menu/{parentId}")
    public ResponseEntity<ApiResponse<CategoryMenuDTO>> getMenuByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(service.getMenuByParentId(parentId)));
    }
}
