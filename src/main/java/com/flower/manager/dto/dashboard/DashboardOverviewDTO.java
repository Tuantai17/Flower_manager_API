package com.flower.manager.dto.dashboard;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO tổng hợp tất cả thống kê cho Dashboard Admin
 * Sử dụng cho API: GET /api/admin/dashboard/overview
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardOverviewDTO {

    // =============== REVENUE STATS ===============
    /**
     * Tổng doanh thu hôm nay
     */
    private BigDecimal todayRevenue;

    /**
     * Tổng doanh thu tháng này
     */
    private BigDecimal monthRevenue;

    /**
     * Tổng doanh thu năm nay
     */
    private BigDecimal yearRevenue;

    /**
     * Tổng doanh thu từ trước đến nay
     */
    private BigDecimal totalRevenue;

    /**
     * Tăng trưởng so với tháng trước (%)
     */
    private Double revenueGrowthPercent;

    // =============== ORDER STATS ===============
    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Số đơn hàng hôm nay
     */
    private Long todayOrders;

    /**
     * Số đơn PENDING (cần xử lý ngay)
     */
    private Long pendingOrders;

    /**
     * Số đơn CONFIRMED (đã xác nhận)
     */
    private Long confirmedOrders;

    /**
     * Số đơn SHIPPING (đang giao)
     */
    private Long shippingOrders;

    /**
     * Số đơn COMPLETED (hoàn thành)
     */
    private Long completedOrders;

    /**
     * Số đơn CANCELLED (đã hủy)
     */
    private Long cancelledOrders;

    // =============== PRODUCT STATS ===============
    /**
     * Tổng số sản phẩm
     */
    private Long totalProducts;

    /**
     * Số sản phẩm đang active
     */
    private Long activeProducts;

    /**
     * Số sản phẩm sắp hết hàng (stock <= threshold)
     */
    private Long lowStockProducts;

    /**
     * Số sản phẩm hết hàng (stock = 0)
     */
    private Long outOfStockProducts;

    // =============== USER STATS ===============
    /**
     * Tổng số người dùng
     */
    private Long totalUsers;

    /**
     * Số người dùng mới trong tháng
     */
    private Long newUsersThisMonth;

    // =============== CATEGORY STATS ===============
    /**
     * Tổng số danh mục
     */
    private Long totalCategories;

    // =============== RECENT ACTIVITY ===============
    /**
     * Đơn hàng gần đây (5-10 đơn mới nhất)
     */
    private List<RecentOrderDTO> recentOrders;

    /**
     * Sản phẩm bán chạy nhất
     */
    private List<TopProductDTO> topSellingProducts;

    /**
     * Sản phẩm sắp hết hàng
     */
    private List<LowStockProductDTO> lowStockProductList;
}
