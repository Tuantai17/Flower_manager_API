package com.flower.manager.service.auth;

import com.flower.manager.dto.auth.*;
import com.flower.manager.entity.Role;
import com.flower.manager.entity.User;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.exception.ResourceAlreadyExistsException;
import com.flower.manager.repository.PasswordResetTokenRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.security.JwtUtils;
import com.flower.manager.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests cho AuthServiceImpl
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthServiceImpl Unit Tests")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private EmailService emailService;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phoneNumber("0912345678")
                .password("encodedPassword")
                .fullName("Test User")
                .role(Role.CUSTOMER)
                .isActive(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should login successfully with valid credentials")
        void login_ValidCredentials_ReturnsAuthResponse() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setIdentifier("testuser");
            request.setPassword("password123");

            Authentication authentication = mock(Authentication.class);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(testUser);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

            // Act
            AuthResponse result = authService.login(request);

            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals("jwt-token", result.getToken());
            assertNotNull(result.getUser());
        }

        @Test
        @DisplayName("Should throw exception with invalid credentials")
        void login_InvalidCredentials_ThrowsException() {
            // Arrange
            LoginRequest request = new LoginRequest();
            request.setIdentifier("testuser");
            request.setPassword("wrongpassword");

            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Invalid credentials"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> authService.login(request));
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should register successfully with valid data")
        void register_ValidData_ReturnsAuthResponse() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newuser");
            request.setEmail("new@example.com");
            request.setPhoneNumber("0987654321");
            request.setPassword("password123");
            request.setConfirmPassword("password123");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("0987654321")).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

            User savedUser = User.builder()
                    .id(2L)
                    .username("newuser")
                    .email("new@example.com")
                    .phoneNumber("0987654321")
                    .role(Role.CUSTOMER)
                    .isActive(true)
                    .emailVerified(false)
                    .createdAt(LocalDateTime.now())
                    .build();
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            Authentication authentication = mock(Authentication.class);
            when(authenticationManager.authenticate(any())).thenReturn(authentication);
            when(authentication.getPrincipal()).thenReturn(savedUser);
            when(jwtUtils.generateJwtToken(authentication)).thenReturn("jwt-token");

            // Act
            AuthResponse result = authService.register(request);

            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            assertEquals("jwt-token", result.getToken());
            verify(emailVerificationService, times(1)).sendVerificationEmail(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when passwords don't match")
        void register_PasswordMismatch_ThrowsException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newuser");
            request.setEmail("new@example.com");
            request.setPhoneNumber("0987654321");
            request.setPassword("password123");
            request.setConfirmPassword("differentpassword");

            // Act & Assert
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> authService.register(request));
            assertEquals("PASSWORD_MISMATCH", exception.getErrorCode());
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void register_UsernameExists_ThrowsException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("testuser");
            request.setEmail("new@example.com");
            request.setPhoneNumber("0987654321");
            request.setPassword("password123");
            request.setConfirmPassword("password123");

            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            // Act & Assert
            assertThrows(ResourceAlreadyExistsException.class,
                    () -> authService.register(request));
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void register_EmailExists_ThrowsException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newuser");
            request.setEmail("test@example.com");
            request.setPhoneNumber("0987654321");
            request.setPassword("password123");
            request.setConfirmPassword("password123");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // Act & Assert
            assertThrows(ResourceAlreadyExistsException.class,
                    () -> authService.register(request));
        }

        @Test
        @DisplayName("Should throw exception when phone already exists")
        void register_PhoneExists_ThrowsException() {
            // Arrange
            RegisterRequest request = new RegisterRequest();
            request.setUsername("newuser");
            request.setEmail("new@example.com");
            request.setPhoneNumber("0912345678");
            request.setPassword("password123");
            request.setConfirmPassword("password123");

            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.existsByPhoneNumber("0912345678")).thenReturn(true);

            // Act & Assert
            assertThrows(ResourceAlreadyExistsException.class,
                    () -> authService.register(request));
        }
    }

    @Nested
    @DisplayName("Forgot Password Tests")
    class ForgotPasswordTests {

        @Test
        @DisplayName("Should send reset email when user exists")
        void forgotPassword_ValidEmail_SendsResetEmail() {
            // Arrange
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("test@example.com");

            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
            when(passwordResetTokenRepository.invalidateOldTokens(testUser)).thenReturn(0);
            when(passwordResetTokenRepository.save(any())).thenReturn(null);

            // Act
            AuthResponse result = authService.forgotPassword(request);

            // Assert
            assertNotNull(result);
            assertTrue(result.isSuccess());
            verify(emailService, times(1)).sendPasswordResetEmail(anyString(), anyString(), anyString());
        }
    }
}
