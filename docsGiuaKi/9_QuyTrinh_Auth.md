# CHƯƠNG 8: QUY TRÌNH XỬ LÝ AUTHENTICATION (ĐĂNG KÝ - ĐĂNG NHẬP)

Tài liệu này mô tả chi tiết luồng dữ liệu và logic xử lý của hệ thống Authentication, từ lúc người dùng gửi yêu cầu đến khi nhận được kết quả (Token/Error).

---

## 1. Lưu Đồ Tổng Quan (Kiến Trúc)

Quy trình Auth đi qua các lớp sau:

1.  **Client (FE/Postman)**: Gửi request JSON.
2.  **Controller (`AuthController`)**: Tiếp nhận request, gọi Service.
3.  **Service (`AuthServiceImpl`)**: Xử lý logic nghiệp vụ chính.
4.  **Security Core**:
    - `AuthenticationManager`: Quản lý việc xác thực.
    - `PasswordEncoder`: Mã hóa/Kiểm tra mật khẩu.
    - `JwtUtils`: Tạo/Kiểm tra Token.
5.  **Repository (`UserRepository`)**: Truy xuất DB.
6.  **Email Service (`AsyncEmailService`)**: Gửi email xác thực/reset pass.

---

## 2. Phân Tích Luồng Đăng Nhập (Login Flow)

**Mục đích:** Xác thực người dùng và cấp phát JWT Token.

### Bước 1: Tiếp Nhận Request

- **API:** `POST /api/auth/login`
- **Body:** `{ "identifier": "user/email/phone", "password": "..." }`
- **Controller:** `AuthController.login()` nhận request, gọi `authService.login()`.

### Bước 2: Xử Lý Logic (Service)

- **File:** `AuthServiceImpl.java` -> `login(LoginRequest request)`

**Chi tiết các bước logic:**

1.  **Chuẩn hóa dữ liệu:** Trim khoảng trắng của identifier.
2.  **Xác thực (Authenticate):**
    - Dùng `authenticationManager.authenticate(...)`.
    - Spring Security sẽ tự động tìm User trong DB (qua `CustomUserDetailsService`) và so sánh Hash Password.
    - Nếu sai mật khẩu -> Ném lỗi `BadCredentialsException`.
    - Nếu tài khoản bị khóa -> Ném lỗi `DisabledException`.
3.  **Lưu Context:** Nếu thành công, lưu thông tin User vào `SecurityContextHolder` (cho session hiện tại).
4.  **Tạo Token:** Gọi `jwtUtils.generateJwtToken(authentication)` để tạo chuỗi JWT.

### Bước 3: Trả Về Kết Quả

- Controller đóng gói Token vào:
  1.  **Body response:** Để Mobile App/Client dùng.
  2.  **HttpOnly Cookie:** Để Web App bảo mật hơn (tránh XSS).
- **Kết quả:** `AuthResponse` chứa thông tin User + Token.

---

## 3. Phân Tích Luồng Đăng Ký (Register Flow)

**Mục đích:** Tạo tài khoản mới và gửi email xác thực.

### Bước 1: Tiếp Nhận Request

- **API:** `POST /api/auth/register`
- **Body:** `{ username, email, phoneNumber, password, confirmPassword }`

### Bước 2: Validate Dữ Liệu (Quan Trọng)

Logic kiểm tra rất chặt chẽ tại `AuthServiceImpl.register()`:

1.  **Check Password Match:** `password` phải giống `confirmPassword`.
2.  **Check Trùng Lặp (Duplicate):**
    - `existsByUsername(username)`: Tên đăng nhập đã có chưa?
    - `existsByEmail(email)`: Email đã đăng ký chưa?
    - `existsByPhoneNumber(phone)`: Số điện thoại đã dùng chưa?
    - _Nếu trùng -> Ném lỗi `ResourceAlreadyExistsException` (Mã 409)._

### Bước 3: Tạo và Lưu User

1.  **Mã hóa mật khẩu:** Dùng `passwordEncoder.encode(password)` (BCrypt).
2.  **Khởi tạo User:**
    - Role mặc định: `CUSTOMER`.
    - Status: `isActive = true`.
    - `emailVerified = false`.
3.  **Lưu DB:** `userRepository.save(user)`.

### Bước 4: Tự Động Đăng Nhập & Gửi Email

Khác với các hệ thống thường, hệ thống này hỗ trợ người dùng **dùng ngay** sau khi đăng ký:

1.  **Auto Login:** Hệ thống tự gọi quy trình Login (như mục 2) để cấp Token ngay lập tức cho user mới.
2.  **Async Email:** Gọi `asyncEmailService.sendVerificationEmailAsync(user)` để gửi email chào mừng/xác thực dưới nền (không làm user phải chờ lâu).

---

## 4. Các Tính Năng Phụ Trợ

| Tính Năng         | Logic Chính                                                                                                                                |
| :---------------- | :----------------------------------------------------------------------------------------------------------------------------------------- |
| **Quên Mật Khẩu** | 1. Check Email tồn tại & Active.<br>2. Tạo Token Random (UUID).<br>3. Gửi Email chứa Link Reset (Sync mode để báo lỗi ngay nếu mail hỏng). |
| **Đổi Mật Khẩu**  | 1. Check Pass cũ (nếu là user thường).<br>2. Check Pass mới != Pass cũ.<br>3. Hash và lưu Pass mới.                                        |
| **User Hiện Tại** | Lấy thông tin từ `SecurityContextHolder` -> Query DB lấy dữ liệu mới nhất.                                                                 |

---

## 5. Tổng Kết

Quy trình Auth được thiết kế tối ưu trải nghiệm (Auto Login, Async Email) nhưng vẫn đảm bảo bảo mật cao (HttpOnly Cookie, BCrypt, Validate chặt chẽ).
