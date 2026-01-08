package com.flower.manager.controller.ticket;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.ticket.*;
import com.flower.manager.entity.User;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.ticket.ContactTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller cho Contact Ticket (public & user)
 * Endpoints cho khách hàng gửi và theo dõi ticket
 */
@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contact Ticket", description = "API liên hệ/hỗ trợ khách hàng")
public class ContactTicketController {

    private final ContactTicketService ticketService;
    private final UserRepository userRepository;

    /**
     * Helper method to get User from UserDetails
     */
    private User getUserFromDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        // userDetails.getUsername() could be username, email, or phone
        return userRepository.findByUsernameOrEmailOrPhoneNumber(userDetails.getUsername()).orElse(null);
    }

    /**
     * Tạo ticket mới (public - không bắt buộc đăng nhập)
     */
    @PostMapping("/tickets")
    @Operation(summary = "Tạo ticket liên hệ mới")
    public ResponseEntity<ApiResponse<TicketDTO>> createTicket(
            @Valid @RequestBody CreateTicketRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = null;
        User user = getUserFromDetails(userDetails);
        if (user != null) {
            userId = user.getId();
        }

        log.info("Creating ticket - userId: {}, email: {}", userId, request.getEmail());
        TicketDTO ticket = ticketService.createTicket(request, userId);

        return ResponseEntity
                .ok(ApiResponse.success(ticket, "Gửi yêu cầu thành công! Mã ticket: " + ticket.getTicketCode()));
    }

    /**
     * Lấy danh sách tickets của user đang đăng nhập
     */
    @GetMapping("/tickets/my")
    @Operation(summary = "Lấy danh sách ticket của tôi")
    public ResponseEntity<ApiResponse<List<TicketDTO>>> getMyTickets(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            log.warn("getMyTickets called without authentication");
            return ResponseEntity.ok(ApiResponse.success(List.of(), "Vui lòng đăng nhập để xem ticket"));
        }

        log.info("getMyTickets - username from token: {}", userDetails.getUsername());

        User user = getUserFromDetails(userDetails);
        if (user == null) {
            log.warn("User not found for username: {}", userDetails.getUsername());
            return ResponseEntity.ok(ApiResponse.success(List.of(), "Không tìm thấy thông tin người dùng"));
        }

        log.info("Found user: id={}, email={}", user.getId(), user.getEmail());
        List<TicketDTO> tickets = ticketService.getMyTickets(user.getId(), user.getEmail());
        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Xem chi tiết ticket
     */
    @GetMapping("/tickets/{id}")
    @Operation(summary = "Xem chi tiết ticket")
    public ResponseEntity<ApiResponse<TicketDTO>> getTicketDetails(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = null;
        String email = null;

        User user = getUserFromDetails(userDetails);
        if (user != null) {
            userId = user.getId();
            email = user.getEmail();
        }

        TicketDTO ticket = ticketService.getTicketDetails(id, userId, email);

        // Mark admin messages as read
        ticketService.markMessagesAsRead(id, "ADMIN");

        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    /**
     * User gửi tin nhắn mới vào ticket
     */
    @PostMapping("/tickets/{id}/messages")
    @Operation(summary = "Gửi tin nhắn mới vào ticket")
    public ResponseEntity<ApiResponse<TicketMessageDTO>> sendMessage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String content = body.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Nội dung tin nhắn không được để trống"));
        }

        Long userId = null;
        String senderName = null;

        User user = getUserFromDetails(userDetails);
        if (user != null) {
            userId = user.getId();
            senderName = user.getFullName();
        }

        TicketMessageDTO message = ticketService.addUserMessage(id, content.trim(), userId, senderName);
        return ResponseEntity.ok(ApiResponse.success(message, "Tin nhắn đã được gửi"));
    }
}
