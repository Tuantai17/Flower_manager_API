package com.flower.manager.service.ticket;

import com.flower.manager.dto.ticket.*;
import com.flower.manager.entity.ContactTicket;
import com.flower.manager.entity.ContactTicketMessage;
import com.flower.manager.entity.User;
import com.flower.manager.enums.TicketCategory;
import com.flower.manager.enums.TicketStatus;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.ContactTicketMessageRepository;
import com.flower.manager.repository.ContactTicketRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.notification.TicketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ContactTicketServiceImpl implements ContactTicketService {

    private final ContactTicketRepository ticketRepository;
    private final ContactTicketMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TicketNotificationService notificationService;

    // ==================== USER APIs ====================

    @Override
    public TicketDTO createTicket(CreateTicketRequest request, Long userId) {
        log.info("Creating ticket for user: {}, email: {}", userId, request.getEmail());

        User user = null;
        if (userId != null) {
            user = userRepository.findById(userId).orElse(null);
        }

        // Parse category
        TicketCategory category = parseCategory(request.getCategory());

        // Tạo subject từ category nếu không có
        String subject = request.getSubject();
        if (subject == null || subject.trim().isEmpty()) {
            subject = category.getDisplayName();
        }

        // Tạo ticket
        ContactTicket ticket = ContactTicket.builder()
                .user(user)
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .subject(subject)
                .category(category)
                .status(TicketStatus.OPEN)
                .priority(3)
                .build();

        ticket = ticketRepository.save(ticket);

        // Tạo tin nhắn đầu tiên
        ContactTicketMessage message = ContactTicketMessage.builder()
                .ticket(ticket)
                .senderType("USER")
                .senderId(userId)
                .senderName(request.getName())
                .content(request.getMessage())
                .isRead(false)
                .build();

        messageRepository.save(message);
        ticket.getMessages().add(message);

        log.info("Created ticket: {} with code: {}", ticket.getId(), ticket.getTicketCode());

        // Realtime: Thông báo cho admin có ticket mới
        notificationService.notifyAdminNewTicket(ticket.getId(), ticket.getTicketCode(),
                request.getName(), subject);

        return toDTO(ticket, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDTO> getMyTickets(Long userId, String email) {
        log.info("Getting tickets for userId: {}, email: {}", userId, email);

        List<ContactTicket> tickets = new ArrayList<>();

        if (userId != null && email != null) {
            tickets = ticketRepository.findByEmailOrUserId(email, userId);
        } else if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            if (user != null) {
                tickets = ticketRepository.findByUserOrderByCreatedAtDesc(user);
            }
        } else if (email != null) {
            tickets = ticketRepository.findByEmailOrderByCreatedAtDesc(email);
        }

        log.info("Found {} tickets", tickets.size());
        return tickets.stream()
                .map(t -> toDTO(t, false))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO getTicketDetails(Long ticketId, Long userId, String email) {
        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));

        // Verify ownership
        boolean isOwner = false;
        if (userId != null && ticket.getUser() != null && ticket.getUser().getId().equals(userId)) {
            isOwner = true;
        }
        if (email != null && email.equalsIgnoreCase(ticket.getEmail())) {
            isOwner = true;
        }

        if (!isOwner) {
            throw new BusinessException("Bạn không có quyền xem ticket này");
        }

        return toDTO(ticket, true);
    }

    @Override
    public TicketMessageDTO addUserMessage(Long ticketId, String content, Long userId, String senderName) {
        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));

        if (ticket.getStatus() == TicketStatus.CLOSED) {
            throw new BusinessException("Ticket đã đóng, không thể gửi tin nhắn");
        }

        ContactTicketMessage message = ContactTicketMessage.builder()
                .ticket(ticket)
                .senderType("USER")
                .senderId(userId)
                .senderName(senderName != null ? senderName : ticket.getName())
                .content(content)
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Update ticket status nếu đang RESOLVED thì chuyển về IN_PROGRESS
        if (ticket.getStatus() == TicketStatus.RESOLVED) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        TicketMessageDTO messageDTO = toMessageDTO(message);

        // Realtime: Broadcast message đến ticket channel
        notificationService.broadcastNewMessage(ticketId, messageDTO);

        // Realtime: Thông báo cho admin có tin nhắn mới
        notificationService.notifyAdminNewMessage(ticketId, ticket.getTicketCode(),
                senderName != null ? senderName : ticket.getName());

        return messageDTO;
    }

    // ==================== ADMIN APIs ====================

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDTO> getAllTickets(String status, String category, String search, Pageable pageable) {
        log.info("getAllTickets called - status: {}, category: {}, search: {}", status, category, search);

        TicketStatus ticketStatus = parseStatus(status);
        TicketCategory ticketCategory = parseCategory(category);
        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        log.info("Parsed values - ticketStatus: {}, ticketCategory: {}, searchTerm: {}",
                ticketStatus, ticketCategory, searchTerm);

        Page<ContactTicket> ticketsPage = ticketRepository.findAllWithFilters(ticketStatus, ticketCategory, searchTerm,
                pageable);
        log.info("Found {} tickets, total elements: {}", ticketsPage.getContent().size(),
                ticketsPage.getTotalElements());

        return ticketsPage.map(t -> toDTO(t, false));
    }

    @Override
    @Transactional(readOnly = true)
    public TicketDTO getTicketForAdmin(Long ticketId) {
        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));
        return toDTO(ticket, true);
    }

    @Override
    public TicketDTO updateTicketStatus(Long ticketId, TicketStatus status, Long adminId) {
        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));

        TicketStatus oldStatus = ticket.getStatus();
        ticket.setStatus(status);

        // Tạo system message
        String statusMessage = String.format("Trạng thái đã thay đổi từ \"%s\" sang \"%s\"",
                oldStatus.getDisplayName(), status.getDisplayName());

        ContactTicketMessage systemMsg = ContactTicketMessage.builder()
                .ticket(ticket)
                .senderType("SYSTEM")
                .senderId(adminId)
                .senderName("Hệ thống")
                .content(statusMessage)
                .isRead(true)
                .build();

        messageRepository.save(systemMsg);
        ticket = ticketRepository.save(ticket);

        // Realtime: Broadcast status change đến ticket channel
        notificationService.broadcastStatusChange(ticketId, status.name(), status.getDisplayName());

        // Realtime: Thông báo cho user về thay đổi trạng thái
        Long userId = ticket.getUser() != null ? ticket.getUser().getId() : null;
        notificationService.notifyUserStatusChange(userId, ticketId, ticket.getTicketCode(), status.getDisplayName());

        return toDTO(ticket, true);
    }

    @Override
    public TicketMessageDTO addAdminReply(Long ticketId, AdminReplyRequest request, Long adminId, String adminName) {
        log.info("Admin reply - ticketId: {}, adminId: {}, content length: {}",
                ticketId, adminId, request.getContent() != null ? request.getContent().length() : 0);

        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));

        // Tạo tin nhắn phản hồi
        ContactTicketMessage message = ContactTicketMessage.builder()
                .ticket(ticket)
                .senderType("ADMIN")
                .senderId(adminId)
                .senderName(adminName != null ? adminName : "Admin")
                .content(request.getContent())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Cập nhật status nếu có
        if (request.getNewStatus() != null && !request.getNewStatus().isEmpty()) {
            TicketStatus newStatus = parseStatus(request.getNewStatus());
            if (newStatus != null && ticket.getStatus() != newStatus) {
                ticket.setStatus(newStatus);
                ticketRepository.save(ticket);
            }
        } else if (ticket.getStatus() == TicketStatus.OPEN) {
            // Auto chuyển sang IN_PROGRESS khi admin phản hồi lần đầu
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
        }

        TicketMessageDTO messageDTO = toMessageDTO(message);

        // Realtime: Broadcast message đến ticket channel
        notificationService.broadcastNewMessage(ticketId, messageDTO);

        // Realtime: Thông báo cho user về phản hồi từ admin
        Long userId = ticket.getUser() != null ? ticket.getUser().getId() : null;
        notificationService.notifyUserAdminReply(userId, ticketId, ticket.getTicketCode(),
                adminName != null ? adminName : "Admin");

        return messageDTO;
    }

    @Override
    public TicketDTO assignTicket(Long ticketId, Long adminId) {
        ContactTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ticket"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy admin"));

        ticket.setAssignedAdmin(admin);
        ticket = ticketRepository.save(ticket);

        return toDTO(ticket, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getTicketStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("open", ticketRepository.countByStatus(TicketStatus.OPEN));
        stats.put("inProgress", ticketRepository.countByStatus(TicketStatus.IN_PROGRESS));
        stats.put("resolved", ticketRepository.countByStatus(TicketStatus.RESOLVED));
        stats.put("closed", ticketRepository.countByStatus(TicketStatus.CLOSED));
        stats.put("total", ticketRepository.count());
        return stats;
    }

    @Override
    public void markMessagesAsRead(Long ticketId, String senderType) {
        messageRepository.markAsReadByTicketAndSenderType(ticketId, senderType);
    }

    // ==================== Helper Methods ====================

    private TicketCategory parseCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return null; // Return null for no filter - query will match all categories
        }
        try {
            return TicketCategory.valueOf(category.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null; // Invalid category = no filter
        }
    }

    private TicketStatus parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }
        try {
            return TicketStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private TicketDTO toDTO(ContactTicket ticket, boolean includeMessages) {
        List<TicketMessageDTO> messageDTOs = null;
        String lastMessage = null;
        java.time.LocalDateTime lastMessageAt = null;
        int unreadCount = 0;

        if (includeMessages) {
            List<ContactTicketMessage> messages = messageRepository.findByTicketIdOrderByCreatedAtAsc(ticket.getId());
            messageDTOs = messages.stream()
                    .map(this::toMessageDTO)
                    .collect(Collectors.toList());

            if (!messages.isEmpty()) {
                ContactTicketMessage last = messages.get(messages.size() - 1);
                lastMessage = last.getContent();
                lastMessageAt = last.getCreatedAt();
            }

            unreadCount = (int) messages.stream()
                    .filter(m -> "ADMIN".equals(m.getSenderType()) && !Boolean.TRUE.equals(m.getIsRead()))
                    .count();
        } else {
            // Lấy tin nhắn cuối và đếm unread
            ContactTicketMessage last = messageRepository.findLastMessageByTicketId(ticket.getId());
            if (last != null) {
                lastMessage = last.getContent();
                lastMessageAt = last.getCreatedAt();
            }
            unreadCount = (int) messageRepository.countUnreadByTicketAndSenderType(ticket.getId(), "ADMIN");
        }

        return TicketDTO.builder()
                .id(ticket.getId())
                .ticketCode(ticket.getTicketCode())
                .userId(ticket.getUser() != null ? ticket.getUser().getId() : null)
                .name(ticket.getName())
                .email(ticket.getEmail())
                .phone(ticket.getPhone())
                .subject(ticket.getSubject())
                .category(ticket.getCategory())
                .categoryDisplayName(ticket.getCategory().getDisplayName())
                .status(ticket.getStatus())
                .statusDisplayName(ticket.getStatus().getDisplayName())
                .priority(ticket.getPriority())
                .assignedAdminId(ticket.getAssignedAdmin() != null ? ticket.getAssignedAdmin().getId() : null)
                .assignedAdminName(ticket.getAssignedAdmin() != null ? ticket.getAssignedAdmin().getFullName() : null)
                .messages(messageDTOs)
                .messageCount(ticket.getMessages() != null ? ticket.getMessages().size() : 0)
                .unreadCount(unreadCount)
                .lastMessage(lastMessage != null
                        ? (lastMessage.length() > 100 ? lastMessage.substring(0, 100) + "..." : lastMessage)
                        : null)
                .lastMessageAt(lastMessageAt)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .closedAt(ticket.getClosedAt())
                .build();
    }

    private TicketMessageDTO toMessageDTO(ContactTicketMessage message) {
        return TicketMessageDTO.builder()
                .id(message.getId())
                .senderType(message.getSenderType())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .isRead(message.getIsRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
