package com.flower.manager.service.auth;

import com.flower.manager.dto.auth.UserDTO;
import com.flower.manager.dto.auth.AuthResponse;
import com.flower.manager.dto.auth.ForgotPasswordRequest;
import com.flower.manager.dto.auth.LoginRequest;
import com.flower.manager.dto.auth.RegisterRequest;
import com.flower.manager.dto.auth.ResetPasswordRequest;
import com.flower.manager.entity.PasswordResetToken;
import com.flower.manager.entity.Role;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceAlreadyExistsException;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.PasswordResetTokenRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.security.JwtUtils;
import com.flower.manager.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EmailService emailService;
    private final EmailVerificationService emailVerificationService;

    /**
     * URL Frontend dùng cho link reset password
     */
    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

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

        // === GỬI EMAIL XÁC THỰC ===
        try {
            emailVerificationService.sendVerificationEmail(savedUser);
            log.info("Verification email sent to: {}", savedUser.getEmail());
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage());
            // Không throw exception để không block đăng ký
        }

        // === TỰ ĐỘNG ĐĂNG NHẬP SAU KHI ĐĂNG KÝ ===
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return AuthResponse.success(
                "Đăng ký thành công! Vui lòng kiểm tra email để xác thực tài khoản.",
                jwt,
                mapToUserDTO(savedUser));
    }

    /**
     * Xử lý yêu cầu quên mật khẩu
     * 1. Kiểm tra email tồn tại
     * 2. Vô hiệu hóa các token cũ
     * 3. Tạo token mới và lưu vào DB
     * 4. Gửi email chứa link reset password
     * 
     * @param request chứa email
     * @return AuthResponse với thông báo
     */
    @Override
    @Transactional
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        log.info("Forgot password request for email: {}", email);

        // 1. Kiểm tra email tồn tại
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy tài khoản với email: " + email));

        if (!user.getIsActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
        }

        // 2. Vô hiệu hóa tất cả token cũ của user này
        int invalidatedCount = passwordResetTokenRepository.invalidateOldTokens(user);
        if (invalidatedCount > 0) {
            log.info("Invalidated {} old reset tokens for user: {}", invalidatedCount, email);
        }

        // 3. Tạo token mới
        String tokenValue = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(tokenValue, user);
        passwordResetTokenRepository.save(resetToken);

        log.info("Created new password reset token for email: {}, expires at: {}",
                email, resetToken.getExpiryDate());

        // 4. Gửi email chứa link reset password (async)
        emailService.sendPasswordResetEmail(email, tokenValue, frontendUrl);

        return AuthResponse.builder()
                .success(true)
                .message("Đã gửi email hướng dẫn đặt lại mật khẩu đến " + email +
                        ". Vui lòng kiểm tra hộp thư (bao gồm cả thư rác).")
                .build();
    }

    /**
     * Đặt lại mật khẩu với token
     * 1. Validate token từ DB (còn hạn, chưa dùng)
     * 2. Validate password match
     * 3. Hash và update password mới
     * 4. Đánh dấu token đã sử dụng
     * 5. Gửi email xác nhận đã đổi mật khẩu
     * 
     * @param request chứa token, email và mật khẩu mới
     * @return AuthResponse với thông báo
     */
    @Override
    @Transactional
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        String email = request.getEmail().trim().toLowerCase();
        String tokenValue = request.getToken();

        log.info("Reset password request for email: {}", email);

        // 1. Validate password match
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("PASSWORD_MISMATCH", "Mật khẩu xác nhận không khớp");
        }

        // 2. Tìm và validate token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("INVALID_TOKEN",
                        "Token không hợp lệ hoặc không tồn tại"));

        // 3. Kiểm tra token đã dùng chưa
        if (resetToken.getUsed()) {
            throw new BusinessException("TOKEN_USED",
                    "Token đã được sử dụng. Vui lòng yêu cầu đặt lại mật khẩu mới.");
        }

        // 4. Kiểm tra token hết hạn chưa
        if (resetToken.isExpired()) {
            throw new BusinessException("TOKEN_EXPIRED",
                    "Token đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.");
        }

        // 5. Kiểm tra email khớp với token
        User user = resetToken.getUser();
        if (!user.getEmail().equalsIgnoreCase(email)) {
            throw new BusinessException("EMAIL_MISMATCH",
                    "Email không khớp với token");
        }

        // 6. Kiểm tra tài khoản còn hoạt động
        if (!user.getIsActive()) {
            throw new BusinessException("ACCOUNT_DISABLED", "Tài khoản đã bị vô hiệu hóa");
        }

        // 7. Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 8. Đánh dấu token đã sử dụng (chỉ dùng 1 lần)
        resetToken.markAsUsed();
        passwordResetTokenRepository.save(resetToken);

        log.info("Password reset successful for user: {}", user.getUsername());

        // 9. Gửi email xác nhận đã đổi mật khẩu (async)
        emailService.sendPasswordChangedEmail(email);

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
                .emailVerified(user.getEmailVerified())
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
