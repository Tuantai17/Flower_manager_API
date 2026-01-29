# 📘 HƯỚNG DẪN DEPLOY FRONTEND LÊN VERCEL (Miễn Phí)

> **Vercel** là nền tảng hosting tốt nhất cho React/Next.js, miễn phí cho cá nhân.  
> Free tier: Bandwidth 100GB/tháng, auto-deploy từ GitHub.  
> Hướng dẫn này dành cho người mới, từng bước chi tiết.

---

## 📋 MỤC LỤC

1. [Chuẩn bị trước khi deploy](#1-chuẩn-bị-trước-khi-deploy)
2. [Đăng ký tài khoản Vercel](#2-đăng-ký-tài-khoản-vercel)
3. [Push code lên GitHub](#3-push-code-lên-github)
4. [Import project vào Vercel](#4-import-project-vào-vercel)
5. [Cấu hình Environment Variables](#5-cấu-hình-environment-variables)
6. [Deploy và theo dõi](#6-deploy-và-theo-dõi)
7. [Kiểm tra sau deploy](#7-kiểm-tra-sau-deploy)
8. [Cập nhật Backend CORS](#8-cập-nhật-backend-cors)
9. [Xử lý lỗi thường gặp](#9-xử-lý-lỗi-thường-gặp)

---

## 1. CHUẨN BỊ TRƯỚC KHI DEPLOY

### 1.1. Kiểm tra các file cần thiết

**Thao tác:** Đảm bảo project có các file sau:

```
flower-shop-frontend/
├── package.json              ✅ Bắt buộc
├── .env                      ✅ Biến môi trường local
├── .env.production           ✅ Biến môi trường production (đã tạo)
├── public/
│   └── index.html           ✅ Entry HTML
├── src/
│   ├── index.js             ✅ Entry JS
│   └── App.js               ✅ Main component
└── .gitignore               ✅ Kiểm tra có bỏ qua node_modules
```

### 1.2. Kiểm tra .env.production

**File:** `.env.production` (đã được tạo)

```env
# Production API Configuration
REACT_APP_API_URL=https://flower-manager-api.onrender.com/api
REACT_APP_UPLOAD_URL=https://flower-manager-api.onrender.com/api/upload
REACT_APP_WS_URL=https://flower-manager-api.onrender.com/ws/chat

# App Configuration
REACT_APP_NAME=FlowerCorner
REACT_APP_HOTLINE=1900 633 045

# Google OAuth
REACT_APP_GOOGLE_CLIENT_ID=418199736625-gu3djsvnrd31hj7rid3eg25ivic6a0dd.apps.googleusercontent.com
```

### 1.3. Kiểm tra .gitignore

**File:** `.gitignore`

**Đảm bảo có các dòng sau:**

```gitignore
# Dependencies
node_modules/

# Environment files (local only)
.env.local
.env.development.local
.env.test.local
.env.production.local

# Build output
build/
dist/

# IDE
.idea/
.vscode/
```

> ⚠️ **LƯU Ý:** File `.env.production` ĐƯỢC commit lên GitHub (không chứa secrets nhạy cảm).

---

## 2. ĐĂNG KÝ TÀI KHOẢN VERCEL

### Bước 2.1: Truy cập Vercel

**Lệnh:** Mở trình duyệt và truy cập:

```
https://vercel.com/
```

### Bước 2.2: Đăng ký tài khoản

**Thao tác:**

```
1. Click nút "Sign Up" (góc phải trên)
2. Chọn phương thức đăng ký:
   ✅ Continue with GitHub (KHUYẾN NGHỊ - để auto-deploy)
   ✅ Continue with GitLab
   ✅ Continue with Bitbucket
   ✅ Continue with Email

3. Nếu chọn GitHub:
   - Click "Continue with GitHub"
   - Authorize Vercel (cho phép truy cập repositories)
   - Chọn plan: Hobby (Free) cho cá nhân
```

### Bước 2.3: Chọn Plan

**Thao tác:**

```
1. Chọn "Hobby" (miễn phí cho cá nhân)
2. Điền tên (không bắt buộc)
3. Click "Continue"
```

**Kiểm tra thành công:**

```
✓ Thấy Vercel Dashboard
✓ Có nút "Add New..." ở góc phải
```

---

## 3. PUSH CODE LÊN GITHUB

### Bước 3.1: Tạo repository mới trên GitHub

**Thao tác:**

```
1. Truy cập: https://github.com/new
2. Điền thông tin:
   - Repository name: flower-shop-frontend
   - Description: React Frontend for Flower Shop
   - Visibility: Public (hoặc Private)
   - ⚠️ KHÔNG tick "Add a README file"

3. Click "Create repository"
```

### Bước 3.2: Push code từ local lên GitHub

**Lệnh (PowerShell):**

```powershell
# Di chuyển vào thư mục frontend
cd E:\DeAn_Java_Flowers\flower-shop-frontend

# Kiểm tra git status
git status

# Nếu chưa có git, khởi tạo:
git init

# Thêm tất cả files
git add .

# Commit
git commit -m "Prepare for Vercel deployment"

# Thêm remote (thay YOUR_USERNAME bằng username GitHub của bạn)
git remote add origin https://github.com/YOUR_USERNAME/flower-shop-frontend.git

# Hoặc nếu đã có remote, đổi URL:
git remote set-url origin https://github.com/YOUR_USERNAME/flower-shop-frontend.git

# Push lên GitHub
git branch -M main
git push -u origin main
```

**Kiểm tra thành công:**

```
1. Mở https://github.com/YOUR_USERNAME/flower-shop-frontend
2. Thấy code đã được push lên
3. Có file package.json, src/, public/
```

---

## 4. IMPORT PROJECT VÀO VERCEL

### Bước 4.1: Tạo project mới

**Thao tác:**

```
1. Vercel Dashboard → Click "Add New..." → "Project"
2. Hoặc truy cập: https://vercel.com/new
```

### Bước 4.2: Import Git Repository

**Thao tác:**

```
1. Tìm repository "flower-shop-frontend" trong danh sách
   - Nếu không thấy: Click "Adjust GitHub App Permissions"
   - Cho phép truy cập repository cần thiết

2. Click "Import" bên cạnh repository
```

### Bước 4.3: Cấu hình Project

**Thao tác điền thông tin:**

| Field                | Giá trị                | Ghi chú                    |
| -------------------- | ---------------------- | -------------------------- |
| **Project Name**     | `flower-shop-frontend` | Tên project (sẽ thành URL) |
| **Framework Preset** | `Create React App`     | ✅ Vercel tự detect        |
| **Root Directory**   | `./ ` (để trống)       | Vì code ở thư mục gốc      |
| **Build Command**    | `npm run build`        | Mặc định, không cần sửa    |
| **Output Directory** | `build`                | Mặc định cho CRA           |
| **Install Command**  | `npm install`          | Mặc định                   |

### Bước 4.4: CHƯA CLICK "Deploy"

**Thao tác:**

```
DỪNG LẠI! Cần thêm Environment Variables trước khi deploy.
Mở phần "Environment Variables" bên dưới.
```

---

## 5. CẤU HÌNH ENVIRONMENT VARIABLES

### Bước 5.1: Thêm các biến môi trường

**Thao tác:**

```
1. Trong trang cấu hình Project
2. Mở section "Environment Variables"
3. Thêm từng biến (Click "Add" sau mỗi biến)
```

**Danh sách biến cần thêm:**

| #   | Name (Key)                   | Value                                                                      |
| --- | ---------------------------- | -------------------------------------------------------------------------- |
| 1   | `REACT_APP_API_URL`          | `https://flower-manager-api.onrender.com/api`                              |
| 2   | `REACT_APP_UPLOAD_URL`       | `https://flower-manager-api.onrender.com/api/upload`                       |
| 3   | `REACT_APP_WS_URL`           | `https://flower-manager-api.onrender.com/ws/chat`                          |
| 4   | `REACT_APP_NAME`             | `FlowerCorner`                                                             |
| 5   | `REACT_APP_HOTLINE`          | `1900 633 045`                                                             |
| 6   | `REACT_APP_GOOGLE_CLIENT_ID` | `418199736625-gu3djsvnrd31hj7rid3eg25ivic6a0dd.apps.googleusercontent.com` |

### Bước 5.2: Chọn Environment

**Thao tác:**

```
Cho mỗi biến, chọn environments:
☑️ Production
☑️ Preview
☑️ Development (optional)
```

### Bước 5.3: Xác minh

**Kiểm tra:**

```
- Đã thêm đủ 6 biến môi trường
- Tất cả đều có tick ở Production
- Không có lỗi đỏ
```

---

## 6. DEPLOY VÀ THEO DÕI

### Bước 6.1: Bắt đầu Deploy

**Thao tác:**

```
1. Kiểm tra lại tất cả cấu hình
2. Click "Deploy" (nút xanh ở dưới cùng)
3. Đợi Vercel bắt đầu build
```

### Bước 6.2: Theo dõi quá trình Build

**Thao tác:**

```
1. Trang sẽ chuyển sang trang deployment
2. Theo dõi quá trình build:
   - Installing dependencies...
   - Building application...
   - Generating static pages...
   - Finalizing...

3. Thời gian build: 1-3 phút (nhanh hơn Render)
```

**Các log cần chú ý:**

```
✓ Installed dependencies
✓ Build completed
✓ 1 Deployment created
🎉 Congratulations! Your project is now live.
```

### Bước 6.3: Lấy URL

**Thao tác:**

```
1. Sau khi deploy xong, sẽ hiển thị preview
2. URL production dạng:
   https://flower-shop-frontend.vercel.app

3. Hoặc custom domain nếu có
```

---

## 7. KIỂM TRA SAU DEPLOY

### Bước 7.1: Test trang chủ

**Thao tác:**

```
1. Mở URL: https://flower-shop-frontend.vercel.app
2. Kiểm tra:
   ✅ Trang load không lỗi
   ✅ Hình ảnh hiển thị
   ✅ Không có lỗi console (F12 → Console)
```

### Bước 7.2: Test API connection

**Thao tác:**

```
1. Mở F12 → Network tab
2. Refresh trang
3. Kiểm tra các request đến:
   - flower-manager-api.onrender.com/api/products ✅
   - flower-manager-api.onrender.com/api/categories ✅
   - flower-manager-api.onrender.com/api/banners ✅
```

### Bước 7.3: Test chức năng

**Checklist:**

```
☐ Trang chủ hiển thị sản phẩm
☐ Click vào sản phẩm xem chi tiết
☐ Tìm kiếm hoạt động
☐ Đăng nhập hoạt động
☐ Giỏ hàng hoạt động
☐ Google Login hoạt động (cần cấu hình thêm)
```

---

## 8. CẬP NHẬT BACKEND CORS

### ⚠️ QUAN TRỌNG: Sau khi có URL Frontend

**Vấn đề:** Backend cần cho phép Frontend URL mới gọi API.

**Bước 8.1: Cập nhật Render Environment Variables**

**Thao tác:**

```
1. Render Dashboard → Flower_manager_API → Settings → Environment
2. Tìm biến APP_FRONTEND_URL
3. Cập nhật giá trị:

   Cũ: https://flower-shop.vercel.app
   Mới: https://flower-shop-frontend.vercel.app (hoặc URL thực của bạn)

4. Click "Save Changes"
5. Render sẽ tự động redeploy
```

### Bước 8.2: Cập nhật Google OAuth (nếu cần)

**Thao tác:**

```
1. Google Cloud Console → APIs & Services → Credentials
2. Tìm OAuth 2.0 Client ID đang dùng
3. Thêm vào "Authorized JavaScript origins":
   https://flower-shop-frontend.vercel.app

4. Thêm vào "Authorized redirect URIs":
   https://flower-shop-frontend.vercel.app/oauth/callback

5. Save
```

---

## 9. XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi 1: Build failed - "Module not found"

**Nguyên nhân:** Import sai path hoặc thiếu dependency

**Giải pháp:**

```
1. Kiểm tra import paths có đúng case-sensitive không (Windows != Linux)
2. Chạy `npm install` local và fix lỗi trước khi push
3. Kiểm tra tất cả dependencies có trong package.json
```

### Lỗi 2: API calls bị CORS blocked

**Nguyên nhân:** Backend chưa cho phép Frontend URL

**Giải pháp:**

```
1. Cập nhật APP_FRONTEND_URL trên Render (xem Bước 8)
2. Đợi Backend redeploy
3. Refresh Frontend
```

### Lỗi 3: Trang trắng sau deploy

**Nguyên nhân:** React Router không hoạt động với static hosting

**Giải pháp:** Tạo file `vercel.json` trong thư mục frontend:

```json
{
  "rewrites": [{ "source": "/(.*)", "destination": "/" }]
}
```

Push lên GitHub và Vercel sẽ tự động redeploy.

### Lỗi 4: Environment variables không hoạt động

**Nguyên nhân:** Biến không có prefix `REACT_APP_`

**Giải pháp:**

```
✅ Đúng: REACT_APP_API_URL
❌ Sai: API_URL

Tất cả biến môi trường trong React PHẢI bắt đầu bằng REACT_APP_
```

### Lỗi 5: Google Login không hoạt động

**Nguyên nhân:** URL chưa được thêm vào Google OAuth settings

**Giải pháp:** Xem Bước 8.2 để thêm authorized origin

### Lỗi 6: WebSocket không kết nối

**Nguyên nhân:** Vercel không hỗ trợ WebSocket trực tiếp cho static sites

**Giải pháp:**

```
WebSocket sẽ hoạt động bình thường vì kết nối trực tiếp đến Backend Render.
Kiểm tra REACT_APP_WS_URL có đúng không.
```

---

## ⏰ THÔNG TIN FREE TIER

| Tính năng                | Giới hạn        |
| ------------------------ | --------------- |
| **Bandwidth**            | 100 GB/tháng    |
| **Deployments**          | Không giới hạn  |
| **Preview Deployments**  | Không giới hạn  |
| **Serverless Functions** | 100 GB-Hours    |
| **Build Time**           | 6000 phút/tháng |
| **Team Members**         | 1 (Hobby plan)  |

> 💡 **Tip:** Mỗi commit lên GitHub sẽ tự động tạo Preview Deployment mới.

---

## ✅ CHECKLIST HOÀN THÀNH

- [ ] Đăng ký tài khoản Vercel (qua GitHub)
- [ ] Push code frontend lên GitHub
- [ ] Import project vào Vercel
- [ ] Framework Preset = Create React App
- [ ] Thêm tất cả 6 Environment Variables
- [ ] Click "Deploy"
- [ ] Build thành công
- [ ] Test trang chủ hoạt động
- [ ] Test API connection (F12 → Network)
- [ ] Cập nhật Backend CORS với URL mới

---

## 🔗 LIÊN KẾT HỮU ÍCH

- [Vercel Documentation](https://vercel.com/docs)
- [Create React App on Vercel](https://vercel.com/guides/deploying-react-with-vercel-cra)
- [Environment Variables](https://vercel.com/docs/concepts/projects/environment-variables)
- [Custom Domains](https://vercel.com/docs/concepts/projects/domains)

---

## 📝 SAU KHI DEPLOY THÀNH CÔNG

**Lưu lại thông tin:**

```
Frontend URL: https://flower-shop-frontend.vercel.app
Backend URL:  https://flower-manager-api.onrender.com
Swagger UI:   https://flower-manager-api.onrender.com/swagger-ui.html
```

**Thông tin đã có sẵn từ Backend Render:**

```
Backend API: https://flower-manager-api.onrender.com/api
Database:    TiDB Cloud (Singapore)
```

---

## 🎉 HOÀN THÀNH TOÀN BỘ DEPLOY

Sau khi hoàn thành tất cả các bước, bạn sẽ có:

| Component         | Platform   | URL                                       |
| ----------------- | ---------- | ----------------------------------------- |
| **Frontend**      | Vercel     | `https://flower-shop-frontend.vercel.app` |
| **Backend API**   | Render     | `https://flower-manager-api.onrender.com` |
| **Database**      | TiDB Cloud | Singapore region                          |
| **Image Storage** | Cloudinary | Tự động                                   |

> **Tổng thời gian deploy Frontend:** ~5-10 phút

---

## 📌 LƯU Ý QUAN TRỌNG

1. **Free tier limitations:**
   - Render: Backend sẽ "sleep" sau 15 phút không hoạt động
   - First request sau sleep: 30-50 giây cold start

2. **Auto-deploy:**
   - Mỗi khi push code mới lên GitHub
   - Vercel & Render sẽ tự động rebuild

3. **Custom domain:**
   - Có thể thêm domain riêng trên cả Vercel và Render
   - Miễn phí với HTTPS
