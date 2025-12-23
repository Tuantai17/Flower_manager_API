package com.flower.manager.repository;

import com.flower.manager.entity.PasswordResetToken;
import com.flower.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository cho PasswordResetToken
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Tìm token theo giá trị token string
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Tìm token hợp lệ theo email
     * Token chưa dùng và chưa hết hạn
     */
    @Query("SELECT prt FROM PasswordResetToken prt " +
            "JOIN prt.user u " +
            "WHERE u.email = :email AND prt.used = false AND prt.expiryDate > :now")
    Optional<PasswordResetToken> findValidTokenByEmail(@Param("email") String email,
            @Param("now") LocalDateTime now);

    /**
     * Tìm tất cả token của user (để xóa khi reset thành công)
     */
    void deleteByUser(User user);

    /**
     * Xóa tất cả token đã hết hạn
     * Dùng cho scheduled job cleanup
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.expiryDate < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Xóa tất cả token đã sử dụng
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken prt WHERE prt.used = true")
    int deleteUsedTokens();

    /**
     * Kiểm tra token tồn tại và còn hợp lệ
     */
    @Query("SELECT CASE WHEN COUNT(prt) > 0 THEN true ELSE false END " +
            "FROM PasswordResetToken prt " +
            "WHERE prt.token = :token AND prt.used = false AND prt.expiryDate > :now")
    boolean isTokenValid(@Param("token") String token, @Param("now") LocalDateTime now);

    /**
     * Vô hiệu hóa tất cả token cũ của user (khi tạo token mới)
     */
    @Modifying
    @Query("UPDATE PasswordResetToken prt SET prt.used = true WHERE prt.user = :user AND prt.used = false")
    int invalidateOldTokens(@Param("user") User user);
}
