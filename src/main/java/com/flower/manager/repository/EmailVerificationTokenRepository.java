package com.flower.manager.repository;

import com.flower.manager.entity.EmailVerificationToken;
import com.flower.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository cho EmailVerificationToken entity
 */
@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    /**
     * Tìm token theo giá trị token
     */
    Optional<EmailVerificationToken> findByToken(String token);

    /**
     * Tìm token theo user
     */
    Optional<EmailVerificationToken> findByUser(User user);

    /**
     * Tìm token chưa sử dụng của user
     */
    @Query("SELECT t FROM EmailVerificationToken t WHERE t.user = :user AND t.confirmedAt IS NULL AND t.expiresAt > :now")
    Optional<EmailVerificationToken> findValidTokenByUser(@Param("user") User user, @Param("now") LocalDateTime now);

    /**
     * Xóa tất cả token của user (dùng khi cần gửi lại email)
     */
    @Modifying
    @Transactional
    void deleteByUser(User user);

    /**
     * Xóa các token đã hết hạn
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Kiểm tra token có tồn tại không
     */
    boolean existsByToken(String token);
}
