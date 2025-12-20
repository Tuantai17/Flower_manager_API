package com.flower.manager.service.impl;

import com.flower.manager.dto.UserDTO;
import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.ForgotPasswordRequest;
import com.flower.manager.dto.auth.LoginRequest;
import com.flower.manager.dto.auth.RegisterRequest;
import com.flower.manager.dto.auth.ResetPasswordRequest;
import com.flower.manager.entity.Role;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceAlreadyExistsException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.security.JwtUtils;
import com.flower.manager.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

/**
 * Implementation của AuthService
 * Xử lý logic đăng nhập (username/email/phone), đăng ký, quên mật khẩu
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    /**
     * Đăng nhập người dùng
     * Hỗ trợ đăng nhập bằng username, email hoặc số điện thoại
     * 
     * @param loginRequest chứa identifier (username/email/phone) và password
     * @return AuthResponse với token JWT và thông tin user
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        String identifier = loginRequest.getIdentifier().trim();

        log.info("Login attempt for identifier: {}", identifier);

        try {
            // Xác thực username/email/phone và password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            identifier,
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tạo JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Lấy thông tin user
            User user = (User) authentication.getPrincipal();

            log.info("User '{}' logged in successfully via {}",
                    user.getUsername(), getIdentifierType(identifier));

            return AuthResponse.success("Đăng nhập thành công", jwt, mapToUserDTO(user));

        } catch (DisabledException e) {
            log.warn("Login failed for '{}': Account is disabled", identifier);
            throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
        } catch (BadCredentialsException e) {
            log.warn("Login failed for '{}': Invalid credentials", identifier);
            throw new BadCredentialsException("Thông tin đăng nhập không chính xác");
        }
    }

    /**
     * Đăng ký người dùng mới
     * Chỉ cần: username, email, phoneNumber, password, confirmPassword
     * 
     * @param request chứa thông tin đăng ký
     * @return AuthResponse với token JWT và thông tin user
     */
    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for username: {}, email: {}, phone: {}",
                request.getUsername(), request.getEmail(), request.getPhoneNumber());

        // === VALIDATE ===

        // 1. Kiểm tra password và confirmPassword khớp nhau
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "Mật khẩu xác nhận không khớp");
        }

        // 2. Kiểm tra username đã tồn tại
        if (Boolean.TRUE.equals(userRepository.existsByUsername(request.getUsername().trim()))) {
            throw new ResourceAlreadyExistsException(
                    "Tên đăng nhập '" + request.getUsername() + "' đã được sử dụng");
        }

        // 3. Kiểm tra email đã tồn tại
        if (Boolean.TRUE.equals(userRepository.existsByEmail(request.getEmail().trim().toLowerCase()))) {
            throw new ResourceAlreadyExistsException(
                    "Email '" + request.getEmail() + "' đã được đăng ký");
        }

        // 4. Kiểm tra số điện thoại đã tồn tại
        String normalizedPhone = normalizePhoneNumber(request.getPhoneNumber());
        if (Boolean.TRUE.equals(userRepository.existsByPhoneNumber(normalizedPhone))) {
            throw new ResourceAlreadyExistsException(
                    "Số điện thoại '" + request.getPhoneNumber() + "' đã được đăng ký");
        }

        // === TẠO USER MỚI ===
        User user = User.builder()
                .username(request.getUsername().trim())
                .email(request.getEmail().trim().toLowerCase())
                .phoneNumber(normalizedPhone)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER) // Mặc định là CUSTOMER
                .isActive(true)
                .build();

        User savedUser = Objects.requireNonNull(userRepository.save(user), "Failed to save user");

        log.info("New user registered successfully: username={}, role={}",
                savedUser.getUsername(), savedUser.getRole());

        // === TỰ ĐỘNG ĐĂNG NHẬP SAU KHI ĐĂNG KÝ ===
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return AuthResponse.success("Đăng ký thành công", jwt, mapToUserDTO(savedUser));
    }

    /**
     * Xử lý yêu cầu quên mật khẩu
     * Gửi email reset password (giả lập - chỉ tạo token)
     * 
     * @param request chứa email
     * @return AuthResponse với thông báo
     */
    @Override
    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        if (!user.getIsActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
        }

        // Tạo reset token (trong thực tế sẽ lưu vào DB và gửi email)
        String resetToken = UUID.randomUUID().toString();

        // NOTE: Trong production, cần implement:
        // 1. Lưu reset token vào DB với thời hạn (vd: 15 phút)
        // 2. Gửi email chứa link reset password
        // Hiện tại đang trả về token trực tiếp cho mục đích demo/test

        log.info("Password reset requested for email: {}. Reset token generated.", email);

        return AuthResponse.builder()
                .success(true)
                .message("Đã gửi email hướng dẫn đặt lại mật khẩu đến " + email)
                .token(resetToken) // Trong production không trả về token này
                .build();
    }

    /**
     * Đặt lại mật khẩu với token
     * 
     * @param request chứa token, email và mật khẩu mới
     * @return AuthResponse với thông báo
     */
    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        // Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "Mật khẩu xác nhận không khớp");
        }

        // NOTE: Trong production, cần validate reset token từ DB
        // Hiện tại đang tìm user theo email cho mục đích demo/test
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        if (!user.getIsActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password reset successful for user: {}", user.getUsername());

        return AuthResponse.builder()
                .success(true)
                .message("Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới.")
                .build();
    }

    /**
     * Lấy thông tin user hiện tại đang đăng nhập
     * 
     * @return UserDTO
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResourceNotFoundException("Không tìm thấy thông tin người dùng");
        }

        String identifier = authentication.getName();
        User user = userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + identifier));

        return mapToUserDTO(user);
    }

    // ============== HELPER METHODS ==============

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

    /**
     * Chuẩn hóa số điện thoại
     * VD: +84912345678 -> 0912345678
     */
    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }

        String normalized = phoneNumber.trim();

        // Convert +84 to 0
        if (normalized.startsWith("+84")) {
            normalized = "0" + normalized.substring(3);
        }

        // Remove all non-digit characters except leading +
        normalized = normalized.replaceAll("[^0-9]", "");

        return normalized;
    }

    /**
     * Xác định loại identifier (username, email hay phone)
     */
    private String getIdentifierType(String identifier) {
        if (identifier.contains("@")) {
            return "email";
        } else if (identifier.matches("^[0-9+]+$")) {
            return "phone";
        }
        return "username";
    }
}
