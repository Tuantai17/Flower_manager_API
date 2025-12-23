package com.flower.manager.controller.payment;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.order.OrderDTO;
import com.flower.manager.entity.Order;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.repository.OrderRepository;
import com.flower.manager.service.order.OrderService;
import com.flower.manager.service.payment.MoMoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller xử lý các API thanh toán
 * Endpoint: /api/payment/**
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final MoMoService momoService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    /**
     * Tạo URL thanh toán MoMo
     * GET /api/payment/momo/{orderId}
     */
    @GetMapping("/momo/{orderId}")
    public ResponseEntity<ApiResponse<String>> createMoMoPayment(@PathVariable Long orderId) {
        log.info("Creating MoMo payment for order: {}", orderId);
        String payUrl = momoService.createPaymentUrl(orderId);
        return ResponseEntity.ok(ApiResponse.success(payUrl, "Tạo thanh toán MoMo thành công"));
    }

    /**
     * Callback từ MoMo (IPN - Instant Payment Notification)
     * POST /api/payment/momo-callback
     */
    @PostMapping("/momo-callback")
    public ResponseEntity<String> momoCallback(@RequestBody Map<String, Object> data) {
        log.info("Received MoMo callback: {}", data);

        try {
            int resultCode = Integer.parseInt(data.get("resultCode").toString());
            String orderId = data.get("orderId").toString();
            String transId = data.get("transId") != null ? data.get("transId").toString() : null;

            // Tìm order theo orderCode
            Order order = orderRepository.findByOrderCode(orderId)
                    .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Đơn hàng không tồn tại"));

            if (resultCode == 0) {
                // Thanh toán thành công
                orderService.confirmPayment(order.getId(), transId);
                log.info("MoMo payment successful for order: {}", orderId);
            } else {
                // Thanh toán thất bại
                log.warn("MoMo payment failed for order: {} with resultCode: {}", orderId, resultCode);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Error processing MoMo callback: {}", e.getMessage());
            return ResponseEntity.ok("OK"); // Vẫn trả về OK để MoMo không gửi lại
        }
    }

    /**
     * Return URL sau khi thanh toán MoMo (Frontend redirect)
     * GET /api/payment/momo-return
     */
    @GetMapping("/momo-return")
    public ResponseEntity<ApiResponse<OrderDTO>> momoReturn(
            @RequestParam Map<String, String> params) {
        log.info("MoMo return with params: {}", params);

        String orderId = params.get("orderId");
        int resultCode = Integer.parseInt(params.getOrDefault("resultCode", "-1"));

        Order order = orderRepository.findByOrderCode(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Đơn hàng không tồn tại"));

        if (resultCode == 0) {
            // Nếu callback chưa xử lý, xử lý ở đây
            if (!order.getIsPaid()) {
                String transId = params.get("transId");
                orderService.confirmPayment(order.getId(), transId);
            }
            return ResponseEntity.ok(ApiResponse.success(
                    orderService.mapToDTO(order), "Thanh toán thành công"));
        } else {
            return ResponseEntity.ok(ApiResponse.error(400, "Thanh toán thất bại"));
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán
     * GET /api/payment/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPaymentStatus(@PathVariable Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Đơn hàng không tồn tại"));

        Map<String, Object> status = Map.of(
                "orderId", order.getId(),
                "orderCode", order.getOrderCode(),
                "isPaid", order.getIsPaid(),
                "status", order.getStatus(),
                "paymentMethod", order.getPaymentMethod());

        return ResponseEntity.ok(ApiResponse.success(status));
    }
}
