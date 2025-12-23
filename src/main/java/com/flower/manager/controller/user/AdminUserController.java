package com.flower.manager.controller.user;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.user.UserProfileDTO;
import com.flower.manager.entity.Role;
import com.flower.manager.entity.User;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller cho Admin quản lý khách hàng
 * Endpoint: /api/admin/customers/**
 */
@RestController
@RequestMapping("/api/admin/customers")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;

    /**
     * Lấy danh sách khách hàng với phân trang và tìm kiếm
     * GET /api/admin/customers
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserProfileDTO>>> getAllCustomers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        log.info("Admin fetching customers - keyword: {}, role: {}, isActive: {}", keyword, role, isActive);

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Role roleEnum = role != null && !role.isEmpty() ? Role.valueOf(role.toUpperCase()) : null;

        Page<User> users = userRepository.findWithFilters(keyword, roleEnum, isActive, pageable);
        Page<UserProfileDTO> result = users.map(this::mapToDTO);

        return ResponseEntity.ok(ApiResponse.success(result, "Lấy danh sách khách hàng thành công"));
    }

    /**
     * Thống kê khách hàng
     * GET /api/admin/customers/stats
     * NOTE: Đặt TRƯỚC /{id} để tránh conflict
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCustomerStats() {
        long totalCustomers = userRepository.countByRole(Role.CUSTOMER);
        long totalAdmins = userRepository.countByRole(Role.ADMIN);
        long totalStaff = userRepository.countByRole(Role.STAFF);
        long totalUsers = userRepository.count();

        Map<String, Object> stats = Map.of(
                "totalCustomers", totalCustomers,
                "totalAdmins", totalAdmins,
                "totalStaff", totalStaff,
                "totalUsers", totalUsers);

        return ResponseEntity.ok(ApiResponse.success(stats, "Lấy thống kê thành công"));
    }

    /**
     * Lấy chi tiết khách hàng theo ID
     * GET /api/admin/customers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getCustomerById(@PathVariable Long id) {
        log.info("Admin fetching customer by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return ResponseEntity.ok(ApiResponse.success(mapToDTO(user), "Lấy thông tin khách hàng thành công"));
    }

    /**
     * Cập nhật trạng thái active của khách hàng (khóa/mở khóa)
     * PUT /api/admin/customers/{id}/status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateCustomerStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {

        log.info("Admin updating customer status - id: {}, isActive: {}", id, request.get("isActive"));

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(request.get("isActive"));
        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(mapToDTO(savedUser),
                request.get("isActive") ? "Mở khóa tài khoản thành công" : "Khóa tài khoản thành công"));
    }

    /**
     * Map User entity sang DTO
     */
    private UserProfileDTO mapToDTO(User user) {
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
