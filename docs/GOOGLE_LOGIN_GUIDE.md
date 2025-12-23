# Hướng dẫn Test Google Login

## 1. Cấu trúc đã triển khai

### Files mới
| File | Mô tả |
|------|-------|
| `dto/auth/GoogleLoginRequest.java` | DTO chứa idToken từ Frontend |
| `dto/auth/GoogleUserInfo.java` | DTO chứa thông tin user từ Google |
| `service/auth/GoogleAuthService.java` | Logic xác thực token và tạo/tìm user |
| `controller/auth/GoogleAuthController.java` | Endpoint `/api/auth/google` |

---

## 2. Flow hoạt động

```
Frontend (React)                    Backend (Spring Boot)                 Google
     |                                        |                              |
     |-- 1. User click "Login with Google" -->|                              |
     |                                        |                              |
     |<-- 2. Google popup opens --------------|----> 3. User logs in ------->|
     |                                        |                              |
     |<-- 4. Receive idToken -----------------|<---- 5. Google returns ------|
     |                                        |                              |
     |-- 6. POST /api/auth/google ----------->|                              |
     |    { "idToken": "..." }                |                              |
     |                                        |-- 7. Verify token ---------->|
     |                                        |<-- 8. Token info ------------|
     |                                        |                              |
     |                                        |-- 9. Find/Create user in DB  |
     |                                        |-- 10. Generate JWT           |
     |                                        |                              |
     |<-- 11. Return { token, user } ---------|                              |
```

---

## 3. API Endpoint

### POST `/api/auth/google`

**Request:**
```json
{
    "idToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6Ij..."
}
```

**Response thành công:**
```json
{
    "success": true,
    "message": "Đăng nhập bằng Google thành công",
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "user": {
        "id": 5,
        "username": "johndoe",
        "email": "johndoe@gmail.com",
        "fullName": "John Doe",
        "role": "CUSTOMER",
        "isActive": true
    }
}
```

**Response lỗi:**
```json
{
    "success": false,
    "message": "Token Google không hợp lệ",
    "errorCode": "INVALID_GOOGLE_TOKEN"
}
```

---

## 4. Test với Postman (Thủ công)

### Bước 1: Lấy Google ID Token

Để test thủ công, bạn có thể dùng trang này để lấy token:

1. Truy cập: https://developers.google.com/oauthplayground/
2. Chọn "Google OAuth2 API v2" → "https://www.googleapis.com/auth/userinfo.email"
3. Click "Authorize APIs" và đăng nhập
4. Click "Exchange authorization code for tokens"
5. Copy `id_token` từ response

### Bước 2: Gọi API

```
POST http://localhost:8080/api/auth/google
Content-Type: application/json

{
    "idToken": "<paste-id-token-here>"
}
```

---

## 5. Tích hợp Frontend (React)

### Cài đặt thư viện
```bash
npm install @react-oauth/google
```

### Code mẫu
```jsx
// App.jsx hoặc main.jsx
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  return (
    <GoogleOAuthProvider clientId="418199736625-gu3djsvnrd31hj7rid3eg25ivic6a0dd.apps.googleusercontent.com">
      <YourApp />
    </GoogleOAuthProvider>
  );
}
```

```jsx
// LoginPage.jsx
import { GoogleLogin } from '@react-oauth/google';

function LoginPage() {
  const handleGoogleSuccess = async (credentialResponse) => {
    try {
      const response = await fetch('http://localhost:8080/api/auth/google', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ idToken: credentialResponse.credential })
      });
      
      const data = await response.json();
      
      if (data.success) {
        localStorage.setItem('token', data.token);
        // Redirect to homepage
      }
    } catch (error) {
      console.error('Login failed:', error);
    }
  };

  return (
    <div>
      <h1>Đăng nhập</h1>
      <GoogleLogin
        onSuccess={handleGoogleSuccess}
        onError={() => console.log('Login Failed')}
      />
    </div>
  );
}
```

---

## 6. Lưu ý bảo mật

- ✅ Client ID đã được cấu hình trong `application.properties`
- ✅ Token được verify thực sự với Google (không decode đơn thuần)
- ✅ User mới được tạo với password ngẫu nhiên (không thể login bằng password)
- ✅ Email phải được xác minh bởi Google mới cho phép đăng nhập
