package com.flower.manager.controller.order;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.order.OrderDTO;
import com.flower.manager.dto.order.OrderFilterRequest;
import com.flower.manager.dto.order.UpdateOrderStatusRequest;
import com.flower.manager.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller dành riêng cho các tính năng quản lý Đơn hàng của Admin
 * Endpoint: /api/admin/orders
 */
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class OrderAdminController {

    private final OrderService orderService;

    /**
     * Lấy danh sách tất cả đơn hàng (Admin)
     * GET /api/admin/orders
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<OrderDTO>>> getAllOrders(
            @ModelAttribute OrderFilterRequest filter) {
        log.info("Admin: Fetching orders with filter - Status: {}, Keyword: {}", filter.getStatus(),
                filter.getKeyword());
        Page<OrderDTO> orders = orderService.getAllOrders(filter);
        return ResponseEntity.ok(ApiResponse.success(orders, "Lấy danh sách đơn hàng thành công"));
    }

    /**
     * Lấy chi tiết đơn hàng (Admin)
     * GET /api/admin/orders/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(@PathVariable Long id) {
        log.info("Admin: Fetching order details for ID: {}", id);
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    /**
     * Cập nhật trạng thái đơn hàng (Admin)
     * PUT /api/admin/orders/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("Admin: Updating order {} status to {}", id, request.getStatus());
        OrderDTO order = orderService.updateOrderStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(order, "Cập nhật trạng thái đơn hàng thành công"));
    }
}
