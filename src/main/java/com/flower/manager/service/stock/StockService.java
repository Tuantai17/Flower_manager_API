package com.flower.manager.service.stock;

import com.flower.manager.dto.stock.StockAdjustRequest;
import com.flower.manager.dto.stock.StockHistoryDTO;
import com.flower.manager.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface cho quản lý tồn kho
 */
public interface StockService {

    /**
     * Điều chỉnh tồn kho (nhập/xuất/sửa)
     */
    StockHistoryDTO adjustStock(StockAdjustRequest request);

    /**
     * Trừ tồn kho khi đặt hàng
     */
    void decreaseStock(Product product, int quantity, String orderCode);

    /**
     * Hoàn lại tồn kho khi hủy đơn
     */
    void restoreStock(Product product, int quantity, String orderCode);

    /**
     * Kiểm tra có đủ tồn kho không
     */
    boolean hasEnoughStock(Long productId, int requiredQuantity);

    /**
     * Lấy số lượng tồn kho hiện tại
     */
    int getCurrentStock(Long productId);

    /**
     * Lấy lịch sử tồn kho theo sản phẩm
     */
    List<StockHistoryDTO> getStockHistory(Long productId);

    /**
     * Lấy lịch sử tồn kho có phân trang
     */
    Page<StockHistoryDTO> getStockHistoryPaged(Long productId, Pageable pageable);
}
