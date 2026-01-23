package com.flower.manager.service.ticket;

import com.flower.manager.dto.ticket.*;
import com.flower.manager.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Service interface cho Contact Ticket
 */
public interface ContactTicketService {

    // ==================== USER APIs ====================

    /**
     * Tạo ticket mới
     */
    TicketDTO createTicket(CreateTicketRequest request, Long userId);

    /**
     * Lấy danh sách ticket của user (theo userId hoặc email)
     */
    List<TicketDTO> getMyTickets(Long userId, String email);

    /**
     * Lấy chi tiết ticket (user)
     */
    TicketDTO getTicketDetails(Long ticketId, Long userId, String email);

    /**
     * User gửi tin nhắn mới vào ticket
     */
    TicketMessageDTO addUserMessage(Long ticketId, String content, List<String> images, Long userId, String senderName);

    // ==================== ADMIN APIs ====================

    /**
     * Lấy tất cả tickets với filter và phân trang
     */
    Page<TicketDTO> getAllTickets(String status, String category, String search, Pageable pageable);

    /**
     * Lấy chi tiết ticket cho admin
     */
    TicketDTO getTicketForAdmin(Long ticketId);

    /**
     * Cập nhật trạng thái ticket
     */
    TicketDTO updateTicketStatus(Long ticketId, TicketStatus status, Long adminId);

    /**
     * Admin phản hồi ticket
     */
    TicketMessageDTO addAdminReply(Long ticketId, AdminReplyRequest request, Long adminId, String adminName);

    /**
     * Giao ticket cho admin
     */
    TicketDTO assignTicket(Long ticketId, Long adminId);

    /**
     * Thống kê ticket
     */
    Map<String, Long> getTicketStats();

    /**
     * Đánh dấu đã đọc tin nhắn
     */
    void markMessagesAsRead(Long ticketId, String senderType);
}
