# BÁO CÁO CHI TIẾT: QUY TRÌNH & KỸ THUẬT TEST AUTHENTICATION

Tài liệu này phân tích sâu về file `AuthServiceImplTest.java` - nơi chứa các test case quan trọng và phức tạp nhất dự án (Đăng ký, Đăng nhập, Quên mật khẩu).

---

## 1. Cấu Trúc & Setup (Khởi Tạo)

Để Unit Test hoạt động độc lập (không cần Database thật), chúng ta sử dụng **Mockito**.

### a. Các Thành Phần Chính (`@Mock` vs `@InjectMocks`)

```java
@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository; // Giả lập Database

    @Mock
    private AsyncEmailService asyncEmailService; // Giả lập gửi Email

    @InjectMocks
    private AuthServiceImpl authService; // Service chính cần test (sẽ được bơm các Mock vào)
}
```

- **Giải thích:** Thay vì kết nối MySQL thật, ta tạo ra các "diễn viên đóng thế" (`@Mock`). Khi `AuthService` chạy, nó sẽ gọi đến các diễn viên này thay vì gọi Database thật.

### b. Kỹ Thuật Nâng Cao: ReflectionTestUtils

```java
@BeforeEach
void setUp() {
    ReflectionTestUtils.setField(authService, "frontendUrl", "http://localhost:3000");
}
```

- **Vấn đề:** Trong code thật, biến `frontendUrl` lấy từ file cấu hình thông qua `@Value`. Mockito không thể tự hiểu `@Value`.
- **Giải pháp:** Dùng `ReflectionTestUtils` để "hack" (set giá trị trực tiếp) vào biến private này, giúp test không bị lỗi `NullPointerException`.

---

## 2. Phân Tích Chi Tiết Các Test Case

### ✅ Test Case 1: Đăng Nhập Thành Công (`login`)

Quy trình kiểm thử luồng đăng nhập chuẩn:

```java
@Test
void login_ValidCredentials_ReturnsAuthResponse() {
    // 1. CHUẨN BỊ (ARRANGE)
    // Giả lập rằng tìm user trong DB sẽ thấy
    when(userRepository.findByUsernameOrEmailOrPhoneNumber("testuser"))
            .thenReturn(Optional.of(testUser));

    // Giả lập mật khẩu nhập vào khớp với mật khẩu db
    when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

    // Giả lập AuthenticationManager xác thực thành công
    when(authenticationManager.authenticate(...)).thenReturn(authentication);

    // 2. THỰC THI (ACT)
    AuthResponse result = authService.login(request);

    // 3. KIỂM TRA (ASSERT)
    assertNotNull(result); // Kết quả không được null
    assertEquals("jwt-token", result.getToken()); // Phải có token trả về
}
```

### ✅ Test Case 2: Đăng Ký & Gửi Email Async (`register`)

Đây là test case phức tạp vì có xử lý Bất đồng bộ (Async).

```java
@Test
void register_ValidData_ReturnsAuthResponse() {
    // 1. CHUẨN BỊ
    // Giả lập chưa có ai trùng tên/email/sdt
    when(userRepository.existsByUsername(...)).thenReturn(false);

    // 2. THỰC THI
    authService.register(request);

    // 3. KIỂM TRA (VERIFY)
    // Quan trọng: Kiểm tra xem hàm gửi email async có được gọi đúng 1 lần không?
    verify(asyncEmailService, times(1)).sendVerificationEmailAsync(any(User.class));
}
```

### ✅ Test Case 3: Quên Mật Khẩu (`forgotPassword`)

Test case này xử lý logic gửi email đồng bộ (Sync) khác với đăng ký.

```java
@Test
void forgotPassword_ValidEmail_SendsResetEmail() {
    // 1. CHUẨN BỊ
    // Giả lập hành động gửi email sẽ thành công (doNothing)
    doNothing().when(emailService).sendPasswordResetEmailSync(
            eq("test@example.com"),
            anyString(), // Token là ngẫu nhiên nên chấp nhận bất kỳ chuỗi nào
            eq("http://localhost:3000"));

    // 2. THỰC THI
    authService.forgotPassword(request);

    // 3. KIỂM TRA
    verify(emailService, times(1)).sendPasswordResetEmailSync(...);
}
```

---

## 3. Tại Sao Cần `Strictness.LENIENT`?

```java
@MockitoSettings(strictness = Strictness.LENIENT)
```

**Lý do:**

- Trong code `login()` thật, có đoạn code Debug kiểm tra user trước khi xác thực.
- Để test chạy qua đoạn Debug này, ta cần mock `userRepository.findBy...`.
- Tuy nhiên, Mockito mặc định rất khắt khe (Strict). Nếu ta mock một hàm mà sau đó lỡ code chạy vào nhánh khác không dùng đến mock đó, nó sẽ báo lỗi `UnnecessaryStubbingException`.
- **Giải pháp:** Dùng chế độ `LENIENT` (Dễ tính) để Mockito bỏ qua các cảnh báo này, giúp chúng ta thoải mái setup dữ liệu test đầy đủ nhất.

---

## 4. Tổng Kết Giá Trị

Bộ Test này đảm bảo 3 yếu tố cốt lõi của tính năng Auth:

1.  **Logic Nghiệp vụ:** Đảm bảo đúng luật (mật khẩu khớp, user tồn tại...).
2.  **Tích hợp thành phần:** Đảm bảo Service nói chuyện đúng với `Repository` và `EmailService`.
3.  **Xử lý ngoại lệ:** Đảm bảo hệ thống không chết khi gặp dữ liệu sai (Login sai pass, Đăng ký trùng email...).

---

## 5. Chi Tiết Test Cases Theo Chức Năng

Dưới đây là bảng phân tích chi tiết từng test case, được chia theo chức năng cụ thể để dễ theo dõi.

### 5.1 Test Cases: Chức Năng Đăng Nhập (Login)

| STT | Tên Test Case (Method)                       | Dữ Liệu Input (Given)       | Kết Quả Mong Đợi (Then)              | Trạng Thái |
| :-- | :------------------------------------------- | :-------------------------- | :----------------------------------- | :--------- |
| 1   | `login_ValidCredentials_ReturnsAuthResponse` | Username & Password ĐÚNG    | Trả về `AuthResponse` chứa JWT Token | ✅ Pass    |
| 2   | `login_InvalidCredentials_ThrowsException`   | Username đúng, Password SAI | Ném lỗi `BadCredentialsException`    | ✅ Pass    |

### 5.2 Test Cases: Chức Năng Đăng Ký (Register)

| STT | Tên Test Case (Method)                      | Dữ Liệu Input (Given)           | Kết Quả Mong Đợi (Then)                         | Trạng Thái |
| :-- | :------------------------------------------ | :------------------------------ | :---------------------------------------------- | :--------- |
| 1   | `register_ValidData_ReturnsAuthResponse`    | Thông tin hợp lệ (chưa tồn tại) | Tạo User mới + Trả Token + Gửi Email Async      | ✅ Pass    |
| 2   | `register_PasswordMismatch_ThrowsException` | Password != Confirm Password    | Ném lỗi `BusinessException` (PASSWORD_MISMATCH) | ✅ Pass    |
| 3   | `register_UsernameExists_ThrowsException`   | Username đã tồn tại             | Ném lỗi `ResourceAlreadyExistsException`        | ✅ Pass    |
| 4   | `register_EmailExists_ThrowsException`      | Email đã tồn tại                | Ném lỗi `ResourceAlreadyExistsException`        | ✅ Pass    |
| 5   | `register_PhoneExists_ThrowsException`      | Số điện thoại đã tồn tại        | Ném lỗi `ResourceAlreadyExistsException`        | ✅ Pass    |

### 5.3 Test Cases: Chức Năng Quên Mật Khẩu (Forgot Password)

| STT | Tên Test Case (Method)                      | Dữ Liệu Input (Given)        | Kết Quả Mong Đợi (Then)                  | Trạng Thái |
| :-- | :------------------------------------------ | :--------------------------- | :--------------------------------------- | :--------- |
| 1   | `forgotPassword_ValidEmail_SendsResetEmail` | Email tồn tại trong hệ thống | Tạo Token Reset + Gọi hàm gửi Email Sync | ✅ Pass    |
