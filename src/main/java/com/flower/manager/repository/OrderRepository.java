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

        // =============== DASHBOARD STATISTICS ===============

        /**
         * Đếm tổng số đơn hàng trong khoảng thời gian
         */
        @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt >= :fromDate AND o.createdAt <= :toDate")
        Long countOrdersInRange(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

        /**
         * Tính tổng doanh thu (COMPLETED + DELIVERED)
         */
        @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE " +
                        "o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED)")
        java.math.BigDecimal calculateTotalRevenue();

        /**
         * Tính doanh thu trong khoảng thời gian (COMPLETED + DELIVERED)
         */
        @Query("SELECT COALESCE(SUM(o.finalPrice), 0) FROM Order o WHERE " +
                        "o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED) AND "
                        +
                        "o.createdAt >= :fromDate AND o.createdAt <= :toDate")
        java.math.BigDecimal calculateRevenueInRange(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        /**
         * Đếm số đơn theo trạng thái trong khoảng thời gian
         */
        @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status AND o.createdAt >= :fromDate AND o.createdAt <= :toDate")
        Long countByStatusInRange(
                        @Param("status") OrderStatus status,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        /**
         * Lấy đơn hàng gần nhất (cho Dashboard)
         */
        @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items ORDER BY o.createdAt DESC LIMIT :limit")
        List<Order> findRecentOrders(@Param("limit") int limit);

        /**
         * Thống kê doanh thu theo ngày
         * Trả về: [date, revenue, orderCount]
         */
        @Query("SELECT CAST(o.createdAt AS LocalDate), COALESCE(SUM(o.finalPrice), 0), COUNT(o) FROM Order o " +
                        "WHERE o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED) "
                        +
                        "AND o.createdAt >= :fromDate AND o.createdAt <= :toDate " +
                        "GROUP BY CAST(o.createdAt AS LocalDate) " +
                        "ORDER BY CAST(o.createdAt AS LocalDate) ASC")
        List<Object[]> getRevenueByDay(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        /**
         * Thống kê doanh thu theo tháng
         * Trả về: [year, month, revenue, orderCount]
         */
        @Query("SELECT YEAR(o.createdAt), MONTH(o.createdAt), COALESCE(SUM(o.finalPrice), 0), COUNT(o) FROM Order o " +
                        "WHERE o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED) "
                        +
                        "AND o.createdAt >= :fromDate AND o.createdAt <= :toDate " +
                        "GROUP BY YEAR(o.createdAt), MONTH(o.createdAt) " +
                        "ORDER BY YEAR(o.createdAt) ASC, MONTH(o.createdAt) ASC")
        List<Object[]> getRevenueByMonth(
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        /**
         * Thống kê số đơn theo ngày (7 ngày gần nhất)
         * Trả về: [date, totalOrders, completedOrders, cancelledOrders]
         */
        @Query("SELECT CAST(o.createdAt AS LocalDate), COUNT(o), " +
                        "SUM(CASE WHEN o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED) THEN 1 ELSE 0 END), "
                        +
                        "SUM(CASE WHEN o.status = com.flower.manager.enums.OrderStatus.CANCELLED THEN 1 ELSE 0 END) " +
                        "FROM Order o " +
                        "WHERE o.createdAt >= :fromDate " +
                        "GROUP BY CAST(o.createdAt AS LocalDate) " +
                        "ORDER BY CAST(o.createdAt AS LocalDate) ASC")
        List<Object[]> getDailyOrderStats(@Param("fromDate") LocalDateTime fromDate);
}
