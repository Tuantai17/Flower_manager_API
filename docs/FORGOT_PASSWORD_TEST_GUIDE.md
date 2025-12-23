# Hướng dẫn Test Chức năng Quên Mật Khẩu với Postman

## Tổng quan Flow
```
1️⃣ User nhập email → gọi API /auth/forgot-password
2️⃣ Backend kiểm tra email tồn tại
3️⃣ Backend tạo token + expiry (30 phút)
4️⃣ Lưu token DB (bảng password_reset_tokens)
5️⃣ Gửi email chứa link reset
6️⃣ User bấm link → FE gửi token + password mới → /auth/reset-password
7️⃣ Backend kiểm tra token hợp lệ (chưa dùng, chưa hết hạn)
8️⃣ Hash password mới và update user
9️⃣ Xoá token (đánh dấu đã dùng)

🔐 Token chỉ dùng 1 lần
⏳ Token có hạn 30 phút
```

---

## BƯỚC 1: Yêu cầu Quên mật khẩu

### Endpoint
```
POST http://localhost:8080/api/auth/forgot-password
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
    "email": "your-email@example.com"
}
```

### Response thành công (200)
```json
{
    "success": true,
    "message": "Đã gửi email hướng dẫn đặt lại mật khẩu đến your-email@example.com. Vui lòng kiểm tra hộp thư (bao gồm cả thư rác).",
    "token": null,
    "user": null
}
```

### Response lỗi - Email không tồn tại (404)
```json
{
    "success": false,
    "message": "Không tìm thấy tài khoản với email: wrong@example.com"
}
```

---

## BƯỚC 2: Lấy Token (Cho mục đích test)

Vì email có thể chưa cấu hình đúng, bạn có thể lấy token trực tiếp từ database:

### Query MySQL
```sql
SELECT token, expiry_date, used 
FROM password_reset_tokens 
WHERE user_id = (SELECT id FROM users WHERE email = 'your-email@example.com')
ORDER BY created_at DESC 
LIMIT 1;
```

---

## BƯỚC 3: Đặt lại mật khẩu

### Endpoint
```
POST http://localhost:8080/api/auth/reset-password
```

### Headers
```
Content-Type: application/json
```

### Body (JSON)
```json
{
    "token": "TOKEN_FROM_EMAIL_OR_DB",
    "email": "your-email@example.com",
    "newPassword": "NewPassword123!",
    "confirmPassword": "NewPassword123!"
}
```

### Response thành công (200)
```json
{
    "success": true,
    "message": "Đặt lại mật khẩu thành công. Vui lòng đăng nhập với mật khẩu mới.",
    "token": null,
    "user": null
}
```

### Response lỗi - Token không hợp lệ
```json
{
    "success": false,
    "message": "Token không hợp lệ hoặc không tồn tại",
    "errorCode": "INVALID_TOKEN"
}
```

### Response lỗi - Token đã hết hạn
```json
{
    "success": false,
    "message": "Token đã hết hạn. Vui lòng yêu cầu đặt lại mật khẩu mới.",
    "errorCode": "TOKEN_EXPIRED"
}
```

### Response lỗi - Token đã sử dụng
```json
{
    "success": false,
    "message": "Token đã được sử dụng. Vui lòng yêu cầu đặt lại mật khẩu mới.",
    "errorCode": "TOKEN_USED"
}
```

### Response lỗi - Mật khẩu không khớp
```json
{
    "success": false,
    "message": "Mật khẩu xác nhận không khớp",
    "errorCode": "PASSWORD_MISMATCH"
}
```

---

## BƯỚC 4: Đăng nhập với mật khẩu mới

### Endpoint
```
POST http://localhost:8080/api/auth/login
```

### Body (JSON)
```json
{
    "identifier": "your-email@example.com",
    "password": "NewPassword123!"
}
```

---

## Cấu hình Email (Quan trọng!)

Để gửi email thực sự, bạn cần cập nhật file `application.properties`:

```properties
# ===============================
# EMAIL (Gmail example)
# ===============================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-gmail@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### Cách lấy App Password của Gmail:
1. Bật xác thực 2 yếu tố (2FA) trong tài khoản Google
2. Truy cập: https://myaccount.google.com/apppasswords
3. Chọn "Mail" và tạo App Password
4. Copy password 16 ký tự và dán vào `spring.mail.password`

---

## Database Schema

Table `password_reset_tokens` sẽ được tự động tạo:

```sql
CREATE TABLE password_reset_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(100) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    expiry_date DATETIME NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Test Cases (Postman Collection)

### TC1: Quên mật khẩu - Email hợp lệ
- Input: email đã đăng ký
- Expected: success = true

### TC2: Quên mật khẩu - Email không tồn tại
- Input: email chưa đăng ký
- Expected: 404 Not Found

### TC3: Reset password - Token hợp lệ
- Input: token đúng, email đúng, password hợp lệ
- Expected: success = true

### TC4: Reset password - Token sai
- Input: token ngẫu nhiên
- Expected: INVALID_TOKEN

### TC5: Reset password - Token đã dùng
- Input: token đã reset trước đó
- Expected: TOKEN_USED

### TC6: Reset password - Mật khẩu không khớp
- Input: newPassword ≠ confirmPassword
- Expected: PASSWORD_MISMATCH

### TC7: Đăng nhập với mật khẩu mới
- Input: email + mật khẩu mới
- Expected: login thành công
