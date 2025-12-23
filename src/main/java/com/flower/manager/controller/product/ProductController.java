package com.flower.manager.controller.product;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.product.ProductDTO;
import com.flower.manager.dto.product.ProductSearchRequest;
import com.flower.manager.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller công khai cho Product
 * Chỉ hỗ trợ các thao tác đọc (GET)
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * Lấy tất cả sản phẩm active
     * GET /api/products
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(productService.getAllActive()));
    }

    /**
     * Lấy sản phẩm theo ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(productService.getById(id)));
    }

    /**
     * Lấy sản phẩm theo slug
     * GET /api/products/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<ApiResponse<ProductDTO>> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBySlug(slug)));
    }

    /**
     * Lấy sản phẩm theo ID danh mục
     * GET /api/products/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategory(categoryId)));
    }

    /**
     * Lấy sản phẩm theo slug danh mục
     * GET /api/products/category/slug/{categorySlug}
     */
    @GetMapping("/category/slug/{categorySlug}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByCategorySlug(@PathVariable String categorySlug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategorySlug(categorySlug)));
    }

    /**
     * Tìm kiếm sản phẩm nâng cao với filter
     * GET /api/products/search
     * 
     * Query params:
     * - keyword: từ khóa tìm kiếm (tên sản phẩm)
     * - priceFrom: giá tối thiểu
     * - priceTo: giá tối đa
     * - categoryId: ID danh mục (nếu null tìm tất cả)
     * - sortBy: "newest" | "price_asc" | "price_desc" | "best_selling"
     * 
     * Ví dụ:
     * /api/products/search?keyword=hoa&priceFrom=100000&priceTo=500000&sortBy=price_asc
     * /api/products/search?categoryId=1&sortBy=best_selling
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal priceFrom,
            @RequestParam(required = false) BigDecimal priceTo,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "newest") String sortBy) {

        ProductSearchRequest request = ProductSearchRequest.builder()
                .keyword(keyword)
                .priceFrom(priceFrom)
                .priceTo(priceTo)
                .categoryId(categoryId)
                .sortBy(sortBy)
                .build();

        return ResponseEntity.ok(ApiResponse.success(productService.advancedSearch(request)));
    }

    /**
     * Lấy sản phẩm bán chạy nhất
     * GET /api/products/best-selling?limit=10
     * 
     * Dựa trên tổng số lượng bán trong các đơn hàng đã hoàn thành
     * (COMPLETED/DELIVERED)
     */
    @GetMapping("/best-selling")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getBestSelling(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(productService.getBestSellingProducts(limit)));
    }

    /**
     * Lấy sản phẩm đang giảm giá
     * GET /api/products/on-sale
     */
    @GetMapping("/on-sale")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getOnSale() {
        return ResponseEntity.ok(ApiResponse.success(productService.getOnSaleProducts()));
    }

    /**
     * Lấy sản phẩm mới nhất
     * GET /api/products/latest?limit=10
     */
    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getLatest(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success(productService.getLatestProducts(limit)));
    }

    // ============ Lấy sản phẩm theo danh mục cha ============

    /**
     * Lấy sản phẩm theo danh mục cha (bao gồm tất cả sản phẩm từ các danh mục con)
     * Ví dụ: GET /api/products/parent-category/1 -> lấy tất cả sản phẩm của "Hoa
     * tươi" (bao gồm "Hoa bó", "Hoa giỏ")
     * GET /api/products/parent-category/{parentCategoryId}
     */
    @GetMapping("/parent-category/{parentCategoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByParentCategory(@PathVariable Long parentCategoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByParentCategory(parentCategoryId)));
    }

    /**
     * Lấy sản phẩm theo slug danh mục cha
     * Ví dụ: GET /api/products/parent-category/slug/hoa-tuoi -> lấy tất cả sản phẩm
     * của "Hoa tươi"
     * GET /api/products/parent-category/slug/{parentCategorySlug}
     */
    @GetMapping("/parent-category/slug/{parentCategorySlug}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByParentCategorySlug(
            @PathVariable String parentCategorySlug) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByParentCategorySlug(parentCategorySlug)));
    }

    /**
     * Lấy sản phẩm theo danh mục - TỰ ĐỘNG phân biệt danh mục cha/con
     * - Nếu truyền ID danh mục cha -> trả về tất cả sản phẩm từ các danh mục con
     * - Nếu truyền ID danh mục con -> chỉ trả về sản phẩm của danh mục đó
     * 
     * Ví dụ:
     * GET /api/products/category-auto/1 -> "Hoa tươi" (cha) -> trả về tất cả sản
     * phẩm "Hoa bó" + "Hoa giỏ"
     * GET /api/products/category-auto/2 -> "Hoa bó" (con) -> chỉ trả về sản phẩm
     * "Hoa bó"
     * 
     * GET /api/products/category-auto/{categoryId}
     */
    @GetMapping("/category-auto/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getByCategoryAuto(@PathVariable Long categoryId) {
        return ResponseEntity.ok(ApiResponse.success(productService.getByCategoryIncludingChildren(categoryId)));
    }
}
