package com.flower.manager.repository;

import com.flower.manager.entity.SavedVoucher;
import com.flower.manager.entity.User;
import com.flower.manager.entity.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho SavedVoucher
 */
@Repository
public interface SavedVoucherRepository extends JpaRepository<SavedVoucher, Long> {

        /**
         * Tìm voucher đã lưu của user theo voucher ID
         */
        Optional<SavedVoucher> findByUserAndVoucher(User user, Voucher voucher);

        /**
         * Kiểm tra user đã lưu voucher này chưa
         */
        boolean existsByUserIdAndVoucherId(Long userId, Long voucherId);

        /**
         * Lấy tất cả voucher đã lưu của user
         */
        List<SavedVoucher> findByUserIdOrderBySavedAtDesc(Long userId);

        /**
         * Lấy voucher còn dùng được của user (chưa sử dụng + voucher còn hạn)
         */
        @Query("SELECT sv FROM SavedVoucher sv " +
                        "JOIN FETCH sv.voucher v " +
                        "WHERE sv.user.id = :userId " +
                        "AND sv.isAvailable = true " +
                        "AND v.isActive = true " +
                        "AND (v.endDate IS NULL OR v.endDate > CURRENT_TIMESTAMP) " +
                        "AND (v.usageLimit IS NULL OR v.usageCount < v.usageLimit) " +
                        "ORDER BY sv.savedAt DESC")
        List<SavedVoucher> findAvailableByUserId(@Param("userId") Long userId);

        /**
         * Lấy voucher đã sử dụng của user
         */
        @Query("SELECT sv FROM SavedVoucher sv " +
                        "JOIN FETCH sv.voucher " +
                        "WHERE sv.user.id = :userId " +
                        "AND sv.isAvailable = false " +
                        "ORDER BY sv.usedAt DESC")
        List<SavedVoucher> findUsedByUserId(@Param("userId") Long userId);

        /**
         * Lấy voucher sắp hết hạn của user (trong 3 ngày tới)
         */
        @Query(value = "SELECT sv.* FROM saved_vouchers sv " +
                        "INNER JOIN vouchers v ON sv.voucher_id = v.id " +
                        "WHERE sv.user_id = :userId " +
                        "AND sv.is_available = true " +
                        "AND v.is_active = true " +
                        "AND v.end_date IS NOT NULL " +
                        "AND v.end_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 3 DAY) " +
                        "ORDER BY v.end_date ASC", nativeQuery = true)
        List<SavedVoucher> findExpiringByUserId(@Param("userId") Long userId);

        /**
         * Lấy voucher đã hết hạn của user
         */
        @Query("SELECT sv FROM SavedVoucher sv " +
                        "JOIN FETCH sv.voucher v " +
                        "WHERE sv.user.id = :userId " +
                        "AND sv.isAvailable = true " +
                        "AND (v.endDate IS NOT NULL AND v.endDate < CURRENT_TIMESTAMP) " +
                        "ORDER BY v.endDate DESC")
        List<SavedVoucher> findExpiredByUserId(@Param("userId") Long userId);

        /**
         * Đếm số voucher còn dùng được của user
         */
        @Query("SELECT COUNT(sv) FROM SavedVoucher sv " +
                        "JOIN sv.voucher v " +
                        "WHERE sv.user.id = :userId " +
                        "AND sv.isAvailable = true " +
                        "AND v.isActive = true " +
                        "AND (v.endDate IS NULL OR v.endDate > CURRENT_TIMESTAMP)")
        long countAvailableByUserId(@Param("userId") Long userId);

        /**
         * Xóa voucher đã lưu
         */
        void deleteByUserIdAndVoucherId(Long userId, Long voucherId);
}
