package com.flower.manager.service.order;

import com.flower.manager.dto.order.*;
import com.flower.manager.entity.*;
import com.flower.manager.enums.OrderStatus;
import com.flower.manager.enums.PaymentMethod;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.*;
import com.flower.manager.service.email.EmailService;
import com.flower.manager.service.payment.MoMoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của OrderService
 * Xử lý toàn bộ logic Checkout, quản lý đơn hàng, hủy đơn & hoàn stock
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final VoucherRepository voucherRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final MoMoService momoService;
    private final com.flower.manager.service.stock.StockService stockService;

    /**
     * Lấy user hiện tại từ Security Context
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("Vui lòng đăng nhập để thực hiện chức năng này");
        }
        String identifier = authentication.getName();
        return userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Override
    public OrderDTO checkout(CheckoutRequest request) {
        User user = getCurrentUser();
        log.info("Processing checkout for user: {}", user.getUsername());

        // 1. Lấy giỏ hàng
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BusinessException("CART_NOT_FOUND", "Giỏ hàng không tồn tại"));

        if (cart.getItems().isEmpty()) {
            throw new BusinessException("CART_EMPTY", "Giỏ hàng trống");
        }

        // 2. Kiểm tra tồn kho và tính tổng tiền
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();

            // Kiểm tra sản phẩm còn hoạt động
            if (!product.getActive()) {
                throw new BusinessException("PRODUCT_UNAVAILABLE",
                        "Sản phẩm '" + product.getName() + "' không còn bán");
            }

            // Kiểm tra tồn kho
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new BusinessException("INSUFFICIENT_STOCK",
                        "Sản phẩm '" + product.getName() + "' chỉ còn " + product.getStockQuantity() + " sản phẩm");
            }

            // Tính giá
            BigDecimal price = product.getCurrentPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalPrice = totalPrice.add(subtotal);

            // Tạo OrderItem
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .productThumbnail(product.getThumbnail())
                    .price(price)
                    .quantity(cartItem.getQuantity())
                    .subtotal(subtotal)
                    .build();
            orderItems.add(orderItem);
        }

        // 3. Áp dụng voucher (nếu có)
        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher voucher = null;

        if (request.getVoucherCode() != null && !request.getVoucherCode().isBlank()) {
            voucher = voucherRepository.findByCodeIgnoreCase(request.getVoucherCode().trim())
                    .orElseThrow(() -> new BusinessException("VOUCHER_NOT_FOUND", "Mã giảm giá không tồn tại"));

            if (!voucher.isValid()) {
                throw new BusinessException("VOUCHER_INVALID", "Mã giảm giá đã hết hạn hoặc hết lượt sử dụng");
            }

            if (voucher.getMinOrderValue() != null && totalPrice.compareTo(voucher.getMinOrderValue()) < 0) {
                throw new BusinessException("VOUCHER_MIN_ORDER",
                        "Đơn hàng phải từ " + voucher.getMinOrderValue() + "đ để áp dụng mã này");
            }

            discountAmount = voucher.calculateDiscount(totalPrice);
            voucher.use(); // Tăng usageCount
            voucherRepository.save(voucher);
            log.info("Applied voucher: {} - Discount: {}", voucher.getCode(), discountAmount);
        }

        // 4. Tính phí ship (có thể mở rộng logic ở đây)
        BigDecimal shippingFee = BigDecimal.ZERO; // Miễn phí ship tạm thời

        // 5. Tính giá cuối
        BigDecimal finalPrice = totalPrice.subtract(discountAmount).add(shippingFee);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // 6. Tạo đơn hàng
        Order order = Order.builder()
                .user(user)
                .customerName(request.getCustomerName())
                .customerEmail(request.getCustomerEmail() != null ? request.getCustomerEmail() : user.getEmail())
                .customerPhone(request.getCustomerPhone())
                .shippingAddress(request.getShippingAddress())
                .note(request.getNote())
                .totalPrice(totalPrice)
                .discountAmount(discountAmount)
                .shippingFee(shippingFee)
                .finalPrice(finalPrice)
                .voucher(voucher)
                .paymentMethod(request.getPaymentMethod())
                .status(OrderStatus.PENDING)
                .isPaid(false)
                .build();

        // Gắn OrderItems vào Order
        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }
        order.setItems(orderItems);

        // 7. Lưu đơn hàng trước để có orderCode
        Order savedOrder = orderRepository.save(order);
        log.info("Created order: {} for user: {}", savedOrder.getOrderCode(), user.getUsername());

        // 8. Trừ tồn kho (sau khi có orderCode)
        for (CartItem cartItem : cart.getItems()) {
            stockService.decreaseStock(cartItem.getProduct(), cartItem.getQuantity(), savedOrder.getOrderCode());
        }

        // 9. Xóa giỏ hàng
        cart.getItems().clear();
        cartRepository.save(cart);

        // 10. Gửi email xác nhận (async)
        try {
            sendOrderConfirmationEmail(savedOrder);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email: {}", e.getMessage());
        }

        // 11. Tạo URL thanh toán nếu là MoMo
        OrderDTO orderDTO = mapToDTO(savedOrder);
        if (request.getPaymentMethod() == PaymentMethod.MOMO) {
            try {
                String paymentUrl = momoService.createPaymentUrl(savedOrder.getId());
                orderDTO.setPaymentUrl(paymentUrl);
                log.info("Generated MoMo payment URL for order: {}", savedOrder.getOrderCode());
            } catch (Exception e) {
                log.error("Failed to create MoMo payment URL: {}", e.getMessage());
                // Vẫn trả về order, frontend có thể retry bằng API /api/payment/momo/{orderId}
            }
        }

        return orderDTO;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // Kiểm tra quyền truy cập
        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new BusinessException("ACCESS_DENIED", "Bạn không có quyền xem đơn hàng này");
        }

        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getOrderByCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn hàng: " + orderCode));

        User currentUser = getCurrentUser();
        if (!order.getUser().getId().equals(currentUser.getId())
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new BusinessException("ACCESS_DENIED", "Bạn không có quyền xem đơn hàng này");
        }

        return mapToDTO(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getMyOrders(OrderFilterRequest filter) {
        User user = getCurrentUser();
        Pageable pageable = createPageable(filter);

        Page<Order> orders = orderRepository.findByUserIdWithFilters(
                user.getId(),
                filter.getStatus(),
                filter.getFromDate(),
                filter.getToDate(),
                pageable);

        return orders.map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDTO> getAllOrders(OrderFilterRequest filter) {
        Pageable pageable = createPageable(filter);

        Page<Order> orders = orderRepository.findWithFilters(
                filter.getStatus(),
                filter.getFromDate(),
                filter.getToDate(),
                filter.getKeyword(),
                pageable);

        return orders.map(this::mapToDTO);
    }

    @Override
    public OrderDTO updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        OrderStatus oldStatus = order.getStatus();
        OrderStatus newStatus = request.getStatus();

        // Validate transition
        validateStatusTransition(oldStatus, newStatus);

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledReason(request.getReason());
            order.setCancelledAt(LocalDateTime.now());
            restoreStock(order);
        }

        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.COMPLETED) {
            if (!order.getIsPaid() && order.getPaymentMethod() == PaymentMethod.COD) {
                order.setIsPaid(true);
                order.setPaidAt(LocalDateTime.now());
            }
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Updated order {} status: {} -> {}", order.getOrderCode(), oldStatus, newStatus);

        return mapToDTO(savedOrder);
    }

    @Override
    public OrderDTO cancelOrder(Long orderId, CancelOrderRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        User currentUser = getCurrentUser();

        // Kiểm tra quyền hủy
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");
        boolean isOwner = order.getUser().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new BusinessException("ACCESS_DENIED", "Bạn không có quyền hủy đơn hàng này");
        }

        // Kiểm tra có thể hủy không
        if (!order.isCancellable()) {
            throw new BusinessException("CANNOT_CANCEL",
                    "Không thể hủy đơn hàng đang ở trạng thái: " + order.getStatus().getDisplayName());
        }

        // Hủy đơn
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledReason(request.getReason());
        order.setCancelledAt(LocalDateTime.now());

        // Hoàn lại tồn kho
        restoreStock(order);

        // Hoàn lại lượt dùng voucher (nếu có)
        if (order.getVoucher() != null) {
            Voucher voucher = order.getVoucher();
            voucher.setUsageCount(voucher.getUsageCount() - 1);
            voucherRepository.save(voucher);
            log.info("Restored voucher usage: {}", voucher.getCode());
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Cancelled order: {} by user: {}", order.getOrderCode(), currentUser.getUsername());

        return mapToDTO(savedOrder);
    }

    @Override
    public OrderDTO confirmPayment(Long orderId, String transactionId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        order.setIsPaid(true);
        order.setPaidAt(LocalDateTime.now());
        order.setTransactionId(transactionId);
        order.setStatus(OrderStatus.CONFIRMED);

        Order savedOrder = orderRepository.save(order);
        log.info("Confirmed payment for order: {} with transaction: {}", order.getOrderCode(), transactionId);

        // Gửi email thông báo thanh toán thành công
        try {
            sendPaymentConfirmationEmail(savedOrder);
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email: {}", e.getMessage());
        }

        return mapToDTO(savedOrder);
    }

    // ================= HELPER METHODS =================

    /**
     * Hoàn lại tồn kho khi hủy đơn
     */
    private void restoreStock(Order order) {
        for (OrderItem item : order.getItems()) {
            stockService.restoreStock(item.getProduct(), item.getQuantity(), order.getOrderCode());
        }
    }

    /**
     * Validate chuyển đổi trạng thái
     */
    private void validateStatusTransition(OrderStatus from, OrderStatus to) {
        // Không cho phép đổi từ CANCELLED/COMPLETED sang trạng thái khác
        if (from == OrderStatus.CANCELLED || from == OrderStatus.COMPLETED || from == OrderStatus.REFUNDED) {
            throw new BusinessException("INVALID_STATUS_TRANSITION",
                    "Không thể thay đổi trạng thái từ " + from.getDisplayName());
        }
    }

    /**
     * Tạo Pageable từ filter request
     */
    private Pageable createPageable(OrderFilterRequest filter) {
        Sort sort = Sort.by(
                filter.getSortDir().equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC,
                filter.getSortBy());
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }

    /**
     * Gửi email xác nhận đơn hàng
     */
    private void sendOrderConfirmationEmail(Order order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return;
        }

        StringBuilder content = new StringBuilder();
        content.append("Xin chào ").append(order.getCustomerName()).append(",\n\n");
        content.append("Đơn hàng của bạn đã được đặt thành công!\n\n");
        content.append("Mã đơn hàng: ").append(order.getOrderCode()).append("\n");
        content.append("Tổng tiền: ").append(order.getFinalPrice()).append(" VNĐ\n");
        content.append("Phương thức thanh toán: ").append(order.getPaymentMethod().getDisplayName()).append("\n");
        content.append("Địa chỉ giao hàng: ").append(order.getShippingAddress()).append("\n\n");
        content.append("Cảm ơn bạn đã mua sắm tại Flower Shop!");

        emailService.sendOrderEmail(order.getCustomerEmail(), content.toString());
    }

    /**
     * Gửi email xác nhận thanh toán
     */
    private void sendPaymentConfirmationEmail(Order order) {
        if (order.getCustomerEmail() == null || order.getCustomerEmail().isBlank()) {
            return;
        }

        String content = "Thanh toán đơn hàng " + order.getOrderCode() + " đã được xác nhận. " +
                "Số tiền: " + order.getFinalPrice() + " VNĐ. " +
                "Mã giao dịch: " + order.getTransactionId();

        emailService.sendOrderEmail(order.getCustomerEmail(), content);
    }

    @Override
    public OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .productSlug(item.getProduct().getSlug())
                        .productThumbnail(item.getProductThumbnail())
                        .price(item.getPrice())
                        .quantity(item.getQuantity())
                        .subtotal(item.getSubtotal())
                        .build())
                .toList();

        return OrderDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .customerEmail(order.getCustomerEmail())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .totalPrice(order.getTotalPrice())
                .discountAmount(order.getDiscountAmount())
                .shippingFee(order.getShippingFee())
                .finalPrice(order.getFinalPrice())
                .voucherCode(order.getVoucher() != null ? order.getVoucher().getCode() : null)
                .paymentMethod(order.getPaymentMethod())
                .isPaid(order.getIsPaid())
                .paidAt(order.getPaidAt())
                .status(order.getStatus())
                .statusDisplayName(order.getStatus().getDisplayName())
                .cancelledReason(order.getCancelledReason())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemDTOs)
                .totalQuantity(order.getTotalQuantity())
                .cancellable(order.isCancellable())
                .build();
    }
}
