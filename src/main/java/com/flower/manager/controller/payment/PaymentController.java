package com.flower.manager.controller.payment;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.order.OrderDTO;
import com.flower.manager.dto.payment.MomoPaymentResponse;
import com.flower.manager.entity.Order;
import com.flower.manager.enums.ErrorCode;
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
 * 
 * Log convention:
 * - [PAYMENT:CREATE] - Tạo payment request
 * - [PAYMENT:IPN] - Xử lý IPN callback từ MoMo
 * - [PAYMENT:RETURN] - Xử lý redirect từ MoMo
 * - [PAYMENT:STATUS] - Kiểm tra trạng thái
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
        log.info("[PAYMENT:CREATE] Request received: orderId={}, type={}", orderId, momoType);

        MomoPaymentResponse response = moMoService.createPaymentFromOrder(orderId, momoType);

        log.info("[PAYMENT:CREATE] Payment URL created successfully: orderId={}", orderId);
        return ResponseEntity.ok(ApiResponse.success(response, "Tạo thanh toán MoMo thành công"));
    }

    /**
     * IPN (Instant Payment Notification) callback từ MoMo
     * MoMo sẽ gọi endpoint này khi thanh toán hoàn tất
     * POST /api/payment/momo/notify
     */
    @PostMapping("/notify")
    public ResponseEntity<String> momoNotify(@RequestBody Map<String, Object> data) {
        String orderCode = (String) data.get("orderId");
        Integer resultCode = data.get("resultCode") != null ? Integer.parseInt(data.get("resultCode").toString()) : -1;

        log.info("[PAYMENT:IPN] Received callback: orderCode={}, resultCode={}", orderCode, resultCode);
        log.debug("[PAYMENT:IPN] Full callback data: {}", data);

        try {
            // Verify signature
            if (!moMoService.verifyIpnSignature(data)) {
                log.error("[PAYMENT:IPN] Invalid signature: orderCode={}", orderCode);
                return ResponseEntity.ok("INVALID_SIGNATURE");
            }
            log.info("[PAYMENT:IPN] Signature verified: orderCode={}", orderCode);

            String transId = data.get("transId") != null ? data.get("transId").toString() : null;

            // Tìm order theo orderCode
            Order order = orderRepository.findByOrderCode(orderCode).orElse(null);

            if (order == null) {
                log.error("[PAYMENT:IPN] Order not found: orderCode={}", orderCode);
                return ResponseEntity.ok("ORDER_NOT_FOUND");
            }

            if (resultCode == 0) {
                // Thanh toán thành công
                if (!order.getIsPaid()) {
                    orderService.confirmPayment(order.getId(), transId);
                    log.info("[PAYMENT:IPN] Payment confirmed: orderCode={}, transId={}", orderCode, transId);
                } else {
                    log.info("[PAYMENT:IPN] Order already paid (duplicate IPN): orderCode={}", orderCode);
                }
            } else {
                // Thanh toán thất bại
                log.warn("[PAYMENT:IPN] Payment failed: orderCode={}, resultCode={}, message={}",
                        orderCode, resultCode, data.get("message"));
            }

            return ResponseEntity.ok("OK");

        } catch (Exception e) {
            log.error("[PAYMENT:IPN] Error processing callback: orderCode={}, error={}",
                    orderCode, e.getMessage(), e);
            return ResponseEntity.ok("ERROR");
        }
    }

    /**
     * Return URL sau khi thanh toán MoMo (Frontend redirect về)
     * GET /api/payment/momo/return
     */
    @GetMapping("/return")
    public ResponseEntity<ApiResponse<OrderDTO>> momoReturn(@RequestParam Map<String, String> params) {
        String orderCode = params.get("orderId");
        String resultCode = params.get("resultCode");

        log.info("[PAYMENT:RETURN] User redirected back: orderCode={}, resultCode={}", orderCode, resultCode);
        log.debug("[PAYMENT:RETURN] Full params: {}", params);

        OrderDTO orderDTO = orderService.processMomoReturn(params);

        if ("0".equals(resultCode)) {
            log.info("[PAYMENT:RETURN] Payment successful: orderCode={}", orderCode);
            return ResponseEntity.ok(ApiResponse.success(orderDTO, "Thanh toán thành công"));
        } else {
            log.warn("[PAYMENT:RETURN] Payment not completed: orderCode={}, resultCode={}", orderCode, resultCode);
            return ResponseEntity.ok(ApiResponse.success(orderDTO, "Chưa hoàn tất thanh toán"));
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán của đơn hàng
     * GET /api/payment/momo/status/{orderId}
     */
    @GetMapping("/status/{orderId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkPaymentStatus(@PathVariable Long orderId) {
        log.info("[PAYMENT:STATUS] Checking status: orderId={}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("[PAYMENT:STATUS] Order not found: orderId={}", orderId);
                    return new BusinessException(ErrorCode.ORDER_NOT_FOUND);
                });

        Map<String, Object> status = Map.of(
                "orderId", order.getId(),
                "orderCode", order.getOrderCode(),
                "isPaid", order.getIsPaid(),
                "status", order.getStatus(),
                "paymentMethod", order.getPaymentMethod(),
                "paidAt", order.getPaidAt() != null ? order.getPaidAt().toString() : "");

        log.info("[PAYMENT:STATUS] Status retrieved: orderCode={}, isPaid={}",
                order.getOrderCode(), order.getIsPaid());

        return ResponseEntity.ok(ApiResponse.success(status, "Lấy trạng thái thanh toán thành công"));
    }
}
