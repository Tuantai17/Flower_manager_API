package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO thống kê doanh thu
 * Sử dụng cho API: GET /api/admin/dashboard/revenue
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatsDTO {

    /**
     * Tổng doanh thu trong khoảng thời gian
     */
    private BigDecimal totalRevenue;

    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Giá trị trung bình mỗi đơn
     */
    private BigDecimal averageOrderValue;

    /**
     * Dữ liệu doanh thu theo từng ngày/tháng
     */
    private List<RevenueDataPoint> dataPoints;

    /**
     * Tăng trưởng so với kỳ trước (%)
     */
    private Double growthPercent;

    /**
     * Ngày bắt đầu thống kê
     */
    private LocalDate fromDate;

    /**
     * Ngày kết thúc thống kê
     */
    private LocalDate toDate;

    /**
     * Đơn vị thời gian: DAY, MONTH, YEAR
     */
    private String groupBy;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDataPoint {
        /**
         * Label cho trục X (VD: "2024-12-27", "Tháng 12", "2024")
         */
        private String label;

        /**
         * Doanh thu
         */
        private BigDecimal revenue;

        /**
         * Số đơn hàng
         */
        private Long orders;

        /**
         * Ngày (nếu group by day)
         */
        private LocalDate date;

        /**
         * Tháng (nếu group by month, format: 2024-12)
         */
        private String month;

        /**
         * Năm (nếu group by year)
         */
        private Integer year;
    }
}
