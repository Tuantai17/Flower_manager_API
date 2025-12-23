package com.flower.manager.service.order;

import com.flower.manager.dto.order.*;
import com.flower.manager.entity.Order;
import org.springframework.data.domain.Page;

/**
 * Service interface cho Order
 */
public interface OrderService {

    /**
     * Tạo đơn hàng từ giỏ hàng (Checkout)
     */
    OrderDTO checkout(CheckoutRequest request);

    /**
     * Lấy chi tiết đơn hàng theo ID
     */
    OrderDTO getOrderById(Long orderId);

    /**
     * Lấy chi tiết đơn hàng theo mã đơn
     */
    OrderDTO getOrderByCode(String orderCode);

    /**
     * Lấy danh sách đơn hàng của user hiện tại (có filter + pagination)
     */
    Page<OrderDTO> getMyOrders(OrderFilterRequest filter);

    /**
     * Lấy tất cả đơn hàng (Admin, có filter + pagination)
     */
    Page<OrderDTO> getAllOrders(OrderFilterRequest filter);

    /**
     * Cập nhật trạng thái đơn hàng (Admin)
     */
    OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequest request);

    /**
     * Hủy đơn hàng (User hoặc Admin)
     */
    OrderDTO cancelOrder(Long orderId, CancelOrderRequest request);

    /**
     * Xác nhận thanh toán thành công (callback từ cổng thanh toán)
     */
    OrderDTO confirmPayment(Long orderId, String transactionId);

    /**
     * Chuyển đổi Entity sang DTO
     */
    OrderDTO mapToDTO(Order order);
}
