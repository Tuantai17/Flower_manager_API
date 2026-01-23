package com.flower.manager.controller.user;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.user.UpdateProfileRequest;
import com.flower.manager.dto.user.UserProfileDTO;
import com.flower.manager.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý các API liên quan đến User Profile
 * Endpoint: /api/users/**
 * 
 * Lưu ý: User chỉ có thể xem và sửa thông tin của chính mình
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Lấy thông tin profile của user hiện tại
     * GET /api/users/me
     * 
     * @return UserProfileDTO chứa thông tin user (không bao gồm password)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> getMyProfile() {
        log.info("Get current user profile");
        UserProfileDTO profile = userService.getCurrentUserProfile();
        return ResponseEntity.ok(ApiResponse.success(profile, "Lấy thông tin thành công"));
    }

    /**
     * Cập nhật thông tin profile của user hiện tại
     * PUT /api/users/me
     * 
     * Chỉ cho phép cập nhật: fullName, phoneNumber, address
     * 
     * @param request UpdateProfileRequest chứa thông tin cần cập nhật
     * @return UserProfileDTO với thông tin đã được cập nhật
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileDTO>> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request) {
        log.info("Update current user profile");
        UserProfileDTO profile = userService.updateCurrentUserProfile(request);
        return ResponseEntity.ok(ApiResponse.success(profile, "Cập nhật thông tin thành công"));
    }

    /**
     * Upload avatar cho user hiện tại
     * POST /api/users/me/avatar
     */
    @PostMapping(value = "/me/avatar", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        log.info("Upload avatar request");
        String avatarUrl = userService.uploadAvatar(file);
        return ResponseEntity.ok(ApiResponse.success(avatarUrl, "Upload avatar thành công"));
    }
}
