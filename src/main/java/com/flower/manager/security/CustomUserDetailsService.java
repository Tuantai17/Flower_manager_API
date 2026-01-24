package com.flower.manager.security;

import com.flower.manager.entity.User;
import com.flower.manager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Custom UserDetailsService - Load user từ MySQL Database
 * Hỗ trợ load user bằng username, email hoặc số điện thoại
 * Chỉ active khi KHÔNG dùng profile "inmemory"
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!inmemory")
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by identifier (username, email hoặc số điện thoại)
     * 
     * @param identifier có thể là username, email hoặc số điện thoại
     * @return UserDetails
     * @throws UsernameNotFoundException nếu không tìm thấy user
     */
    @Override
    @Transactional(readOnly = true) // Hỗ trợ đăng nhập bằng Username OR Email OR Số điện thoại
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        log.debug("Attempting to load user by identifier: {}", identifier);

        User user = userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> {
                    log.warn("User not found with identifier: {}", identifier);
                    return new UsernameNotFoundException(
                            "Không tìm thấy tài khoản với thông tin: " + identifier);
                });
        // Kiểm tra tài khoản có bị khóa không
        if (!user.getIsActive()) {
            log.warn("User account is disabled: {}", identifier);
            throw new UsernameNotFoundException("Tài khoản đã bị vô hiệu hóa");
        }

        log.debug("User loaded successfully: {}", user.getUsername());
        return user;
    }

    /**
     * Load user by ID
     * 
     * @param id user ID
     * @return User
     * @throws UsernameNotFoundException nếu không tìm thấy
     */
    @Transactional(readOnly = true)
    public User loadUserById(@NonNull Long id) throws UsernameNotFoundException {
        Objects.requireNonNull(id, "User ID must not be null");
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy người dùng với ID: " + id));
    }
}
