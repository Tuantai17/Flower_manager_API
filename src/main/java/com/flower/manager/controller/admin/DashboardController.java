package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.dashboard.*;
import com.flower.manager.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * Controller Dashboard cho Admin
 * Cung cấp các API thống kê cho trang quản trị
 * 
 * Endpoint: /api/admin/dashboard/**
 * Yêu cầu: ROLE_ADMIN
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * Lấy tổng quan Dashboard
     * GET /api/admin/dashboard/overview
     * 
     * Response bao gồm:
     * - Doanh thu (hôm nay, tháng, năm, tổng, tăng trưởng)
     * - Đơn hàng (tổng, pending, confirmed, shipping, completed, cancelled)
     * - Sản phẩm (tổng, active, lowStock, outOfStock)
     * - Users (tổng, mới trong tháng)
     * - Đơn hàng gần đây (10 đơn)
     * - Sản phẩm bán chạy (10 sản phẩm)
     * - Sản phẩm sắp hết hàng
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<DashboardOverviewDTO>> getOverview() {
        log.info("[DASHBOARD:API] GET /overview");
        DashboardOverviewDTO overview = dashboardService.getOverview();
        return ResponseEntity.ok(ApiResponse.success(overview, "Lấy tổng quan dashboard thành công"));
    }

    /**
     * Lấy thống kê doanh thu
     * GET /api/admin/dashboard/revenue
     * 
     * Query params:
     * - fromDate (optional): Ngày bắt đầu, format: yyyy-MM-dd, default: 30 ngày
     * trước
     * - toDate (optional): Ngày kết thúc, format: yyyy-MM-dd, default: hôm nay
     * - groupBy (optional): DAY | MONTH | YEAR, default: DAY
     * 
     * Ví dụ:
     * GET
     * /api/admin/dashboard/revenue?fromDate=2024-01-01&toDate=2024-12-31&groupBy=MONTH
     */
    @GetMapping("/revenue")
    public ResponseEntity<ApiResponse<RevenueStatsDTO>> getRevenueStats(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @RequestParam(required = false, defaultValue = "DAY") String groupBy) {

        log.info("[DASHBOARD:API] GET /revenue?fromDate={}&toDate={}&groupBy={}", fromDate, toDate, groupBy);
        RevenueStatsDTO stats = dashboardService.getRevenueStats(fromDate, toDate, groupBy);
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê doanh thu thành công"));
    }

    /**
     * Lấy thống kê đơn hàng
     * GET /api/admin/dashboard/orders
     * 
     * Response bao gồm:
     * - Tổng số đơn, đơn hôm nay, tuần, tháng
     * - Số đơn theo từng trạng thái
     * - Tỷ lệ hoàn thành, tỷ lệ hủy
     * - Đơn hàng gần đây
     * - Thống kê đơn theo ngày (7 ngày gần nhất)
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<OrderStatsDTO>> getOrderStats() {
        log.info("[DASHBOARD:API] GET /orders");
        OrderStatsDTO stats = dashboardService.getOrderStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê đơn hàng thành công"));
    }

    /**
     * Lấy thống kê tồn kho
     * GET /api/admin/dashboard/stock
     * 
     * Query params:
     * - threshold (optional): Ngưỡng cảnh báo sắp hết hàng, default: 10
     * 
     * Response bao gồm:
     * - Tổng sản phẩm, active, inactive
     * - Số sản phẩm inStock, lowStock, outOfStock
     * - Tổng giá trị tồn kho
     * - Danh sách sản phẩm sắp hết hàng
     * - Danh sách sản phẩm hết hàng
     */
    @GetMapping("/stock")
    public ResponseEntity<ApiResponse<StockStatsDTO>> getStockStats(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {

        log.info("[DASHBOARD:API] GET /stock?threshold={}", threshold);
        StockStatsDTO stats = dashboardService.getStockStats(threshold);
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê tồn kho thành công"));
    }

    /**
     * API nhanh: Đếm số đơn PENDING
     * GET /api/admin/dashboard/pending-count
     * 
     * Dùng để hiển thị badge notification trên menu
     */
    @GetMapping("/pending-count")
    public ResponseEntity<ApiResponse<Long>> getPendingOrderCount() {
        log.info("[DASHBOARD:API] GET /pending-count");
        OrderStatsDTO stats = dashboardService.getOrderStats();
        return ResponseEntity.ok(ApiResponse.success(stats.getPendingCount(), "Số đơn hàng đang chờ xử lý"));
    }

    /**
     * API nhanh: Lấy sản phẩm cần bổ sung
     * GET /api/admin/dashboard/low-stock
     * 
     * Dùng để hiển thị cảnh báo nhanh
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<StockStatsDTO>> getLowStockProducts(
            @RequestParam(required = false, defaultValue = "10") Integer threshold) {

        log.info("[DASHBOARD:API] GET /low-stock?threshold={}", threshold);
        StockStatsDTO stats = dashboardService.getStockStats(threshold);
        return ResponseEntity.ok(ApiResponse.success(stats, "Danh sách sản phẩm cần bổ sung"));
    }

    // =============== CHART APIs ===============

    /**
     * Lấy dữ liệu biểu đồ doanh thu (Line/Area Chart)
     * GET /api/admin/dashboard/revenue-chart
     * 
     * Query params:
     * - period: 7_DAYS | 30_DAYS | 3_MONTHS | 12_MONTHS (default: 7_DAYS)
     * 
     * Response:
     * - totalRevenue: Tổng doanh thu trong kỳ
     * - totalOrders: Tổng số đơn
     * - growthPercent: Tăng trưởng so với kỳ trước
     * - dataPoints: Mảng [{label, date, revenue, orders}] để vẽ biểu đồ
     * 
     * Note: dataPoints đã được fill đủ các ngày (không bị đứt đoạn)
     */
    @GetMapping("/revenue-chart")
    public ResponseEntity<ApiResponse<RevenueChartDataDTO>> getRevenueChart(
            @RequestParam(required = false, defaultValue = "7_DAYS") String period) {

        log.info("[DASHBOARD:API] GET /revenue-chart?period={}", period);
        RevenueChartDataDTO chartData = dashboardService.getRevenueChartData(period);
        return ResponseEntity.ok(ApiResponse.success(chartData, "Lấy dữ liệu biểu đồ doanh thu thành công"));
    }

    /**
     * Lấy dữ liệu phân bố đơn hàng (Pie/Donut Chart)
     * GET /api/admin/dashboard/order-distribution
     * 
     * Response:
     * - totalOrders: Tổng số đơn
     * - segments: Mảng [{name, status, value, percentage, color}]
     * 
     * Mỗi segment đã có màu sắc định sẵn phù hợp với UI
     */
    @GetMapping("/order-distribution")
    public ResponseEntity<ApiResponse<OrderDistributionDTO>> getOrderDistribution() {
        log.info("[DASHBOARD:API] GET /order-distribution");
        OrderDistributionDTO distribution = dashboardService.getOrderDistribution();
        return ResponseEntity.ok(ApiResponse.success(distribution, "Lấy phân bố đơn hàng thành công"));
    }

    /**
     * Lấy thống kê nhanh cho Cards (nhẹ, phù hợp auto-refresh)
     * GET /api/admin/dashboard/quick-stats
     * 
     * Response nhẹ chỉ chứa các số liệu chính:
     * - Revenue: total, today, month, growth
     * - Orders: total, today, pending, growth
     * - Products: total, active, lowStock
     * - Categories: total
     * - Users: total, newThisMonth
     */
    @GetMapping("/quick-stats")
    public ResponseEntity<ApiResponse<DashboardStatsDTO>> getQuickStats() {
        log.info("[DASHBOARD:API] GET /quick-stats");
        DashboardStatsDTO stats = dashboardService.getQuickStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê nhanh thành công"));
    }
}
