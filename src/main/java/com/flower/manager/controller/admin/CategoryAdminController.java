package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.CategoryCreateDTO;
import com.flower.manager.dto.CategoryDTO;
import com.flower.manager.dto.CategoryMenuDTO;
import com.flower.manager.dto.CategoryUpdateDTO;
import com.flower.manager.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Category cho Admin
 * Hỗ trợ CRUD đầy đủ cho cấu trúc đa cấp cha-con
 */
@RestController
@RequestMapping("/api/admin/categories")
@PreAuthorize("hasRole('ADMIN')") // Thêm dòng này
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryAdminController {

    private final CategoryService service;

    // ============ CREATE ============

    /**
     * Tạo danh mục mới
     * POST /api/admin/categories
     * 
     * Body:
     * {
     * "name": "Hoa Hồng",
     * "slug": "hoa-hong",
     * "description": "Hoa hồng các loại",
     * "parentId": 1, // null nếu là danh mục cha
     * "active": true,
     * "sortOrder": 0
     * }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDTO>> create(@Valid @RequestBody CategoryCreateDTO dto) {
        CategoryDTO created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Tạo danh mục thành công"));
    }

    // ============ READ ============

    /**
     * Lấy tất cả danh mục
     * GET /api/admin/categories
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(service.getAll()));
    }

    /**
     * Lấy danh mục theo ID
     * GET /api/admin/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.getById(id)));
    }

    /**
     * Lấy tất cả danh mục cha (cấp 1)
     * GET /api/admin/categories/parents
     */
    @GetMapping("/parents")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getAllParents() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllParentCategories()));
    }

    /**
     * Lấy danh mục con theo ID cha
     * GET /api/admin/categories/children/{parentId}
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(service.getChildrenByParentId(parentId)));
    }

    /**
     * Lấy menu danh mục (cấu trúc cây cha-con)
     * GET /api/admin/categories/menu
     */
    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<CategoryMenuDTO>>> getMenu() {
        return ResponseEntity.ok(ApiResponse.success(service.getMenu()));
    }

    /**
     * Lấy menu theo ID danh mục cha
     * GET /api/admin/categories/menu/{parentId}
     */
    @GetMapping("/menu/{parentId}")
    public ResponseEntity<ApiResponse<CategoryMenuDTO>> getMenuByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(ApiResponse.success(service.getMenuByParentId(parentId)));
    }

    // ============ UPDATE ============

    /**
     * Cập nhật danh mục
     * PUT /api/admin/categories/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateDTO dto) {
        CategoryDTO updated = service.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật danh mục thành công"));
    }

    // ============ DELETE ============

    /**
     * Xóa danh mục
     * DELETE /api/admin/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa danh mục thành công"));
    }

    // ============ UTILITY ============

    /**
     * Kiểm tra slug đã tồn tại chưa
     * GET /api/admin/categories/check-slug?slug=xxx&excludeId=1
     */
    @GetMapping("/check-slug")
    public ResponseEntity<Map<String, Boolean>> checkSlug(
            @RequestParam String slug,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = service.existsBySlug(slug, excludeId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }
}
