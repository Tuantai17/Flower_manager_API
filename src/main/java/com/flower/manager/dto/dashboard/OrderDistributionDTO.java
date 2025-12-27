package com.flower.manager.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO phân bố đơn hàng theo trạng thái (Pie Chart / Donut Chart)
 * Sử dụng cho API: GET /api/admin/dashboard/order-distribution
 * 
 * Frontend (Recharts PieChart) sử dụng trực tiếp mảng segments
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDistributionDTO {

    /**
     * Tổng số đơn hàng
     */
    private Long totalOrders;

    /**
     * Các phần của biểu đồ tròn
     */
    private List<Segment> segments;

    /**
     * Phần của biểu đồ tròn
     * Cấu trúc phù hợp với Recharts PieChart
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Segment {
        /**
         * Tên trạng thái (hiển thị trong legend)
         * VD: "Hoàn thành", "Đã hủy", "Đang xử lý"
         */
        private String name;

        /**
         * Mã trạng thái gốc (COMPLETED, CANCELLED, PENDING...)
         */
        private String status;

        /**
         * Số lượng đơn
         */
        private Long value;

        /**
         * Phần trăm (đã tính sẵn cho frontend)
         */
        private Double percentage;

        /**
         * Màu sắc cho segment (hex color)
         * VD: "#10B981" (xanh lá), "#EF4444" (đỏ)
         */
        private String color;
    }
}
