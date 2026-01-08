package com.flower.manager.repository;

import com.flower.manager.entity.ContactTicket;
import com.flower.manager.entity.ContactTicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactTicketMessageRepository extends JpaRepository<ContactTicketMessage, Long> {

    /**
     * Lấy tin nhắn theo ticket, sắp xếp theo thời gian
     */
    List<ContactTicketMessage> findByTicketOrderByCreatedAtAsc(ContactTicket ticket);

    /**
     * Lấy tin nhắn theo ticket ID, sắp xếp theo thời gian
     */
    List<ContactTicketMessage> findByTicketIdOrderByCreatedAtAsc(Long ticketId);

    /**
     * Lấy tin nhắn theo ticket ID (alias)
     */
    @Query("SELECT m FROM ContactTicketMessage m WHERE m.ticket.id = :ticketId ORDER BY m.createdAt ASC")
    List<ContactTicketMessage> findByTicketId(@Param("ticketId") Long ticketId);

    /**
     * Đếm tin nhắn chưa đọc theo loại người gửi
     */
    @Query("SELECT COUNT(m) FROM ContactTicketMessage m WHERE m.ticket.id = :ticketId AND m.senderType = :senderType AND m.isRead = false")
    long countUnreadByTicketAndSenderType(@Param("ticketId") Long ticketId, @Param("senderType") String senderType);

    /**
     * Đánh dấu đã đọc tất cả tin nhắn từ một loại người gửi
     */
    @Modifying
    @Query("UPDATE ContactTicketMessage m SET m.isRead = true WHERE m.ticket.id = :ticketId AND m.senderType = :senderType AND m.isRead = false")
    int markAsReadByTicketAndSenderType(@Param("ticketId") Long ticketId, @Param("senderType") String senderType);

    /**
     * Lấy tin nhắn cuối cùng của ticket
     */
    @Query("SELECT m FROM ContactTicketMessage m WHERE m.ticket.id = :ticketId ORDER BY m.createdAt DESC LIMIT 1")
    ContactTicketMessage findLastMessageByTicketId(@Param("ticketId") Long ticketId);
}
