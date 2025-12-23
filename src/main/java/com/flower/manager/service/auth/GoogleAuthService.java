package com.flower.manager.service.auth;

import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.GoogleLoginRequest;
import com.flower.manager.dto.auth.GoogleUserInfo;
import com.flower.manager.dto.auth.UserDTO;
import com.flower.manager.entity.Role;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.security.JwtUtils;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

/**
 * Service xử lý đăng nhập bằng Google OAuth2
 * Sử dụng Google ID Token Verification
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Value("${google.client-id}")
    private String googleClientId;

    /**
     * Xác thực và đăng nhập bằng Google
     *
     * @param request chứa idToken từ Frontend
     * @return AuthResponse với JWT token
     */
    @Transactional
    public AuthResponse loginWithGoogle(GoogleLoginRequest request) {
        log.info("Google login attempt with idToken");

        // 1. Xác thực token với Google
        GoogleUserInfo googleUser = verifyGoogleToken(request.getIdToken());

        if (googleUser == null) {
            throw new BusinessException("INVALID_GOOGLE_TOKEN", "Token Google không hợp lệ");
        }

        if (!Boolean.TRUE.equals(googleUser.getEmailVerified())) {
            throw new BusinessException("EMAIL_NOT_VERIFIED", "Email chưa được xác minh bởi Google");
        }

        log.info("Google token verified for email: {}", googleUser.getEmail());

        // 2. Tìm hoặc tạo user trong database
        User user = findOrCreateUser(googleUser);

        // 3. Tạo JWT token của hệ thống
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtils.generateJwtToken(authentication);

        log.info("User '{}' logged in successfully via Google", user.getUsername());

        return AuthResponse.success("Đăng nhập bằng Google thành công", jwt, mapToUserDTO(user));
    }

    /**
     * Xác thực Google ID Token
     *
     * @param idTokenString token từ Frontend
     * @return GoogleUserInfo nếu hợp lệ, null nếu không
     */
    private GoogleUserInfo verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken == null) {
                log.warn("Google ID Token verification failed - token is null or invalid");
                return null;
            }

            GoogleIdToken.Payload payload = idToken.getPayload();

            return GoogleUserInfo.builder()
                    .googleId(payload.getSubject())
                    .email(payload.getEmail())
                    .emailVerified(payload.getEmailVerified())
                    .fullName((String) payload.get("name"))
                    .pictureUrl((String) payload.get("picture"))
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            log.error("Error verifying Google token: {}", e.getMessage());
            throw new BusinessException("GOOGLE_AUTH_ERROR", "Lỗi xác thực với Google: " + e.getMessage());
        }
    }

    /**
     * Tìm user theo email hoặc tạo mới nếu chưa tồn tại
     *
     * @param googleUser thông tin từ Google
     * @return User entity
     */
    private User findOrCreateUser(GoogleUserInfo googleUser) {
        String email = googleUser.getEmail().toLowerCase();

        return userRepository.findByEmail(email)
                .map(existingUser -> {
                    // User đã tồn tại - kiểm tra active
                    if (!existingUser.getIsActive()) {
                        throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
                    }

                    // Cập nhật thông tin từ Google nếu cần
                    boolean updated = false;

                    if (googleUser.getFullName() != null && existingUser.getFullName() == null) {
                        existingUser.setFullName(googleUser.getFullName());
                        updated = true;
                    }

                    if (updated) {
                        userRepository.save(existingUser);
                        log.info("Updated existing user info from Google: {}", email);
                    }

                    log.info("Existing user logged in via Google: {}", email);
                    return existingUser;
                })
                .orElseGet(() -> {
                    // Tạo user mới
                    log.info("Creating new user from Google login: {}", email);

                    // Tạo username từ email (phần trước @)
                    String baseUsername = email.split("@")[0];
                    String username = generateUniqueUsername(baseUsername);

                    // Tạo password ngẫu nhiên (user sẽ không cần dùng vì đăng nhập qua Google)
                    String randomPassword = UUID.randomUUID().toString();

                    User newUser = User.builder()
                            .username(username)
                            .email(email)
                            .fullName(googleUser.getFullName())
                            .password(passwordEncoder.encode(randomPassword))
                            .role(Role.CUSTOMER) // Mặc định CUSTOMER
                            .isActive(true)
                            .build();

                    User savedUser = userRepository.save(newUser);
                    log.info("New user created via Google: username={}, email={}", username, email);

                    return savedUser;
                });
    }

    /**
     * Tạo username duy nhất nếu đã tồn tại
     *
     * @param baseUsername username gốc
     * @return username duy nhất
     */
    private String generateUniqueUsername(String baseUsername) {
        // Loại bỏ ký tự đặc biệt
        String cleanUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");

        if (cleanUsername.length() < 3) {
            cleanUsername = "user" + cleanUsername;
        }

        String username = cleanUsername;
        int suffix = 1;

        while (Boolean.TRUE.equals(userRepository.existsByUsername(username))) {
            username = cleanUsername + suffix;
            suffix++;
        }

        return username;
    }

    /**
     * Map User entity sang UserDTO
     */
    private UserDTO mapToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole().name())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
