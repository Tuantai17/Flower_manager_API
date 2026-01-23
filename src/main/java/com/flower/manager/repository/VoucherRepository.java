package com.flower.manager.repository;

import com.flower.manager.entity.Voucher;
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
 * Repository cho Voucher entity
 */
@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

        /**
         * Tìm voucher mã
         */
        Optional<Voucher> findByCode(String code);

        /**
         * Tìm voucher theo mã (case insensitive)
         */
        Optional<Voucher> findByCodeIgnoreCase(String code);

        /**
         * Tìm voucher active theo mã
         */
        Optional<Voucher> findByCodeAndIsActiveTrue(String code);

        /**
         * Kiểm tra mã voucher đã tồn tại chưa
         */
        boolean existsByCode(String code);

        /**
         * Lấy danh sách voucher đang hoạt động
         */
        List<Voucher> findByIsActiveTrue();

        /**
         * Lấy danh sách voucher còn hiệu lực
         */
        @Query("SELECT v FROM Voucher v WHERE v.isActive = true AND " +
                        "(v.startDate IS NULL OR v.startDate <= :now) AND " +
                        "(v.endDate IS NULL OR v.endDate >= :now) AND " +
                        "(v.usageLimit IS NULL OR v.usageCount < v.usageLimit)")
        List<Voucher> findValidVouchers(@Param("now") LocalDateTime now);

        /**
         * Tìm kiếm voucher với phân trang
         */
        @Query("SELECT v FROM Voucher v WHERE " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        "   LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "   LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Voucher> searchVouchers(@Param("keyword") String keyword, Pageable pageable);

        /**
         * Tìm kiếm voucher CÒN HIỆU LỰC cho Admin
         * - isActive = true
         * - Chưa hết hạn (endDate IS NULL OR endDate >= now)
         * - Chưa hết lượt sử dụng
         */
        @Query("SELECT v FROM Voucher v WHERE v.isActive = true AND " +
                        "(v.endDate IS NULL OR v.endDate >= :now) AND " +
                        "(v.usageLimit IS NULL OR v.usageCount < v.usageLimit) AND " +
                        "(:keyword IS NULL OR :keyword = '' OR " +
                        "   LOWER(v.code) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "   LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
        Page<Voucher> searchActiveVouchers(@Param("keyword") String keyword,
                        @Param("now") LocalDateTime now,
                        Pageable pageable);

        /**
         * Đếm số voucher còn hiệu lực
         */
        @Query("SELECT COUNT(v) FROM Voucher v WHERE v.isActive = true AND " +
                        "(v.endDate IS NULL OR v.endDate >= :now) AND " +
                        "(v.usageLimit IS NULL OR v.usageCount < v.usageLimit)")
        long countActiveVouchers(@Param("now") LocalDateTime now);

        /**
         * Đếm số voucher đã hết hạn hoặc bị ẩn
         */
        @Query("SELECT COUNT(v) FROM Voucher v WHERE " +
                        "v.isActive = false OR " +
                        "(v.endDate IS NOT NULL AND v.endDate < :now) OR " +
                        "(v.usageLimit IS NOT NULL AND v.usageCount >= v.usageLimit)")
        long countExpiredOrHiddenVouchers(@Param("now") LocalDateTime now);
}
