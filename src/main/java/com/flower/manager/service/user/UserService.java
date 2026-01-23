package com.flower.manager.service.user;

import com.flower.manager.dto.user.UpdateProfileRequest;
import com.flower.manager.dto.user.UserProfileDTO;

/**
 * Interface cho User Profile Service
 */
public interface UserService {

    /**
     * Lấy thông tin profile của user hiện tại
     */
    UserProfileDTO getCurrentUserProfile();

    /**
     * Cập nhật thông tin profile của user hiện tại
     */
    UserProfileDTO updateCurrentUserProfile(UpdateProfileRequest request);

    /**
     * Upload avatar cho user hiện tại
     */
    String uploadAvatar(org.springframework.web.multipart.MultipartFile file);
}
