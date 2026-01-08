package com.flower.manager.repository;

import com.flower.manager.entity.ChatSession;
import com.flower.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ChatSession entity
 */
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    /**
     * Find active session by user
     */
    @Query("SELECT s FROM ChatSession s WHERE s.user = :user AND s.status IN ('ACTIVE', 'WAITING_STAFF', 'WITH_STAFF') ORDER BY s.startedAt DESC")
    List<ChatSession> findActiveSessionsByUser(@Param("user") User user);

    /**
     * Find active session by guest ID
     */
    @Query("SELECT s FROM ChatSession s WHERE s.guestId = :guestId AND s.status IN ('ACTIVE', 'WAITING_STAFF', 'WITH_STAFF') ORDER BY s.startedAt DESC")
    List<ChatSession> findActiveSessionsByGuestId(@Param("guestId") String guestId);

    /**
     * Find most recent active session by user
     */
    Optional<ChatSession> findFirstByUserAndStatusInOrderByStartedAtDesc(User user, List<String> statuses);

    /**
     * Find most recent active session by guest ID
     */
    Optional<ChatSession> findFirstByGuestIdAndStatusInOrderByStartedAtDesc(String guestId, List<String> statuses);

    /**
     * Find all sessions waiting for staff (Admin dashboard)
     */
    List<ChatSession> findByStatusOrderByUpdatedAtDesc(String status);

    /**
     * Find all chat history by user
     */
    List<ChatSession> findByUserOrderByStartedAtDesc(User user);

    /**
     * Find sessions assigned to a staff member
     */
    List<ChatSession> findByStaffAndStatusOrderByUpdatedAtDesc(User staff, String status);

    /**
     * Count sessions waiting for staff
     */
    long countByStatus(String status);

    /**
     * Find sessions by status list, ordered by updatedAt desc
     */
    List<ChatSession> findByStatusInOrderByUpdatedAtDesc(List<String> statuses);

    /**
     * Find active sessions using native query (for debugging)
     */
    @Query(value = "SELECT * FROM chat_sessions WHERE status IN (:statuses) ORDER BY updated_at DESC", nativeQuery = true)
    List<ChatSession> findActiveSessionsNative(@Param("statuses") List<String> statuses);
}
