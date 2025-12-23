# Hướng dẫn hoàn chỉnh: Đăng ký, Đăng nhập & Quên mật khẩu

Tài liệu này hướng dẫn bạn cách kiểm tra toàn bộ luồng xác thực (Authentication) của hệ thống Flower Manager.

---

## 1. Cấu hình Email (Thật và Ảo)

### Cách 1: Sử dụng Email Ảo (Mailtrap - Khuyên dùng khi phát triển)
Mailtrap là một công cụ tuyệt vời để bắt email mà không cần gửi đến người nhận thật.
1. Đăng ký tại: [mailtrap.io](https://mailtrap.io/)
2. Vào **Inboxes** -> **SMTP Settings** -> Chọn **Spring Boot** trong danh sách tích hợp.
3. Copy các thông số dán vào `application.properties`:
```properties
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=your_mailtrap_id
spring.mail.password=your_mailtrap_password
```

### Cách 2: Sử dụng Email Thật (Gmail)
1. Bật xác thực 2 lớp cho Gmail của bạn.
2. Tạo **Mật khẩu ứng dụng (App Password)** tại [Google Account Security](https://myaccount.google.com/apppasswords).
3. Cấu hình:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-character-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 2. Quy trình kiểm tra (Step-by-Step)

### Bước 1: Đăng ký thành viên mới
**API**: `POST /api/auth/register`
- **Body**: 
```json
{
    "username": "testuser",
    "email": "testuser@example.com",
    "phoneNumber": "0912345678",
    "password": "Password123!",
    "confirmPassword": "Password123!"
}
```
- **Kết quả**: Server trả về JWT Token và thông tin User.

### Bước 2: Đăng nhập
**API**: `POST /api/auth/login`
- **Body**:
```json
{
    "identifier": "testuser",
    "password": "Password123!"
}
```
- **Lưu ý**: Bạn có thể dùng `username`, `email` hoặc `phoneNumber` làm `identifier`.

### Bước 3: Quên mật khẩu
**API**: `POST /api/auth/forgot-password`
- **Body**:
```json
{
    "email": "testuser@example.com"
}
```
- **Kết quả**: 
    - Server tạo token, lưu vào bảng `password_reset_tokens`.
    - Gửi email chứa link có dạng: `http://localhost:3000/reset-password?token=xxxx&email=yyyy`

### Bước 4: Kiểm tra Email (Ảo hoặc Thật)
- Mở Mailtrap hoặc Gmail để lấy token từ đường link.
- **Mẹo**: Nếu chưa có frontend, hãy copy giá trị `token` từ link trong email.

### Bước 5: Đặt lại mật khẩu mới
**API**: `POST /api/auth/reset-password`
- **Body**:
```json
{
    "token": "TOKEN_LẤY_TỪ_EMAIL",
    "email": "testuser@example.com",
    "newPassword": "NewPassword456!",
    "confirmPassword": "NewPassword456!"
}
```

### Bước 6: Thử đăng nhập lại với mật khẩu mới
- Lặp lại Bước 2 nhưng dùng mật khẩu `NewPassword456!`.

---

## 3. Kiểm tra dưới Database (Nếu cần)
Bạn có thể xem token và trạng thái sử dụng bằng câu lệnh:
```sql
SELECT * FROM password_reset_tokens ORDER BY created_at DESC;
```
Trường `used` sẽ chuyển từ `0 (false)` sang `1 (true)` sau khi bạn đổi mật khẩu thành công.
