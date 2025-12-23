package com.flower.manager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity StockHistory - Lịch sử thay đổi tồn kho
 * Theo dõi mọi biến động tồn kho của sản phẩm
 */
@Entity
@Table(name = "stock_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Sản phẩm liên quan
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Số lượng thay đổi
     * Dương (+) = nhập hàng/hoàn hàng
     * Âm (-) = xuất hàng/bán hàng
     */
    @Column(name = "change_quantity", nullable = false)
    private Integer changeQuantity;

    /**
     * Số lượng tồn kho sau khi thay đổi
     */
    @Column(name = "final_quantity", nullable = false)
    private Integer finalQuantity;

    /**
     * Lý do thay đổi
     */
    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private StockChangeReason reason;

    /**
     * Ghi chú thêm (nếu có)
     */
    @Column(length = 500)
    private String note;

    /**
     * Mã đơn hàng liên quan (nếu có)
     */
    @Column(name = "order_code", length = 50)
    private String orderCode;

    /**
     * Người thực hiện thay đổi
     */
    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Enum các lý do thay đổi tồn kho
     */
    public enum StockChangeReason {
        ORDER_PLACED("Đặt hàng"), // Trừ khi đặt hàng
        ORDER_CANCELLED("Hủy đơn hàng"), // Hoàn khi hủy đơn
        IMPORT("Nhập hàng"), // Nhập hàng từ nhà cung cấp
        EXPORT("Xuất hàng"), // Xuất hàng (khác bán)
        ADMIN_ADJUST("Admin điều chỉnh"), // Admin chỉnh sửa thủ công
        RETURN("Khách trả hàng"), // Khách trả hàng
        DAMAGED("Hàng hư hỏng"), // Hàng bị hỏng
        INITIAL("Khởi tạo"); // Khởi tạo ban đầu

        private final String displayName;

        StockChangeReason(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
