package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO sản phẩm bán chạy (top selling)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopProductDTO {

    private Long id;
    private String name;
    private String slug;
    private String thumbnail;
    private BigDecimal price;
    private BigDecimal salePrice;

    /**
     * Tổng số lượng đã bán
     */
    private Long totalSold;

    /**
     * Tổng doanh thu từ sản phẩm này
     */
    private BigDecimal totalRevenue;

    /**
     * Số đơn hàng chứa sản phẩm này
     */
    private Long orderCount;

    /**
     * Số lượng tồn kho hiện tại
     */
    private Integer stockQuantity;

    /**
     * Tên danh mục
     */
    private String categoryName;
}
