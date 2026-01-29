# 📘 HƯỚNG DẪN DEPLOY BACKEND LÊN RENDER.COM (Miễn Phí)

> **Render.com** là nền tảng cloud hosting miễn phí cho web services.  
> Free tier: 750 giờ/tháng, auto-deploy từ GitHub.  
> Hướng dẫn này dành cho người mới, từng bước chi tiết.

---

## 📋 MỤC LỤC

1. [Chuẩn bị trước khi deploy](#1-chuẩn-bị-trước-khi-deploy)
2. [Đăng ký tài khoản Render](#2-đăng-ký-tài-khoản-render)
3. [Push code lên GitHub](#3-push-code-lên-github)
4. [Tạo Web Service trên Render](#4-tạo-web-service-trên-render)
5. [Cấu hình Environment Variables](#5-cấu-hình-environment-variables)
6. [Deploy và theo dõi](#6-deploy-và-theo-dõi)
7. [Kiểm tra sau deploy](#7-kiểm-tra-sau-deploy)
8. [Xử lý lỗi thường gặp](#8-xử-lý-lỗi-thường-gặp)

---

## 1. CHUẨN BỊ TRƯỚC KHI DEPLOY

### 1.1. Kiểm tra các file cần thiết

**Thao tác:** Đảm bảo project có các file sau:

```
flower-manager/
├── Dockerfile                    ✅ Bắt buộc (đã có)
├── pom.xml                       ✅ Bắt buộc (đã có)
├── src/
│   └── main/
│       └── resources/
│           ├── application.properties        ✅ (đã có)
│           └── application-prod.properties   ✅ (đã tạo)
└── .gitignore                    ✅ Kiểm tra có bỏ qua .env
```

### 1.2. Kiểm tra Dockerfile

**File:** `Dockerfile` (đã có sẵn trong project)

```dockerfile
# Xác nhận Dockerfile có nội dung tương tự này
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
# ... build steps ...

FROM eclipse-temurin:21-jre-alpine
# ... runtime config ...
ENV SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 1.3. Kiểm tra .gitignore

**File:** `.gitignore`

**Đảm bảo có các dòng sau:**

```gitignore
# Environment files (KHÔNG push lên GitHub)
.env
.env.local
.env.production

# Build outputs
target/
*.jar

# IDE
.idea/
*.iml
.vscode/
```

---

## 2. ĐĂNG KÝ TÀI KHOẢN RENDER

### Bước 2.1: Truy cập Render.com

**Lệnh:** Mở trình duyệt và truy cập:

```
https://render.com/
```

### Bước 2.2: Đăng ký tài khoản

**Thao tác:**

```
1. Click nút "Get Started for Free" (góc phải trên)
2. Chọn phương thức đăng ký:
   ✅ GitHub (KHUYẾN NGHỊ - để auto-deploy)
   ✅ GitLab
   ✅ Google
   ✅ Email

3. Nếu chọn GitHub:
   - Click "Continue with GitHub"
   - Authorize Render (cho phép truy cập repositories)
   - Điền thông tin nếu được yêu cầu
```

### Bước 2.3: Xác nhận email (nếu cần)

**Thao tác:**

```
1. Kiểm tra email để xác nhận tài khoản
2. Click link xác nhận trong email
3. Quay lại Render Dashboard
```

**Kiểm tra thành công:**

```
✓ Thấy Render Dashboard với menu bên trái
✓ Có nút "+ New" ở góc phải trên
```

---

## 3. PUSH CODE LÊN GITHUB

### Bước 3.1: Tạo repository mới trên GitHub

**Thao tác:**

```
1. Truy cập: https://github.com/new
2. Điền thông tin:
   - Repository name: flower-manager-api
   - Description: Spring Boot API for Flower Shop
   - Visibility: Public (hoặc Private nếu muốn)
   - ⚠️ KHÔNG tick "Add a README file" (sẽ conflict)

3. Click "Create repository"
```

### Bước 3.2: Push code từ local lên GitHub

**Lệnh (PowerShell):**

```powershell
# Di chuyển vào thư mục project
cd E:\DeAn_Java_Flowers\flower-manager

# Kiểm tra git status
git status

# Nếu chưa có git, khởi tạo:
git init

# Thêm tất cả files
git add .

# Commit
git commit -m "Prepare for Render deployment"

# Thêm remote (thay YOUR_USERNAME bằng username GitHub của bạn)
git remote add origin https://github.com/YOUR_USERNAME/flower-manager-api.git

# Hoặc nếu đã có remote, đổi URL:
git remote set-url origin https://github.com/YOUR_USERNAME/flower-manager-api.git

# Push lên GitHub
git branch -M main
git push -u origin main
```

**Nếu gặp lỗi authentication:**

```powershell
# Dùng Personal Access Token thay vì password
# 1. GitHub Settings → Developer settings → Personal access tokens
# 2. Generate new token (classic)
# 3. Chọn scope: repo
# 4. Copy token và dùng làm password khi push
```

**Kiểm tra thành công:**

```
1. Mở https://github.com/YOUR_USERNAME/flower-manager-api
2. Thấy code đã được push lên
3. Có file Dockerfile, pom.xml, src/
```

---

## 4. TẠO WEB SERVICE TRÊN RENDER

### Bước 4.1: Tạo Web Service mới

**Thao tác:**

```
1. Render Dashboard → Click "+ New" (góc phải trên)
2. Chọn "Web Service"
```

### Bước 4.2: Kết nối với GitHub Repository

**Thao tác:**

```
1. Chọn tab "Build and deploy from a Git repository"
2. Click "Next"
3. Tìm repository "flower-manager-api"
   - Nếu không thấy: Click "Configure account" → Cho phép Render truy cập repo
4. Click "Connect" bên cạnh repository
```

### Bước 4.3: Cấu hình Web Service

**Thao tác điền thông tin:**

| Field             | Giá trị                      | Ghi chú                                     |
| ----------------- | ---------------------------- | ------------------------------------------- |
| **Name**          | `flower-manager-api`         | Tên service (sẽ thành URL)                  |
| **Region**        | `Singapore (Southeast Asia)` | ✅ Gần Việt Nam nhất                        |
| **Branch**        | `main`                       | Branch để deploy                            |
| **Runtime**       | `Docker`                     | ⚠️ QUAN TRỌNG: Chọn Docker vì có Dockerfile |
| **Instance Type** | `Free`                       | Chọn Free tier                              |

```
⚠️ LƯU Ý QUAN TRỌNG:
- Runtime PHẢI là "Docker" (không phải Native)
- Render sẽ tự động detect và build từ Dockerfile
```

### Bước 4.4: CHƯA CLICK "Create Web Service"

**Thao tác:**

```
DỪNG LẠI! Cần thêm Environment Variables trước khi deploy.
Scroll xuống phần "Environment Variables" hoặc click "Advanced"
```

---

## 5. CẤU HÌNH ENVIRONMENT VARIABLES

### Bước 5.1: Thêm các biến môi trường bắt buộc

**Thao tác:**

```
1. Trong trang cấu hình Web Service
2. Scroll xuống phần "Environment Variables"
3. Click "Add Environment Variable" để thêm từng biến
```

**Danh sách biến cần thêm:**

| Key                      | Value                                                                                                               | Mô tả                        |
| ------------------------ | ------------------------------------------------------------------------------------------------------------------- | ---------------------------- |
| `SPRING_PROFILES_ACTIVE` | `prod`                                                                                                              | Kích hoạt profile production |
| `DB_URL`                 | `jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/java_flower?sslMode=VERIFY_IDENTITY&useSSL=true` | TiDB Cloud connection        |
| `DB_USERNAME`            | `33pdfJX7R4Ajcx6.root`                                                                                              | TiDB username                |
| `DB_PASSWORD`            | `Bj9T8Ac2vGI6gS48`                                                                                                  | TiDB password                |
| `JWT_SECRET`             | `YourVeryLongSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLongForHS256Algorithm`                               | JWT secret key               |
| `APP_FRONTEND_URL`       | `https://flower-shop.vercel.app`                                                                                    | URL Frontend (cập nhật sau)  |

### Bước 5.2: Thêm các biến môi trường tùy chọn (nếu cần)

| Key                     | Value                                                                      | Mô tả              |
| ----------------------- | -------------------------------------------------------------------------- | ------------------ |
| `CLOUDINARY_CLOUD_NAME` | `db1b15yn4`                                                                | Cloudinary config  |
| `CLOUDINARY_API_KEY`    | `783928139148693`                                                          | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | `uLWkqSzOEXku4qLRUJWd9qbiv5Y`                                              | Cloudinary secret  |
| `MAIL_USERNAME`         | `tuantainguyen13579@gmail.com`                                             | Email gửi mail     |
| `MAIL_PASSWORD`         | `qqjajdkasamectfq`                                                         | Gmail app password |
| `GEMINI_API_KEY`        | `AIzaSyCoS0MZ-yRFaXaXgMbPL4acIptL2U31pt4`                                  | Gemini AI key      |
| `GOOGLE_CLIENT_ID`      | `418199736625-gu3djsvnrd31hj7rid3eg25ivic6a0dd.apps.googleusercontent.com` | Google OAuth       |

### Bước 5.3: Copy nhanh (Secret File)

**Cách khác - Dùng Secret Files:**

```
1. Click "Add Secret File"
2. Filename: .env
3. Contents: (paste nội dung bên dưới)
```

```env
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/java_flower?sslMode=VERIFY_IDENTITY&useSSL=true
DB_USERNAME=33pdfJX7R4Ajcx6.root
DB_PASSWORD=Bj9T8Ac2vGI6gS48
JWT_SECRET=YourVeryLongSecretKeyForJWTTokenGenerationMustBeAtLeast256BitsLongForHS256Algorithm
APP_FRONTEND_URL=https://flower-shop.vercel.app
CLOUDINARY_CLOUD_NAME=db1b15yn4
CLOUDINARY_API_KEY=783928139148693
CLOUDINARY_API_SECRET=uLWkqSzOEXku4qLRUJWd9qbiv5Y
MAIL_USERNAME=tuantainguyen13579@gmail.com
MAIL_PASSWORD=qqjajdkasamectfq
GEMINI_API_KEY=AIzaSyCoS0MZ-yRFaXaXgMbPL4acIptL2U31pt4
GOOGLE_CLIENT_ID=418199736625-gu3djsvnrd31hj7rid3eg25ivic6a0dd.apps.googleusercontent.com
```

---

## 6. DEPLOY VÀ THEO DÕI

### Bước 6.1: Bắt đầu Deploy

**Thao tác:**

```
1. Kiểm tra lại tất cả cấu hình
2. Click "Create Web Service" (nút màu xanh ở dưới cùng)
3. Đợi Render bắt đầu build
```

### Bước 6.2: Theo dõi quá trình Build

**Thao tác:**

```
1. Trang sẽ chuyển sang "Logs" tab
2. Theo dõi quá trình build:
   - Pulling Docker base image...
   - Downloading dependencies...
   - Building JAR...
   - Creating runtime image...

3. Thời gian build lần đầu: 5-15 phút
```

**Các log cần chú ý:**

```
==> Building image...
==> Uploading build...
==> Build successful 🎉
==> Deploying...
==> Your service is live 🎉
```

### Bước 6.3: Chờ service khởi động

**Thao tác:**

```
1. Sau khi deploy xong, service cần thời gian khởi động
2. Theo dõi logs để thấy Spring Boot starting
3. Tìm dòng: "Started FlowerManagerApplication in X seconds"
```

**Log thành công:**

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
...
Started FlowerManagerApplication in 15.234 seconds
Tomcat started on port 8080
```

---

## 7. KIỂM TRA SAU DEPLOY

### Bước 7.1: Lấy URL của service

**Thao tác:**

```
1. Trong Render Dashboard → Service "flower-manager-api"
2. Copy URL ở phía trên, dạng:
   https://flower-manager-api.onrender.com
```

### Bước 7.2: Test các endpoint

**Test 1: API Root**

```
URL: https://flower-manager-api.onrender.com/api
Expected: JSON response với thông tin API
```

**Test 2: Swagger UI**

```
URL: https://flower-manager-api.onrender.com/swagger-ui.html
Expected: Swagger interface hiển thị
```

**Test 3: Products API**

```
URL: https://flower-manager-api.onrender.com/api/products
Expected: Danh sách sản phẩm từ database
```

**Test 4: Health Check (PowerShell)**

```powershell
# Test từ PowerShell
Invoke-RestMethod -Uri "https://flower-manager-api.onrender.com/api" -Method GET

# Hoặc dùng curl
curl https://flower-manager-api.onrender.com/api
```

### Bước 7.3: Kiểm tra kết nối Database

**Trong Render Logs, tìm:**

```
HikariPool-1 - Start completed.
```

Nếu thấy dòng này → Database kết nối OK ✅

---

## 8. XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi 1: Build failed - "mvnw: Permission denied"

**Nguyên nhân:** File mvnw không có quyền execute

**Giải pháp:** Đã fix trong Dockerfile với:

```dockerfile
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw
```

### Lỗi 2: "Port 10000 is already in use" hoặc port binding

**Nguyên nhân:** Render dùng port động, không phải 8080

**Giải pháp:** Đảm bảo application-prod.properties có:

```properties
server.port=${PORT:8080}
```

### Lỗi 3: Database connection failed

**Nguyên nhân:** Sai connection string hoặc credentials

**Giải pháp:**

```
1. Kiểm tra DB_URL có đúng format không
2. Kiểm tra DB_USERNAME và DB_PASSWORD
3. Đảm bảo TiDB Cluster đang "Available"
4. Kiểm tra IP whitelist trên TiDB (nếu có)
```

### Lỗi 4: Service keeps restarting

**Nguyên nhân:** Application crash hoặc OOM

**Giải pháp:**

```
1. Kiểm tra Logs để tìm error
2. Nếu OutOfMemory → Reduce heap size:
   - Thêm env var: JAVA_OPTS=-Xmx256m -Xms128m
```

### Lỗi 5: Deploy timeout

**Nguyên nhân:** Build quá lâu (>15 phút)

**Giải pháp:**

```
1. Kiểm tra Dockerfile có multi-stage build không
2. Xóa target/ và .m2/ cache nếu có trong git
3. Thử lại bằng cách click "Manual Deploy" → "Deploy latest commit"
```

### Lỗi 6: CORS blocked từ Frontend

**Nguyên nhân:** Frontend URL chưa được whitelist

**Giải pháp:**

```
1. Cập nhật APP_FRONTEND_URL = URL thực của Vercel
2. Redeploy service
```

---

## ⏰ THÔNG TIN FREE TIER

| Tính năng         | Giới hạn                             |
| ----------------- | ------------------------------------ |
| **Web Services**  | 750 giờ/tháng (tổng tất cả services) |
| **Bandwidth**     | 100 GB/tháng                         |
| **Build minutes** | 500 phút/tháng                       |
| **Auto-sleep**    | Sau 15 phút không có request         |
| **Cold start**    | 30-50 giây sau khi wake up           |

> 💡 **Tip:** Free tier sẽ "spin down" sau 15 phút idle. Request đầu tiên sau đó sẽ mất 30-50 giây để "cold start".

---

## ✅ CHECKLIST HOÀN THÀNH

- [ ] Đăng ký tài khoản Render.com
- [ ] Push code lên GitHub
- [ ] Tạo Web Service mới
- [ ] Chọn Runtime = Docker
- [ ] Chọn Region = Singapore
- [ ] Thêm tất cả Environment Variables
- [ ] Click "Create Web Service"
- [ ] Build thành công (xanh lá)
- [ ] Test API endpoint hoạt động
- [ ] Kiểm tra Swagger UI

---

## 🔗 LIÊN KẾT HỮU ÍCH

- [Render Documentation](https://render.com/docs)
- [Render Docker Deploy Guide](https://render.com/docs/docker)
- [Render Environment Variables](https://render.com/docs/environment-variables)
- [Render Free Plan Limits](https://render.com/docs/free)

---

## 📝 SAU KHI DEPLOY THÀNH CÔNG

**Lưu lại thông tin:**

```
Backend URL: https://flower-manager-api.onrender.com
Swagger UI:  https://flower-manager-api.onrender.com/swagger-ui.html
API Base:    https://flower-manager-api.onrender.com/api
```

**Bước tiếp theo:**

1. Cập nhật `.env.production` của Frontend với Backend URL mới
2. Deploy Frontend lên Vercel
3. Quay lại Render → Cập nhật `APP_FRONTEND_URL` với URL Vercel

---

> **Tổng thời gian deploy:** ~15-20 phút (lần đầu)
