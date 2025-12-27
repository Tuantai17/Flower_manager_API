package com.flower.manager.service.dashboard;

import com.flower.manager.dto.dashboard.*;

import java.time.LocalDate;

/**
 * Service interface cho Dashboard Admin
 * Cung cấp các phương thức thống kê cho quản trị viên
 */
public interface DashboardService {

    /**
     * Lấy tổng quan dashboard (overview)
     * Bao gồm: doanh thu, đơn hàng, sản phẩm, user
     */
    DashboardOverviewDTO getOverview();

    /**
     * Lấy thống kê doanh thu
     * 
     * @param fromDate Ngày bắt đầu
     * @param toDate   Ngày kết thúc
     * @param groupBy  Nhóm theo: DAY, MONTH, YEAR
     */
    RevenueStatsDTO getRevenueStats(LocalDate fromDate, LocalDate toDate, String groupBy);

    /**
     * Lấy thống kê đơn hàng
     */
    OrderStatsDTO getOrderStats();

    /**
     * Lấy thống kê tồn kho
     * 
     * @param lowStockThreshold Ngưỡng cảnh báo sắp hết hàng (mặc định 10)
     */
    StockStatsDTO getStockStats(Integer lowStockThreshold);

    // =============== CHART APIs ===============

    /**
     * Lấy dữ liệu biểu đồ doanh thu
     * Dữ liệu đã được xử lý sẵn để vẽ biểu đồ đường/vùng
     * 
     * @param period Khoảng thời gian: 7_DAYS, 30_DAYS, 3_MONTHS, 12_MONTHS
     * @return RevenueChartDataDTO với mảng dataPoints đầy đủ (không bị đứt)
     */
    RevenueChartDataDTO getRevenueChartData(String period);

    /**
     * Lấy dữ liệu phân bố đơn hàng theo trạng thái
     * Dữ liệu đã được xử lý sẵn để vẽ biểu đồ tròn
     * 
     * @return OrderDistributionDTO với segments và màu sắc
     */
    OrderDistributionDTO getOrderDistribution();

    /**
     * Lấy thống kê nhanh cho các Card
     * API nhẹ, phù hợp cho auto-refresh
     */
    DashboardStatsDTO getQuickStats();
}
