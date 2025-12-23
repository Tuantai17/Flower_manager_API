package com.flower.manager.service.user;

import com.flower.manager.dto.user.UpdateProfileRequest;
import com.flower.manager.dto.user.UserProfileDTO;
import com.flower.manager.entity.User;
import com.flower.manager.exception.DuplicateResourceException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation của UserService
 * Xử lý logic quản lý thông tin cá nhân của user
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    /**
     * Lấy user hiện tại từ Security Context
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("Vui lòng đăng nhập để thực hiện chức năng này");
        }
        String identifier = authentication.getName();
        return userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
    }

    @Override
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile() {
        User user = getCurrentUser();
        log.info("Getting profile for user: {}", user.getUsername());
        return mapToDTO(user);
    }

    @Override
    public UserProfileDTO updateCurrentUserProfile(UpdateProfileRequest request) {
        User user = getCurrentUser();
        log.info("Updating profile for user: {}", user.getUsername());

        // Kiểm tra số điện thoại mới có bị trùng không (nếu thay đổi)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            String newPhone = request.getPhoneNumber().trim();
            if (!newPhone.equals(user.getPhoneNumber())) {
                if (userRepository.existsByPhoneNumberAndIdNot(newPhone, user.getId())) {
                    throw new DuplicateResourceException("Số điện thoại đã được sử dụng bởi tài khoản khác");
                }
                user.setPhoneNumber(newPhone);
                log.info("Updated phone number for user: {}", user.getUsername());
            }
        }

        // Cập nhật fullName (nếu có)
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName().trim());
        }

        // Cập nhật address (nếu có)
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }

        User savedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", savedUser.getUsername());

        return mapToDTO(savedUser);
    }

    /**
     * Map User entity sang UserProfileDTO
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
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
