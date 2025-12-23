package com.flower.manager.repository;

import com.flower.manager.entity.Order;
import com.flower.manager.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Order entity
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

        /**
         * Tìm đơn hàng theo mã đơn
         */
        Optional<Order> findByOrderCode(String orderCode);

        /**
         * Lấy đơn hàng của user
         */
        List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

        /**
         * Lấy đơn hàng của user với phân trang
         */
        Page<Order> findByUserId(Long userId, Pageable pageable);

        /**
         * Lấy đơn hàng theo trạng thái
         */
        List<Order> findByStatus(OrderStatus status);

        /**
         * Đếm đơn hàng theo trạng thái
         */
        long countByStatus(OrderStatus status);

        /**
         * Tìm kiếm đơn hàng với filter (Admin)
         */
        @Query("SELECT o FROM Order o WHERE " +
                        "(:status IS NULL OR o.status = :status) AND " +
                        "(:fromDate IS NULL OR o.createdAt >= :fromDate) AND " +
                        "(:toDate IS NULL OR o.createdAt <= :toDate) AND " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        "   LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "   LOWER(o.customerName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "   o.customerPhone LIKE CONCAT('%', :keyword, '%'))")
        Page<Order> findWithFilters(
                        @Param("status") OrderStatus status,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        /**
         * Tìm kiếm đơn hàng của user với filter
         */
        @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND " +
                        "(:status IS NULL OR o.status = :status) AND " +
                        "(:fromDate IS NULL OR o.createdAt >= :fromDate) AND " +
                        "(:toDate IS NULL OR o.createdAt <= :toDate)")
        Page<Order> findByUserIdWithFilters(
                        @Param("userId") Long userId,
                        @Param("status") OrderStatus status,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate,
                        Pageable pageable);

        /**
         * Thống kê doanh thu theo ngày
         */
        @Query("SELECT SUM(o.finalPrice) FROM Order o WHERE " +
                        "o.status = 'COMPLETED' AND " +
                        "o.createdAt >= :fromDate AND o.createdAt <= :toDate")
        java.math.BigDecimal calculateRevenue(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        /**
         * Tìm các đơn hàng PENDING (chưa thanh toán) với phương thức MoMo
         * được tạo trước thời điểm expiredBefore
         */
        @Query("SELECT o FROM Order o WHERE " +
                        "o.status = :status AND " +
                        "o.isPaid = false AND " +
                        "o.paymentMethod = :paymentMethod AND " +
                        "o.createdAt < :expiredBefore")
        List<Order> findExpiredPendingOrders(
                        @Param("status") OrderStatus status,
                        @Param("paymentMethod") com.flower.manager.enums.PaymentMethod paymentMethod,
                        @Param("expiredBefore") LocalDateTime expiredBefore);
}
