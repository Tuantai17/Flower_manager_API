package com.flower.manager.controller.payment;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.order.OrderDTO;
import com.flower.manager.dto.payment.MomoPaymentResponse;
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
 * Controller xử lý các API thanh toán MoMo
 * Endpoint: /api/payment/momo/**
 */
@RestController
@RequestMapping("/api/payment/momo")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final MoMoService moMoService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    /**
     * Tạo URL thanh toán MoMo từ Order ID
     * POST /api/payment/momo/create?orderId={orderId}
     * 
     * @param orderId ID của đơn hàng cần thanh toán
     * @return MomoPaymentResponse chứa payUrl để redirect
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MomoPaymentResponse>> createPayment(
            @RequestParam Long orderId,
            @RequestParam(required = false) String momoType) {
        log.info("Creating MoMo payment for order: {}, type: {}", orderId, momoType);

        MomoPaymentResponse response = moMoService.createPaymentFromOrder(orderId, momoType);

        return ResponseEntity.ok(ApiResponse.success(response, "Tạo thanh toán MoMo thành công"));
    }

    /**
     * IPN (Instant Payment Notification) callback từ MoMo
     * MoMo sẽ gọi endpoint này khi thanh toán hoàn tất
     * POST /api/payment/momo/notify
     */
    @PostMapping("/notify")
    public ResponseEntity<String> momoNotify(@RequestBody Map<String, Object> data) {
        log.info("Received MoMo IPN callback: {}", data);

        try {
            // Verify signature
            if (!moMoService.verifyIpnSignature(data)) {
                log.warn("Invalid MoMo signature");
                return ResponseEntity.ok("INVALID_SIGNATURE");
            }

            String orderCode = (String) data.get("orderId");
            Integer resultCode = (Integer) data.get("resultCode");
            String transId = data.get("transId") != null ? data.get("transId").toString() : null;

            // Tìm order theo orderCode
            Order order = orderRepository.findByOrderCode(orderCode)
                    .orElse(null);

            if (order == null) {
                log.warn("Order not found: {}", orderCode);
                return ResponseEntity.ok("ORDER_NOT_FOUND");
            }

            if (resultCode == 0) {
                // Thanh toán thành công
                if (!order.getIsPaid()) {
                    orderService.confirmPayment(order.getId(), transId);
                    log.info("MoMo payment confirmed for order: {}", orderCode);
                }
            } else {
                // Thanh toán thất bại
                log.warn("MoMo payment failed for order: {} with resultCode: {}", orderCode, resultCode);
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("Error processing MoMo IPN: {}", e.getMessage(), e);
            return ResponseEntity.ok("ERROR");
        }
    }

    /**
     * Return URL sau khi thanh toán MoMo (Frontend redirect về)
     * GET /api/payment/momo/return
     */
    @GetMapping("/return")
    public ResponseEntity<ApiResponse<OrderDTO>> momoReturn(@RequestParam Map<String, String> params) {
        OrderDTO orderDTO = orderService.processMomoReturn(params);
        return ResponseEntity.ok(ApiResponse.success(orderDTO, "Thanh toán thành công"));
    }

    /**
     * Kiểm tra trạng thái thanh toán của đơn hàng
     * GET /api/payment/momo/status/{orderId}
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
                "paymentMethod", order.getPaymentMethod(),
                "paidAt", order.getPaidAt() != null ? order.getPaidAt().toString() : "");

        return ResponseEntity.ok(ApiResponse.success(status, "Lấy trạng thái thanh toán thành công"));
    }
}
