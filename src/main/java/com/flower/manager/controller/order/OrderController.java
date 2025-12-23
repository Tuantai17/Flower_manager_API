package com.flower.manager.controller.order;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.order.*;
import com.flower.manager.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API liên quan đến Đơn hàng
 * Endpoint: /api/orders/**
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService orderService;

    // ================= USER ENDPOINTS =================

    /**
     * Đặt hàng (Checkout)
     * POST /api/orders/checkout
     */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderDTO>> checkout(@Valid @RequestBody CheckoutRequest request) {
        log.info("Checkout request: {}", request.getCustomerName());
        OrderDTO order = orderService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(order, "Đặt hàng thành công"));
    }

    /**
     * Xem đơn hàng của tôi (có filter + pagination)
     * GET /api/orders/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getMyOrders(
            @ModelAttribute OrderFilterRequest filter) {
        log.info("Get my orders with filter: status={}, page={}", filter.getStatus(), filter.getPage());
        Page<OrderDTO> orders = orderService.getMyOrders(filter);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
    }

    /**
     * Xem chi tiết đơn hàng theo ID
     * GET /api/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        log.info("Get order by id: {}", id);
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * Xem chi tiết đơn hàng theo mã đơn
     * GET /api/orders/code/{orderCode}
     */
    @GetMapping("/code/{orderCode}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderByCode(@PathVariable String orderCode) {
        log.info("Get order by code: {}", orderCode);
        OrderDTO order = orderService.getOrderByCode(orderCode);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * Hủy đơn hàng (User)
     * POST /api/orders/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<OrderDTO>> cancelOrder(
            @PathVariable Long id,
            @Valid @RequestBody CancelOrderRequest request) {
        log.info("Cancel order: {} - Reason: {}", id, request.getReason());
        OrderDTO order = orderService.cancelOrder(id, request);
        return ResponseEntity.ok(ApiResponse.success(order, "Đã hủy đơn hàng thành công"));
    }
}
