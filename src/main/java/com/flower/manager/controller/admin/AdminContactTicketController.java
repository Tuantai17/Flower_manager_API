package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.ticket.*;
import com.flower.manager.entity.User;
import com.flower.manager.enums.TicketStatus;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.ticket.ContactTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller cho Admin quản lý Contact Ticket
 */
@RestController
@RequestMapping("/api/admin/tickets")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - Contact Tickets", description = "API quản lý ticket liên hệ cho Admin")
public class AdminContactTicketController {

    private final ContactTicketService ticketService;
    private final UserRepository userRepository;

    /**
     * Helper method to get Admin User from UserDetails
     */
    private User getAdminFromDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        // userDetails.getUsername() could be username, email, or phone
        return userRepository.findByUsernameOrEmailOrPhoneNumber(userDetails.getUsername()).orElse(null);
    }

    /**
     * Lấy danh sách tất cả tickets với filter và pagination
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tickets")
    public ResponseEntity<ApiResponse<Page<TicketDTO>>> getAllTickets(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TicketDTO> tickets = ticketService.getAllTickets(status, category, search, pageable);

        return ResponseEntity.ok(ApiResponse.success(tickets));
    }

    /**
     * Thống kê tickets
     */
    @GetMapping("/stats")
    @Operation(summary = "Thống kê tickets")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getTicketStats() {
        Map<String, Long> stats = ticketService.getTicketStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    /**
     * Xem chi tiết ticket
     */
    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết ticket")
    public ResponseEntity<ApiResponse<TicketDTO>> getTicketDetails(@PathVariable Long id) {
        TicketDTO ticket = ticketService.getTicketForAdmin(id);

        // Mark user messages as read
        ticketService.markMessagesAsRead(id, "USER");

        return ResponseEntity.ok(ApiResponse.success(ticket));
    }

    /**
     * Cập nhật trạng thái ticket
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "Cập nhật trạng thái ticket")
    public ResponseEntity<ApiResponse<TicketDTO>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String statusStr = body.get("status");
        if (statusStr == null || statusStr.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Trạng thái không được để trống"));
        }

        TicketStatus status;
        try {
            status = TicketStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Trạng thái không hợp lệ"));
        }

        User admin = getAdminFromDetails(userDetails);
        Long adminId = admin != null ? admin.getId() : null;

        TicketDTO ticket = ticketService.updateTicketStatus(id, status, adminId);
        return ResponseEntity.ok(ApiResponse.success(ticket, "Cập nhật trạng thái thành công"));
    }

    /**
     * Admin phản hồi ticket
     */
    @PostMapping("/{id}/reply")
    @Operation(summary = "Phản hồi ticket")
    public ResponseEntity<ApiResponse<TicketMessageDTO>> replyToTicket(
            @PathVariable Long id,
            @Valid @RequestBody AdminReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Admin reply to ticket {} - username from token: {}", id,
                userDetails != null ? userDetails.getUsername() : "null");

        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            log.error("Admin not found for username: {}",
                    userDetails != null ? userDetails.getUsername() : "null");
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        log.info("Found admin: id={}, fullName={}", admin.getId(), admin.getFullName());
        TicketMessageDTO message = ticketService.addAdminReply(id, request, admin.getId(), admin.getFullName());
        return ResponseEntity.ok(ApiResponse.success(message, "Phản hồi đã được gửi"));
    }

    /**
     * Giao ticket cho admin
     */
    @PutMapping("/{id}/assign")
    @Operation(summary = "Giao ticket cho admin")
    public ResponseEntity<ApiResponse<TicketDTO>> assignTicket(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        TicketDTO ticket = ticketService.assignTicket(id, admin.getId());
        return ResponseEntity.ok(ApiResponse.success(ticket, "Đã nhận xử lý ticket"));
    }
}
