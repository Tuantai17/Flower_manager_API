package com.flower.manager.dto.product;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO cho yêu cầu tìm kiếm sản phẩm nâng cao
 * Hỗ trợ filter theo: keyword, priceFrom, priceTo, categoryId
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchRequest {

    /**
     * Từ khóa tìm kiếm (tên sản phẩm)
     */
    private String keyword;

    /**
     * Giá tối thiểu
     */
    private BigDecimal priceFrom;

    /**
     * Giá tối đa
     */
    private BigDecimal priceTo;

    /**
     * ID danh mục (nếu null sẽ tìm tất cả)
     */
    private Long categoryId;

    /**
     * Sắp xếp theo: "price_asc", "price_desc", "newest", "best_selling"
     */
    @Builder.Default
    private String sortBy = "newest";
}
