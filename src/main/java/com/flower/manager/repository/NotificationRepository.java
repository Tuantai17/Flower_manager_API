package com.flower.manager.repository;

import com.flower.manager.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

        /**
         * Lấy notifications cho admin (theo ID hoặc ALL_ADMINS)
         */
        @Query("SELECT n FROM Notification n WHERE " +
                        "(n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR " +
                        "n.recipientRole = 'ALL_ADMINS' " +
                        "ORDER BY n.createdAt DESC")
        Page<Notification> findForAdmin(@Param("adminId") Long adminId, Pageable pageable);

        /**
         * Lấy notifications chưa đọc cho admin
         */
        @Query("SELECT n FROM Notification n WHERE " +
                        "((n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR n.recipientRole = 'ALL_ADMINS') "
                        +
                        "AND n.isRead = false " +
                        "ORDER BY n.createdAt DESC")
        List<Notification> findUnreadForAdmin(@Param("adminId") Long adminId);

        /**
         * Đếm số notifications chưa đọc cho admin
         */
        @Query("SELECT COUNT(n) FROM Notification n WHERE " +
                        "((n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR n.recipientRole = 'ALL_ADMINS') "
                        +
                        "AND n.isRead = false")
        Long countUnreadForAdmin(@Param("adminId") Long adminId);

        /**
         * Lấy notifications cho user
         */
        @Query("SELECT n FROM Notification n WHERE " +
                        "n.recipientRole = 'USER' AND n.recipientId = :userId " +
                        "ORDER BY n.createdAt DESC")
        Page<Notification> findForUser(@Param("userId") Long userId, Pageable pageable);

        /**
         * Đếm số notifications chưa đọc cho user
         */
        @Query("SELECT COUNT(n) FROM Notification n WHERE " +
                        "n.recipientRole = 'USER' AND n.recipientId = :userId AND n.isRead = false")
        Long countUnreadForUser(@Param("userId") Long userId);

        /**
         * Đánh dấu đã đọc cho admin
         */
        @Modifying
        @Query("UPDATE Notification n SET n.isRead = true WHERE " +
                        "((n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR n.recipientRole = 'ALL_ADMINS') "
                        +
                        "AND n.isRead = false")
        void markAllReadForAdmin(@Param("adminId") Long adminId);

        /**
         * Đánh dấu đã đọc cho user
         */
        @Modifying
        @Query("UPDATE Notification n SET n.isRead = true WHERE " +
                        "n.recipientRole = 'USER' AND n.recipientId = :userId AND n.isRead = false")
        void markAllReadForUser(@Param("userId") Long userId);

        // ==================== DELETE METHODS ====================

        /**
         * Xóa 1 notification theo ID
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE n.id = :notificationId")
        void deleteNotification(@Param("notificationId") Long notificationId);

        /**
         * Xóa nhiều notifications theo danh sách ID
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE n.id IN :ids")
        void deleteNotifications(@Param("ids") List<Long> ids);

        /**
         * Xóa tất cả notifications cho admin (theo ID hoặc ALL_ADMINS)
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE " +
                        "(n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR " +
                        "n.recipientRole = 'ALL_ADMINS'")
        void deleteAllForAdmin(@Param("adminId") Long adminId);

        /**
         * Xóa tất cả notifications cho user
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE " +
                        "n.recipientRole = 'USER' AND n.recipientId = :userId")
        void deleteAllForUser(@Param("userId") Long userId);

        /**
         * Xóa các notifications đã đọc cho admin
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE " +
                        "((n.recipientRole = 'ADMIN' AND n.recipientId = :adminId) OR n.recipientRole = 'ALL_ADMINS') "
                        +
                        "AND n.isRead = true")
        void deleteReadForAdmin(@Param("adminId") Long adminId);

        /**
         * Xóa các notifications đã đọc cho user
         */
        @Modifying
        @Query("DELETE FROM Notification n WHERE " +
                        "n.recipientRole = 'USER' AND n.recipientId = :userId AND n.isRead = true")
        void deleteReadForUser(@Param("userId") Long userId);
}
