package com.flower.manager.dto.stock;

import com.flower.manager.entity.StockHistory;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO cho yêu cầu điều chỉnh tồn kho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockAdjustRequest {

    @NotNull(message = "Product ID không được để trống")
    private Long productId;

    /**
     * Số lượng thay đổi (dương = nhập, âm = xuất)
     */
    @NotNull(message = "Số lượng thay đổi không được để trống")
    private Integer changeQuantity;

    /**
     * Lý do thay đổi
     */
    @NotNull(message = "Lý do không được để trống")
    private StockHistory.StockChangeReason reason;

    /**
     * Ghi chú (tùy chọn)
     */
    private String note;
}
