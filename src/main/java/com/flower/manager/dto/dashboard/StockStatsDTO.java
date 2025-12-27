package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO thống kê tồn kho
 * Sử dụng cho API: GET /api/admin/dashboard/stock
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockStatsDTO {

    /**
     * Tổng số sản phẩm
     */
    private Long totalProducts;

    /**
     * Số sản phẩm đang active
     */
    private Long activeProducts;

    /**
     * Số sản phẩm inactive
     */
    private Long inactiveProducts;

    /**
     * Số sản phẩm tồn kho đủ (stock > threshold)
     */
    private Long inStockProducts;

    /**
     * Số sản phẩm sắp hết hàng (0 < stock <= threshold)
     */
    private Long lowStockProducts;

    /**
     * Số sản phẩm hết hàng (stock = 0)
     */
    private Long outOfStockProducts;

    /**
     * Tổng giá trị tồn kho (price * quantity)
     */
    private BigDecimal totalStockValue;

    /**
     * Ngưỡng cảnh báo sắp hết hàng (mặc định thường là 10)
     */
    private Integer lowStockThreshold;

    /**
     * Danh sách sản phẩm sắp hết hàng
     */
    private List<LowStockProductDTO> lowStockProductList;

    /**
     * Danh sách sản phẩm hết hàng
     */
    private List<LowStockProductDTO> outOfStockProductList;
}
