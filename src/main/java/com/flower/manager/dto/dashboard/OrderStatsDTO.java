package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO thống kê đơn hàng
 * Sử dụng cho API: GET /api/admin/dashboard/orders
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatsDTO {

    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Số đơn hôm nay
     */
    private Long todayOrders;

    /**
     * Số đơn tuần này
     */
    private Long weekOrders;

    /**
     * Số đơn tháng này
     */
    private Long monthOrders;

    /**
     * Thống kê theo trạng thái
     */
    private Map<String, Long> ordersByStatus;

    /**
     * Số đơn PENDING cần xử lý ngay
     */
    private Long pendingCount;

    /**
     * Số đơn CONFIRMED
     */
    private Long confirmedCount;

    /**
     * Số đơn SHIPPING
     */
    private Long shippingCount;

    /**
     * Số đơn DELIVERED
     */
    private Long deliveredCount;

    /**
     * Số đơn COMPLETED
     */
    private Long completedCount;

    /**
     * Số đơn CANCELLED
     */
    private Long cancelledCount;

    /**
     * Tỷ lệ hoàn thành (%)
     */
    private Double completionRate;

    /**
     * Tỷ lệ hủy (%)
     */
    private Double cancellationRate;

    /**
     * Danh sách đơn hàng gần đây
     */
    private List<RecentOrderDTO> recentOrders;

    /**
     * Thống kê đơn hàng theo ngày (7 ngày gần nhất)
     */
    private List<DailyOrderCount> dailyOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyOrderCount {
        private String date;
        private Long count;
        private Long completed;
        private Long cancelled;
    }
}
