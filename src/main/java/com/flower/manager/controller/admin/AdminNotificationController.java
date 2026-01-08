package com.flower.manager.controller.admin;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.notification.NotificationPayload;
import com.flower.manager.entity.User;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.notification.TicketNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller quản lý notifications cho admin
 */
@RestController
@RequestMapping("/api/admin/notifications")
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationController {

    private final TicketNotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Lấy danh sách notifications cho admin
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationPayload>>> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationPayload> notifications = notificationService.getAdminNotifications(admin.getId(), pageable);

        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Lấy notifications chưa đọc (cho dropdown nhanh)
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationPayload>>> getUnreadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        List<NotificationPayload> notifications = notificationService.getUnreadAdminNotifications(admin.getId());
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Đếm số notifications chưa đọc
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        Long count = notificationService.countUnreadForAdmin(admin.getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("unreadCount", count)));
    }

    /**
     * Đánh dấu tất cả đã đọc
     */
    @PostMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }

        notificationService.markAllReadForAdmin(admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu tất cả đã đọc"));
    }

    /**
     * Đánh dấu 1 notification đã đọc
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu đã đọc"));
    }

    // ==================== DELETE ENDPOINTS ====================

    /**
     * Xóa 1 notification
     * DELETE /api/admin/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa thông báo"));
    }

    /**
     * Xóa nhiều notifications
     * DELETE /api/admin/notifications/bulk
     */
    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> deleteNotifications(@RequestBody List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Danh sách ID không hợp lệ"));
        }
        notificationService.deleteNotifications(ids);
        return ResponseEntity.ok(ApiResponse.success("Đã xóa " + ids.size() + " thông báo"));
    }

    /**
     * Xóa tất cả notifications
     * DELETE /api/admin/notifications/all
     */
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<String>> deleteAllNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }
        notificationService.deleteAllForAdmin(admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã xóa tất cả thông báo"));
    }

    /**
     * Xóa các notifications đã đọc
     * DELETE /api/admin/notifications/read
     */
    @DeleteMapping("/read")
    public ResponseEntity<ApiResponse<String>> deleteReadNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        User admin = getAdminFromDetails(userDetails);
        if (admin == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("Không tìm thấy thông tin admin"));
        }
        notificationService.deleteReadForAdmin(admin.getId());
        return ResponseEntity.ok(ApiResponse.success("Đã xóa các thông báo đã đọc"));
    }

    // ==================== Helper ====================

    private User getAdminFromDetails(UserDetails userDetails) {
        if (userDetails == null)
            return null;
        return userRepository.findByUsernameOrEmailOrPhoneNumber(userDetails.getUsername()).orElse(null);
    }
}
