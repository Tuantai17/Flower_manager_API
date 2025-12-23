package com.flower.manager.controller.stock;

import com.flower.manager.dto.stock.StockAdjustRequest;
import com.flower.manager.dto.stock.StockHistoryDTO;
import com.flower.manager.entity.StockHistory;
import com.flower.manager.service.stock.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý tồn kho (Admin only)
 */
@RestController
@RequestMapping("/api/admin/stock")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StockController {

    private final StockService stockService;

    /**
     * Điều chỉnh tồn kho (nhập/xuất/sửa)
     */
    @PostMapping("/adjust")
    public ResponseEntity<StockHistoryDTO> adjustStock(@Valid @RequestBody StockAdjustRequest request) {
        StockHistoryDTO result = stockService.adjustStock(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy số lượng tồn kho hiện tại
     */
    @GetMapping("/current/{productId}")
    public ResponseEntity<Map<String, Object>> getCurrentStock(@PathVariable Long productId) {
        int stock = stockService.getCurrentStock(productId);
        return ResponseEntity.ok(Map.of(
                "productId", productId,
                "currentStock", stock));
    }

    /**
     * Kiểm tra có đủ tồn kho không
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {
        boolean hasEnough = stockService.hasEnoughStock(productId, quantity);
        int currentStock = stockService.getCurrentStock(productId);
        return ResponseEntity.ok(Map.of(
                "productId", productId,
                "requestedQuantity", quantity,
                "currentStock", currentStock,
                "hasEnoughStock", hasEnough));
    }

    /**
     * Lấy lịch sử tồn kho theo sản phẩm
     */
    @GetMapping("/history/{productId}")
    public ResponseEntity<List<StockHistoryDTO>> getStockHistory(@PathVariable Long productId) {
        List<StockHistoryDTO> history = stockService.getStockHistory(productId);
        return ResponseEntity.ok(history);
    }

    /**
     * Lấy lịch sử tồn kho có phân trang
     */
    @GetMapping("/history/{productId}/paged")
    public ResponseEntity<Page<StockHistoryDTO>> getStockHistoryPaged(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StockHistoryDTO> history = stockService.getStockHistoryPaged(productId, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * Lấy danh sách các lý do thay đổi tồn kho
     */
    @GetMapping("/reasons")
    public ResponseEntity<List<Map<String, String>>> getStockChangeReasons() {
        List<Map<String, String>> reasons = java.util.Arrays.stream(StockHistory.StockChangeReason.values())
                .map(reason -> Map.of(
                        "code", reason.name(),
                        "displayName", reason.getDisplayName()))
                .toList();
        return ResponseEntity.ok(reasons);
    }
}
