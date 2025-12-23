package com.flower.manager.dto.stock;

import com.flower.manager.entity.StockHistory;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO cho lịch sử tồn kho
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockHistoryDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Integer changeQuantity;
    private Integer finalQuantity;
    private StockHistory.StockChangeReason reason;
    private String reasonDisplayName;
    private String note;
    private String orderCode;
    private String createdBy;
    private LocalDateTime createdAt;
}
