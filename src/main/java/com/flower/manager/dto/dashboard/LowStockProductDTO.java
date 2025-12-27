package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO sản phẩm sắp hết hàng / hết hàng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LowStockProductDTO {

    private Long id;
    private String name;
    private String slug;
    private String thumbnail;
    private BigDecimal price;

    /**
     * Số lượng tồn kho hiện tại
     */
    private Integer stockQuantity;

    /**
     * Tên danh mục
     */
    private String categoryName;

    /**
     * Trạng thái active
     */
    private Boolean active;

    /**
     * Mức độ cảnh báo: CRITICAL (0), WARNING (1-threshold), NORMAL (> threshold)
     */
    private String alertLevel;

    /**
     * Số ngày còn lại dựa trên tốc độ bán (ước tính)
     */
    private Integer estimatedDaysLeft;
}
