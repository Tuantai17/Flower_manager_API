package com.flower.manager.service.order;

import com.flower.manager.entity.Order;
import com.flower.manager.entity.OrderItem;
import com.flower.manager.entity.Product;
import com.flower.manager.entity.Voucher;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import com.flower.manager.repository.OrderRepository;
import com.flower.manager.repository.ProductRepository;
import com.flower.manager.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled Service de xu ly cac tac vu tu dong lien quan den Order
 * - Tu dong huy don hang PENDING (MoMo) qua han va hoan lai stock
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderScheduledService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;

    /**
     * Thoi gian timeout cho don hang MoMo (phut)
     * Mac dinh: 15 phut
     */
    @Value("${order.momo.timeout-minutes:15}")
    private int momoTimeoutMinutes;

    /**
     * Task chay moi 5 phut de kiem tra va huy cac don hang MoMo qua han
     */
    @Scheduled(cron = "0 0/5 * * * *")
    @Transactional
    public void cancelExpiredMomoOrders() {
        log.info("[Scheduled] Checking for expired MoMo orders...");

        LocalDateTime expiredBefore = LocalDateTime.now().minusMinutes(momoTimeoutMinutes);

        List<Order> expiredOrders = orderRepository.findExpiredPendingOrders(
                OrderStatus.PENDING,
                PaymentMethod.MOMO,
                expiredBefore);

        if (expiredOrders.isEmpty()) {
            log.info("[Scheduled] No expired MoMo orders found.");
            return;
        }

        log.info("[Scheduled] Found {} expired MoMo orders. Processing cancellation...", expiredOrders.size());

        for (Order order : expiredOrders) {
            try {
                cancelOrderAndRestoreStock(order);
                log.info("[Scheduled] Cancelled expired order: {} (created at: {})",
                        order.getOrderCode(), order.getCreatedAt());
            } catch (Exception e) {
                log.error("[Scheduled] Failed to cancel order {}: {}",
                        order.getOrderCode(), e.getMessage());
            }
        }

        log.info("[Scheduled] Finished processing {} expired orders.", expiredOrders.size());
    }

    /**
     * Huy don hang va hoan lai stock + voucher
     */
    private void cancelOrderAndRestoreStock(Order order) {
        // 1. Cap nhat trang thai don hang
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledReason(
                "Don hang tu dong huy do qua thoi gian thanh toan MoMo (" + momoTimeoutMinutes + " phut)");
        order.setCancelledAt(LocalDateTime.now());

        // 2. Hoan lai ton kho
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product != null) {
                int oldStock = product.getStockQuantity();
                product.setStockQuantity(oldStock + item.getQuantity());
                productRepository.save(product);
                log.debug("Restored stock for product '{}': {} -> {}",
                        product.getName(), oldStock, product.getStockQuantity());
            }
        }

        // 3. Hoan lai luot su dung voucher (neu co)
        if (order.getVoucher() != null) {
            Voucher voucher = order.getVoucher();
            if (voucher.getUsageCount() > 0) {
                voucher.setUsageCount(voucher.getUsageCount() - 1);
                voucherRepository.save(voucher);
                log.debug("Restored voucher usage for code: {}", voucher.getCode());
            }
        }

        // 4. Luu don hang da huy
        orderRepository.save(order);
    }
}
