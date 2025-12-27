package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO dữ liệu biểu đồ doanh thu (Line Chart / Area Chart)
 * Sử dụng cho API: GET /api/admin/dashboard/revenue-chart
 * 
 * Frontend (Recharts) sử dụng trực tiếp mảng dataPoints
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueChartDataDTO {

    /**
     * Khoảng thời gian: 7_DAYS, 30_DAYS, 3_MONTHS, 12_MONTHS
     */
    private String period;

    /**
     * Tổng doanh thu trong khoảng thời gian
     */
    private BigDecimal totalRevenue;

    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Tăng trưởng so với kỳ trước (%)
     */
    private Double growthPercent;

    /**
     * Dữ liệu từng điểm trên biểu đồ
     * Đây là mảng chính để Recharts vẽ
     */
    private List<DataPoint> dataPoints;

    /**
     * Điểm dữ liệu cho biểu đồ
     * Cấu trúc phù hợp với Recharts
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        /**
         * Nhãn hiển thị trên trục X (VD: "21-12", "22-12")
         */
        private String label;

        /**
         * Ngày (để frontend có thể xử lý thêm nếu cần)
         */
        private LocalDate date;

        /**
         * Doanh thu trong ngày/tuần/tháng
         */
        private BigDecimal revenue;

        /**
         * Số đơn hàng
         */
        private Long orders;
    }
}
