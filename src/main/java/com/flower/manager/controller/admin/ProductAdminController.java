package com.flower.manager.controller.admin;

import com.flower.manager.dto.ProductDTO;
import com.flower.manager.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller quản lý Product cho Admin
 * Hỗ trợ CRUD đầy đủ
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductAdminController {

    private final ProductService productService;

    // ============ CREATE ============

    /**
     * Tạo sản phẩm mới
     * POST /api/admin/products
     *
     * Body:
     * {
     * "name": "Hoa Hồng Đỏ",
     * "slug": "hoa-hong-do",
     * "description": "Hoa hồng đỏ tươi thắm",
     * "price": 150000,
     * "salePrice": 120000,
     * "thumbnail": "https://example.com/image.jpg",
     * "stockQuantity": 100,
     * "status": 1,
     * "active": true,
     * "categoryId": 1
     * }
     */
    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        ProductDTO created = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // ============ READ ============

    /**
     * Lấy tất cả sản phẩm
     * GET /api/admin/products
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    /**
     * Lấy sản phẩm theo ID
     * GET /api/admin/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Lấy sản phẩm theo slug
     * GET /api/admin/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    /**
     * Lấy sản phẩm theo danh mục
     * GET /api/admin/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getByCategory(categoryId));
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     * GET /api/admin/products/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchByName(keyword));
    }

    // ============ UPDATE ============

    /**
     * Cập nhật sản phẩm
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto) {
        ProductDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    // ============ DELETE ============

    /**
     * Xóa sản phẩm
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        productService.delete(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Xóa sản phẩm thành công");
        response.put("id", id.toString());

        return ResponseEntity.ok(response);
    }

    // ============ UTILITY ============

    /**
     * Kiểm tra slug đã tồn tại chưa
     * GET /api/admin/products/check-slug?slug=xxx&excludeId=1
     */
    @GetMapping("/check-slug")
    public ResponseEntity<Map<String, Boolean>> checkSlug(
            @RequestParam String slug,
            @RequestParam(required = false) Long excludeId) {
        boolean exists = productService.existsBySlug(slug, excludeId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    /**
     * Đếm số sản phẩm theo danh mục
     * GET /api/admin/products/count/category/{categoryId}
     */
    @GetMapping("/count/category/{categoryId}")
    public ResponseEntity<Map<String, Long>> countByCategory(@PathVariable Long categoryId) {
        long count = productService.countByCategory(categoryId);

        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
