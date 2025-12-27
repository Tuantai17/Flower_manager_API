package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO thống kê tổng quan nhanh cho Dashboard
 * Sử dụng cho các Card thống kê trên cùng
 * 
 * Cấu trúc gọn nhẹ, phù hợp cho việc poll định kỳ (auto-refresh)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    // =============== REVENUE ===============
    /**
     * Tổng doanh thu (đã hoàn thành)
     */
    private BigDecimal totalRevenue;

    /**
     * Doanh thu hôm nay
     */
    private BigDecimal todayRevenue;

    /**
     * Doanh thu tháng này
     */
    private BigDecimal monthRevenue;

    /**
     * Tăng trưởng doanh thu so với tháng trước (%)
     */
    private Double revenueGrowth;

    // =============== ORDERS ===============
    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Số đơn hàng hôm nay
     */
    private Long todayOrders;

    /**
     * Số đơn đang chờ xử lý (PENDING)
     */
    private Long pendingOrders;

    /**
     * Tăng trưởng đơn hàng so với tháng trước (%)
     */
    private Double ordersGrowth;

    // =============== PRODUCTS ===============
    /**
     * Tổng số sản phẩm
     */
    private Long totalProducts;

    /**
     * Số sản phẩm active
     */
    private Long activeProducts;

    /**
     * Số sản phẩm sắp hết hàng
     */
    private Long lowStockCount;

    /**
     * Tăng trưởng sản phẩm so với tháng trước (%)
     */
    private Double productsGrowth;

    // =============== CATEGORIES ===============
    /**
     * Tổng số danh mục
     */
    private Long totalCategories;

    /**
     * Tăng trưởng danh mục (%)
     */
    private Double categoriesGrowth;

    // =============== USERS ===============
    /**
     * Tổng số người dùng
     */
    private Long totalUsers;

    /**
     * Số người dùng mới tháng này
     */
    private Long newUsersThisMonth;
}
