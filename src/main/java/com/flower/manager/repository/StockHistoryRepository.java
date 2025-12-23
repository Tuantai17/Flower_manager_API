package com.flower.manager.repository;

import com.flower.manager.entity.Product;
import com.flower.manager.entity.StockHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository cho StockHistory
 */
@Repository
public interface StockHistoryRepository extends JpaRepository<StockHistory, Long> {

    /**
     * Lấy lịch sử tồn kho theo sản phẩm (mới nhất trước)
     */
    List<StockHistory> findByProductOrderByCreatedAtDesc(Product product);

    /**
     * Lấy lịch sử tồn kho theo sản phẩm có phân trang
     */
    Page<StockHistory> findByProductOrderByCreatedAtDesc(Product product, Pageable pageable);

    /**
     * Lấy lịch sử theo product ID
     */
    @Query("SELECT sh FROM StockHistory sh WHERE sh.product.id = :productId ORDER BY sh.createdAt DESC")
    List<StockHistory> findByProductId(@Param("productId") Long productId);

    /**
     * Lấy lịch sử theo product ID có phân trang
     */
    @Query("SELECT sh FROM StockHistory sh WHERE sh.product.id = :productId ORDER BY sh.createdAt DESC")
    Page<StockHistory> findByProductId(@Param("productId") Long productId, Pageable pageable);

    /**
     * Lấy lịch sử theo mã đơn hàng
     */
    List<StockHistory> findByOrderCode(String orderCode);

    /**
     * Lấy lịch sử theo khoảng thời gian
     */
    @Query("SELECT sh FROM StockHistory sh WHERE sh.createdAt BETWEEN :from AND :to ORDER BY sh.createdAt DESC")
    List<StockHistory> findByDateRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * Lấy lịch sử theo lý do
     */
    List<StockHistory> findByReasonOrderByCreatedAtDesc(StockHistory.StockChangeReason reason);
}
