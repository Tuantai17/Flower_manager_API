package com.flower.manager.service.stock;

import com.flower.manager.dto.stock.StockAdjustRequest;
import com.flower.manager.dto.stock.StockHistoryDTO;
import com.flower.manager.entity.Product;
import com.flower.manager.entity.StockHistory;
import com.flower.manager.entity.StockHistory.StockChangeReason;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.ProductRepository;
import com.flower.manager.repository.StockHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation của StockService
 * Quản lý tồn kho và lịch sử biến động
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockServiceImpl implements StockService {

    private final ProductRepository productRepository;
    private final StockHistoryRepository stockHistoryRepository;

    /**
     * Lấy tên người dùng hiện tại
     */
    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "SYSTEM";
    }

    @Override
    public StockHistoryDTO adjustStock(StockAdjustRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int newStock = currentStock + request.getChangeQuantity();

        // Kiểm tra không cho âm
        if (newStock < 0) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Không đủ tồn kho. Hiện có: " + currentStock + ", muốn xuất: "
                            + Math.abs(request.getChangeQuantity()));
        }

        // Cập nhật tồn kho
        product.setStockQuantity(newStock);
        productRepository.save(product);

        // Ghi lịch sử
        StockHistory history = StockHistory.builder()
                .product(product)
                .changeQuantity(request.getChangeQuantity())
                .finalQuantity(newStock)
                .reason(request.getReason())
                .note(request.getNote())
                .createdBy(getCurrentUsername())
                .build();
        StockHistory savedHistory = stockHistoryRepository.save(history);

        log.info("Stock adjusted for product {}: {} -> {} (change: {}, reason: {})",
                product.getName(), currentStock, newStock, request.getChangeQuantity(), request.getReason());

        return mapToDTO(savedHistory);
    }

    @Override
    public void decreaseStock(Product product, int quantity, String orderCode) {
        if (quantity <= 0) {
            throw new BusinessException("INVALID_QUANTITY", "Số lượng phải lớn hơn 0");
        }

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;

        if (currentStock < quantity) {
            throw new BusinessException("INSUFFICIENT_STOCK",
                    "Sản phẩm '" + product.getName() + "' chỉ còn " + currentStock + " sản phẩm");
        }

        int newStock = currentStock - quantity;
        product.setStockQuantity(newStock);
        productRepository.save(product);

        // Ghi lịch sử
        StockHistory history = StockHistory.builder()
                .product(product)
                .changeQuantity(-quantity)
                .finalQuantity(newStock)
                .reason(StockChangeReason.ORDER_PLACED)
                .orderCode(orderCode)
                .note("Trừ tồn kho khi đặt hàng")
                .createdBy(getCurrentUsername())
                .build();
        stockHistoryRepository.save(history);

        log.info("Decreased stock for product {}: {} -> {} (order: {})",
                product.getName(), currentStock, newStock, orderCode);
    }

    @Override
    public void restoreStock(Product product, int quantity, String orderCode) {
        if (quantity <= 0) {
            return; // Không cần hoàn nếu số lượng <= 0
        }

        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int newStock = currentStock + quantity;

        product.setStockQuantity(newStock);
        productRepository.save(product);

        // Ghi lịch sử
        StockHistory history = StockHistory.builder()
                .product(product)
                .changeQuantity(quantity)
                .finalQuantity(newStock)
                .reason(StockChangeReason.ORDER_CANCELLED)
                .orderCode(orderCode)
                .note("Hoàn lại tồn kho khi hủy đơn")
                .createdBy(getCurrentUsername())
                .build();
        stockHistoryRepository.save(history);

        log.info("Restored stock for product {}: {} -> {} (order: {})",
                product.getName(), currentStock, newStock, orderCode);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEnoughStock(Long productId, int requiredQuantity) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            return false;
        }
        int currentStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        return currentStock >= requiredQuantity;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCurrentStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return product.getStockQuantity() != null ? product.getStockQuantity() : 0;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StockHistoryDTO> getStockHistory(Long productId) {
        // Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        return stockHistoryRepository.findByProductId(productId).stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StockHistoryDTO> getStockHistoryPaged(Long productId, Pageable pageable) {
        // Kiểm tra product tồn tại
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product", "id", productId);
        }

        return stockHistoryRepository.findByProductId(productId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Map entity sang DTO
     */
    private StockHistoryDTO mapToDTO(StockHistory history) {
        return StockHistoryDTO.builder()
                .id(history.getId())
                .productId(history.getProduct().getId())
                .productName(history.getProduct().getName())
                .changeQuantity(history.getChangeQuantity())
                .finalQuantity(history.getFinalQuantity())
                .reason(history.getReason())
                .reasonDisplayName(history.getReason().getDisplayName())
                .note(history.getNote())
                .orderCode(history.getOrderCode())
                .createdBy(history.getCreatedBy())
                .createdAt(history.getCreatedAt())
                .build();
    }
}
