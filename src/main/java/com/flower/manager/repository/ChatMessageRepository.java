package com.flower.manager.repository;

import com.flower.manager.entity.ChatMessage;
import com.flower.manager.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find all messages in a session ordered by time
     */
    List<ChatMessage> findBySessionOrderBySentAtAsc(ChatSession session);

    /**
     * Find last N messages in a session
     */
    List<ChatMessage> findTop50BySessionOrderBySentAtDesc(ChatSession session);

    /**
     * Count unread messages in a session
     */
    long countBySessionAndIsReadFalseAndSenderTypeNot(ChatSession session, String senderType);

    /**
     * Mark all messages as read for a sender type
     */
    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.session = :session AND m.senderType != :senderType AND m.isRead = false")
    int markAsRead(@Param("session") ChatSession session, @Param("senderType") String senderType);

    /**
     * Find recent messages for context (for AI)
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.session = :session ORDER BY m.sentAt DESC LIMIT 10")
    List<ChatMessage> findRecentMessages(@Param("session") ChatSession session);

    /**
     * Delete all messages in a session
     */
    void deleteBySession(ChatSession session);
}
