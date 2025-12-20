package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.ProductCreateDTO;
import com.flower.manager.dto.ProductDTO;
import com.flower.manager.dto.ProductUpdateDTO;
import com.flower.manager.service.ProductService;
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
 * Controller quản lý Product cho Admin
 * Hỗ trợ CRUD đầy đủ
 */
@RestController
@RequestMapping("/api/admin/products")
@PreAuthorize("hasRole('ADMIN')") // Thêm dòng này
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
    public ResponseEntity<ApiResponse<ProductDTO>> create(@Valid @RequestBody ProductCreateDTO dto) {
        ProductDTO created = productService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(created, "Tạo sản phẩm thành công"));
    }

    // ============ READ ============

    /**
     * Lấy tất cả sản phẩm
     * GET /api/admin/products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAll()));
    }

    /**
     * Lấy sản phẩm theo ID
     * GET /api/admin/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    /**
     * Lấy sản phẩm theo slug
     * GET /api/admin/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug)));
    }

    /**
     * Lấy sản phẩm theo danh mục
     * GET /api/admin/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategory(categoryId)));
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     * GET /api/admin/products/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(productService.searchByName(keyword)));
    }

    // ============ UPDATE ============

    /**
     * Cập nhật sản phẩm
     * PUT /api/admin/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO dto) {
        ProductDTO updated = productService.update(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Cập nhật sản phẩm thành công"));
    }

    // ============ DELETE ============

    /**
     * Xóa sản phẩm
     * DELETE /api/admin/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Xóa sản phẩm thành công"));
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
