package com.flower.manager.controller.publics;

import com.flower.manager.dto.ProductDTO;
import com.flower.manager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller công khai cho Product
 * Chỉ hỗ trợ các thao tác đọc (GET)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductPublicController {

    private final ProductService productService;

    /**
     * Lấy tất cả sản phẩm active
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(productService.getAllActive());
    }

    /**
     * Lấy sản phẩm theo ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    /**
     * Lấy sản phẩm theo slug
     * GET /api/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ProductDTO> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(productService.getBySlug(slug));
    }

    /**
     * Lấy sản phẩm theo ID danh mục
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(productService.getByCategory(categoryId));
    }

    /**
     * Lấy sản phẩm theo slug danh mục
     * GET /api/products/category/slug/{categorySlug}
     */
    @GetMapping("/category/slug/{categorySlug}")
    public ResponseEntity<List<ProductDTO>> getByCategorySlug(@PathVariable String categorySlug) {
        return ResponseEntity.ok(productService.getByCategorySlug(categorySlug));
    }

    /**
     * Tìm kiếm sản phẩm theo tên
     * GET /api/products/search?keyword=xxx
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(productService.searchByName(keyword));
    }

    /**
     * Lấy sản phẩm đang giảm giá
     * GET /api/products/on-sale
     */
    @GetMapping("/on-sale")
    public ResponseEntity<List<ProductDTO>> getOnSale() {
        return ResponseEntity.ok(productService.getOnSaleProducts());
    }

    /**
     * Lấy sản phẩm mới nhất
     * GET /api/products/latest?limit=10
     */
    @GetMapping("/latest")
    public ResponseEntity<List<ProductDTO>> getLatest(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getLatestProducts(limit));
    }
}
