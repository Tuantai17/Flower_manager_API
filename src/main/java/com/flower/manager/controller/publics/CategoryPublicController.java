package com.flower.manager.controller.publics;

import com.flower.manager.dto.CategoryDTO;
import com.flower.manager.dto.CategoryMenuDTO;
import com.flower.manager.service.CategoryService;
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
public class CategoryPublicController {

    private final CategoryService service;

    /**
     * Lấy menu danh mục (đa cấp cha-con)
     * GET /api/categories/menu
     */
    @GetMapping("/menu")
    public ResponseEntity<List<CategoryMenuDTO>> getMenu() {
        return ResponseEntity.ok(service.getMenu());
    }

    /**
     * Lấy danh mục theo slug
     * GET /api/categories/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(service.getBySlug(slug));
    }

    /**
     * Lấy danh mục theo ID
     * GET /api/categories/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    /**
     * Lấy tất cả danh mục cha (cấp 1)
     * GET /api/categories/parents
     */
    @GetMapping("/parents")
    public ResponseEntity<List<CategoryDTO>> getAllParents() {
        return ResponseEntity.ok(service.getAllParentCategories());
    }

    /**
     * Lấy danh mục con theo ID cha
     * GET /api/categories/children/{parentId}
     */
    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<CategoryDTO>> getChildren(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getChildrenByParentId(parentId));
    }

    /**
     * Lấy menu theo ID danh mục cha
     * GET /api/categories/menu/{parentId}
     */
    @GetMapping("/menu/{parentId}")
    public ResponseEntity<CategoryMenuDTO> getMenuByParentId(@PathVariable Long parentId) {
        return ResponseEntity.ok(service.getMenuByParentId(parentId));
    }
}
