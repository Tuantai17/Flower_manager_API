# 📋 BÁO CÁO REVIEW & CHẤM ĐIỂM DỰ ÁN THƯƠNG MẠI ĐIỆN TỬ

<div align="center">

# 🌸 FlowerCorner E-Commerce Platform

**Đồ án cuối khóa - Hệ thống Thương mại Điện tử Bán Hoa Online**

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green?style=for-the-badge&logo=spring)
![React](https://img.shields.io/badge/React-19.2.3-blue?style=for-the-badge&logo=react)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-red?style=for-the-badge)
![TailwindCSS](https://img.shields.io/badge/TailwindCSS-3.4-blue?style=for-the-badge&logo=tailwindcss)

</div>

---

## 📑 MỤC LỤC

1. [Thông tin chung](#1-thông-tin-chung)
2. [Tổng quan dự án (Inventory)](#2-tổng-quan-dự-án-inventory)
   - [2.1 Backend Inventory](#21-backend-inventory)
   - [2.2 Frontend Inventory](#22-frontend-inventory)
3. [Bảng chấm điểm theo Rubric](#3-bảng-chấm-điểm-theo-rubric)
4. [Kết luận & Ưu tiên cải thiện](#4-kết-luận--ưu-tiên-cải-thiện)
5. [Checklist kiểm tra nhanh](#5-checklist-kiểm-tra-nhanh)

---

## 1. THÔNG TIN CHUNG

| Mục               | Chi tiết                                |
| ----------------- | --------------------------------------- |
| **Tên dự án**     | FlowerCorner E-Commerce Platform        |
| **Loại dự án**    | Trang thương mại điện tử bán hoa online |
| **Ngày review**   | 27/01/2026                              |
| **Reviewer**      | Senior Fullstack (Spring Boot + React)  |
| **Backend Repo**  | `flower-manager/`                       |
| **Frontend Repo** | `flower-shop-frontend/`                 |

### Yêu cầu đề tài:

- ✅ Sử dụng Spring Boot (bắt buộc)
- ✅ Áp dụng nguyên lý lập trình hướng đối tượng
- ✅ Khoảng 10 bảng database
- ✅ Chức năng: Sản phẩm, Danh mục, Người dùng, Đơn hàng, Giỏ hàng
- ✅ Ít nhất 2 chức năng nâng cao

---

## 2. TỔNG QUAN DỰ ÁN (INVENTORY)

### 2.1 BACKEND INVENTORY

#### 📦 Tech Stack

| Công nghệ       | Phiên bản | Mô tả                   | Evidence                 |
| --------------- | --------- | ----------------------- | ------------------------ |
| Spring Boot     | 3.4.0     | Application Framework   | `pom.xml` (line 10-11)   |
| Java            | 21        | Programming Language    | `pom.xml` (line 22)      |
| MySQL           | 8.x       | Database                | `pom.xml` (line 86-90)   |
| Spring Security | 6.x       | Security Framework      | `pom.xml` (line 93-96)   |
| JWT (jjwt)      | 0.12.5    | Token Authentication    | `pom.xml` (line 98-115)  |
| Spring Data JPA | 3.x       | ORM Framework           | `pom.xml` (line 32-36)   |
| Cloudinary      | 1.36.0    | Image Upload            | `pom.xml` (line 117-122) |
| Swagger/OpenAPI | 2.7.0     | API Documentation       | `pom.xml` (line 159-164) |
| WebSocket/STOMP | -         | Real-time Communication | `pom.xml` (line 153-157) |
| Gemini AI       | 1.2.0     | AI Chatbot              | `pom.xml` (line 139-144) |
| Bucket4j        | 8.1.0     | Rate Limiting           | `pom.xml` (line 166-171) |
| Maven           | Wrapper   | Build Tool              | `mvnw`, `mvnw.cmd`       |

#### 📁 Cấu trúc Package

```
src/main/java/com/flower/manager/
├── config/           # 12 files - Spring Configurations
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   ├── CorsConfig.java
│   ├── CloudinaryConfig.java
│   ├── GeminiConfig.java
│   └── OpenApiConfig.java
├── controller/       # 37 files - REST Controllers
│   ├── auth/         # AuthController, GoogleAuthController
│   ├── product/      # ProductController
│   ├── order/        # OrderController, OrderAdminController
│   ├── cart/         # CartController
│   ├── payment/      # PaymentController (MoMo)
│   └── admin/        # AdminController
├── dto/              # 84 files - Data Transfer Objects
│   ├── request/      # Request DTOs
│   └── response/     # Response DTOs
├── entity/           # 23 files - JPA Entities
├── enums/            # 10 files - Enum types
├── exception/        # 5 files - Exception Handling
├── mapper/           # 2 files - Entity-DTO mappers
├── repository/       # 22 files - JPA Repositories
├── security/         # 4 files - JWT & Auth
│   ├── JwtUtils.java
│   └── JwtAuthenticationFilter.java
├── service/          # 52 files - Business Logic
│   ├── impl/         # Service implementations
│   └── interfaces/   # Service interfaces
└── util/             # 1 file - Utilities
```

#### 🗄️ Database Entities (23 bảng)

| #   | Entity                     | Mô tả                               | Fields chính                                                            |
| --- | -------------------------- | ----------------------------------- | ----------------------------------------------------------------------- |
| 1   | **User**                   | Người dùng (implements UserDetails) | id, username, email, password, role, isActive                           |
| 2   | **Product**                | Sản phẩm hoa                        | id, name, slug, description, price, salePrice, thumbnail, stockQuantity |
| 3   | **Category**               | Danh mục sản phẩm                   | id, name, slug, description, thumbnail                                  |
| 4   | **Order**                  | Đơn hàng                            | id, orderCode, user, senderInfo, recipientInfo, totalPrice, status      |
| 5   | **OrderItem**              | Chi tiết đơn hàng                   | id, order, productName, quantity, price                                 |
| 6   | **Cart**                   | Giỏ hàng                            | id, user, items, createdAt                                              |
| 7   | **CartItem**               | Chi tiết giỏ hàng                   | id, cart, product, quantity, price                                      |
| 8   | **Voucher**                | Mã giảm giá                         | id, code, discountType, discountValue, minOrderAmount                   |
| 9   | **SavedVoucher**           | Voucher đã lưu                      | id, user, voucher, savedAt                                              |
| 10  | **Review**                 | Đánh giá sản phẩm                   | id, user, product, rating, comment                                      |
| 11  | **Notification**           | Thông báo                           | id, user, title, message, isRead                                        |
| 12  | **Banner**                 | Banner quảng cáo                    | id, title, imageUrl, link, isActive                                     |
| 13  | **Article**                | Tin tức/Bài viết                    | id, title, slug, content, thumbnail                                     |
| 14  | **ChatSession**            | Session chat                        | id, user, status, createdAt                                             |
| 15  | **ChatMessage**            | Tin nhắn chat                       | id, session, sender, message, timestamp                                 |
| 16  | **ContactTicket**          | Phiếu hỗ trợ                        | id, user, subject, status, priority                                     |
| 17  | **ContactTicketMessage**   | Tin nhắn hỗ trợ                     | id, ticket, sender, message, imageUrl                                   |
| 18  | **StockHistory**           | Lịch sử tồn kho                     | id, product, quantity, reason, createdAt                                |
| 19  | **ShippingDistrictRule**   | Phí ship theo quận                  | id, province, district, shippingFee                                     |
| 20  | **PasswordResetToken**     | Token đặt lại mật khẩu              | id, user, token, expiryDate                                             |
| 21  | **EmailVerificationToken** | Token xác thực email                | id, user, token, expiryDate                                             |
| 22  | **NewsletterSubscriber**   | Đăng ký nhận tin                    | id, email, subscribedAt                                                 |
| 23  | **Role**                   | Vai trò (Enum)                      | ADMIN, CUSTOMER, STAFF                                                  |

**✅ Đáp ứng yêu cầu: ≥10 bảng (có 23 bảng)**

#### 🔌 API Endpoints chính

**Authentication APIs:**
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/api/auth/login` | Đăng nhập | ❌ |
| POST | `/api/auth/register` | Đăng ký | ❌ |
| POST | `/api/auth/google` | Đăng nhập Google | ❌ |
| POST | `/api/auth/forgot-password` | Quên mật khẩu | ❌ |
| POST | `/api/auth/reset-password` | Đặt lại mật khẩu | ❌ |
| POST | `/api/auth/change-password` | Đổi mật khẩu | ✅ |
| GET | `/api/auth/me` | Thông tin user hiện tại | ✅ |

**Product & Category APIs:**
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/products` | Danh sách sản phẩm | ❌ |
| GET | `/api/products/{id}` | Chi tiết sản phẩm | ❌ |
| GET | `/api/categories` | Danh sách danh mục | ❌ |
| GET | `/api/categories/{id}` | Chi tiết danh mục | ❌ |

**Cart & Order APIs:**
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/cart` | Lấy giỏ hàng | ✅ |
| POST | `/api/cart/add` | Thêm vào giỏ | ✅ |
| PUT | `/api/cart/update` | Cập nhật giỏ | ✅ |
| DELETE | `/api/cart/remove/{id}` | Xóa khỏi giỏ | ✅ |
| POST | `/api/orders` | Tạo đơn hàng | ✅ |
| GET | `/api/orders` | Danh sách đơn hàng | ✅ |
| GET | `/api/orders/{id}` | Chi tiết đơn hàng | ✅ |

**Payment APIs:**
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| POST | `/api/payment/momo/create` | Tạo thanh toán MoMo | ✅ |
| POST | `/api/payment/momo/notify` | IPN callback | ❌ |
| GET | `/api/payment/momo/return` | Return URL | ❌ |
| GET | `/api/payment/momo/status/{id}` | Kiểm tra trạng thái | ✅ |

**Admin APIs:**
| Method | Endpoint | Mô tả | Auth |
|--------|----------|-------|------|
| GET | `/api/admin/dashboard` | Dashboard stats | ADMIN |
| CRUD | `/api/admin/products/**` | Quản lý sản phẩm | ADMIN |
| CRUD | `/api/admin/categories/**` | Quản lý danh mục | ADMIN |
| CRUD | `/api/admin/orders/**` | Quản lý đơn hàng | ADMIN |
| CRUD | `/api/admin/users/**` | Quản lý người dùng | ADMIN |
| CRUD | `/api/admin/vouchers/**` | Quản lý voucher | ADMIN |

**📖 Swagger UI:** `http://localhost:8080/swagger-ui.html`

#### 🔐 Authentication & Security

**JWT Implementation:**

```
┌─────────────────────────────────────────────────────────────┐
│                    JWT Authentication Flow                   │
├─────────────────────────────────────────────────────────────┤
│  1. User login với username/email/phone + password          │
│  2. AuthController → AuthManager → UserDetailsService       │
│  3. Verify credentials → JwtUtils.generateJwtToken()        │
│  4. Return JWT token + user info                            │
│  5. Frontend lưu token vào localStorage                     │
│  6. Mỗi request: Authorization: Bearer {token}              │
│  7. JwtAuthenticationFilter validate token                  │
│  8. Set SecurityContext → Cho phép truy cập                 │
└─────────────────────────────────────────────────────────────┘
```

**Evidence Files:**

- `security/JwtUtils.java` - Generate, validate JWT token
- `security/JwtAuthenticationFilter.java` - Filter mỗi request
- `security/CustomUserDetailsService.java` - Load user từ DB
- `config/SecurityConfig.java` - Cấu hình URL permissions

**Role-based Authorization:**
| Role | Permissions |
|------|-------------|
| ADMIN | Toàn quyền, /api/admin/** |
| STAFF | /api/chat/admin/**, /api/staff/\*\* |
| CUSTOMER | User routes, cart, orders |

#### 🧪 Testing

**Test Files (7 files):**

| #   | Test File                            | Loại        | Số test cases |
| --- | ------------------------------------ | ----------- | ------------- |
| 1   | `FlowerManagerApplicationTests.java` | Integration | 1             |
| 2   | `ProductControllerTest.java`         | Controller  | ~3            |
| 3   | `AuthServiceImplTest.java`           | Unit        | ~5            |
| 4   | `CartServiceImplTest.java`           | Unit        | ~4            |
| 5   | `OrderServiceImplTest.java`          | Unit        | ~15 (@Nested) |
| 6   | `ProductServiceTest.java`            | Unit        | 4             |
| 7   | `VoucherServiceImplTest.java`        | Unit        | ~5            |

**Test Framework:**

- JUnit 5 (@Test, @BeforeEach, @Nested, @DisplayName)
- Mockito (@Mock, @InjectMocks, @ExtendWith)
- Spring Boot Test (@SpringBootTest)
- Spring Security Test

**Chạy tests:**

```bash
./mvnw test                           # Chạy tất cả tests
./mvnw test -Dtest=ProductServiceTest # Chạy test cụ thể
./mvnw test jacoco:report             # Chạy với coverage
```

**Test Report:** `target/surefire-reports/`

#### 🐳 Deployment

**Dockerfile (Multi-stage build):**

```dockerfile
# Stage 1: Build
FROM eclipse-temurin:21-jdk AS builder
# Maven build...

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
# Copy JAR, create non-root user
HEALTHCHECK --interval=30s...
```

**Environment Variables (.env.example):**

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/java_flower
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=YourSecretKey256Bits
JWT_EXPIRATION=86400000

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# MoMo Payment
MOMO_PARTNER_CODE=MOMO
MOMO_ACCESS_KEY=your_access_key
MOMO_SECRET_KEY=your_secret_key

# Google OAuth & Gemini AI
GOOGLE_CLIENT_ID=your_client_id
GEMINI_API_KEY=your_gemini_key
```

---

### 2.2 FRONTEND INVENTORY

#### 📦 Tech Stack

| Công nghệ        | Phiên bản | Mô tả               | Evidence                 |
| ---------------- | --------- | ------------------- | ------------------------ |
| React            | 19.2.3    | UI Framework        | `package.json` (line 18) |
| React Router DOM | 7.10.1    | Client-side Routing | `package.json` (line 22) |
| Axios            | 1.13.2    | HTTP Client         | `package.json` (line 15) |
| TailwindCSS      | 3.4.1     | CSS Framework       | `package.json` (line 57) |
| React Toastify   | 11.0.5    | Toast Notifications | `package.json` (line 24) |
| Recharts         | 3.6.0     | Charts & Analytics  | `package.json` (line 25) |
| React Icons      | 5.5.0     | Icon Library        | `package.json` (line 20) |
| Leaflet          | 1.9.4     | Maps Integration    | `package.json` (line 17) |
| STOMP/WebSocket  | 7.2.1     | Real-time           | `package.json` (line 10) |
| Google OAuth     | 0.13.0    | Social Login        | `package.json` (line 9)  |
| Create React App | 5.0.1     | Build Tool          | `package.json` (line 23) |

#### 📁 Cấu trúc thư mục

```
src/
├── api/                    # 24 files - API Services
│   ├── axiosConfig.js      # Axios instance + interceptors
│   ├── authService.js      # Authentication APIs
│   ├── productApi.js       # Product APIs
│   ├── cartApi.js          # Cart APIs
│   ├── orderApi.js         # Order APIs
│   ├── paymentApi.js       # Payment APIs
│   └── ...
├── assets/                 # 2 files - Static assets
├── components/             # 69 files - Reusable Components
│   ├── admin/              # 29 files - Admin components
│   ├── checkout/           # 2 files - Checkout components
│   ├── common/             # 17 files - Shared components
│   │   ├── Header.js       # Navigation header
│   │   ├── Footer.js       # Page footer
│   │   ├── ProtectedRoute.js
│   │   ├── AdminProtectedRoute.js
│   │   ├── Loading.js
│   │   └── ...
│   └── user/               # 21 files - User components
├── context/                # 3 files - React Context
│   ├── AppContext.js       # Cart, Favorites, Notification
│   ├── AuthContext.js      # User authentication
│   └── AdminAuthContext.js # Admin authentication
├── hooks/                  # 5 files - Custom Hooks
│   ├── useDebounce.js
│   ├── useCheckout.js
│   ├── useCheckoutShipping.js
│   └── useLocalStorage.js
├── layouts/                # 3 files - Page Layouts
│   ├── UserLayout.js
│   ├── AdminLayout.js
│   └── BlankLayout.js
├── pages/                  # 72+ files - Page Components
│   ├── admin/              # 40+ files (15 subdirs)
│   │   ├── Dashboard.js
│   │   ├── product/        # CRUD sản phẩm
│   │   ├── category/       # CRUD danh mục
│   │   ├── order/          # Quản lý đơn hàng
│   │   └── ...
│   └── user/               # 30 files
│       ├── HomePage.js
│       ├── ShopPage.js
│       ├── CartPage.js
│       ├── CheckoutPage.js
│       └── ...
├── services/               # 4 files - Service layer
├── types/                  # 1 file - TypeScript types
├── utils/                  # 6 files - Utility functions
├── App.js                  # Main router configuration
├── App.css
├── index.js                # Entry point
└── index.css               # Global styles
```

#### 🛣️ Routes & Pages

**Public Routes (UserLayout):**

| Route              | Component          | Mô tả                  |
| ------------------ | ------------------ | ---------------------- |
| `/`                | HomePage           | Trang chủ              |
| `/shop`            | ShopPage           | Cửa hàng               |
| `/product/:id`     | ProductDetailPage  | Chi tiết sản phẩm      |
| `/category/:id`    | CategoryPage       | Sản phẩm theo danh mục |
| `/search`          | SearchResultPage   | Kết quả tìm kiếm       |
| `/about`           | AboutPage          | Giới thiệu             |
| `/contact`         | ContactPage        | Liên hệ                |
| `/news`            | NewsPage           | Tin tức                |
| `/news/:slug`      | NewsDetailPage     | Chi tiết tin tức       |
| `/vouchers`        | VoucherPage        | Danh sách voucher      |
| `/wishlist`        | WishlistPage       | Sản phẩm yêu thích     |
| `/login`           | LoginPage          | Đăng nhập              |
| `/register`        | RegisterPage       | Đăng ký                |
| `/forgot-password` | ForgotPasswordPage | Quên mật khẩu          |
| `/reset-password`  | ResetPasswordPage  | Đặt lại mật khẩu       |
| `/faq`             | FAQPage            | Câu hỏi thường gặp     |
| `/privacy`         | PrivacyPolicyPage  | Chính sách bảo mật     |
| `/terms`           | TermsOfServicePage | Điều khoản sử dụng     |

**Protected User Routes:**

| Route                 | Component             | Mô tả              | Auth |
| --------------------- | --------------------- | ------------------ | ---- |
| `/cart`               | CartPage              | Giỏ hàng           | ✅   |
| `/checkout`           | CheckoutPage          | Thanh toán         | ✅   |
| `/profile`            | ProfilePage           | Tài khoản          | ✅   |
| `/profile/orders`     | MyOrdersPage          | Đơn hàng của tôi   | ✅   |
| `/profile/orders/:id` | OrderDetailPage       | Chi tiết đơn hàng  | ✅   |
| `/my-vouchers`        | MyVouchersPage        | Voucher của tôi    | ✅   |
| `/my-tickets`         | MyTicketsPage         | Phiếu hỗ trợ       | ✅   |
| `/change-password`    | ChangePasswordPage    | Đổi mật khẩu       | ✅   |
| `/notifications`      | UserNotificationsPage | Thông báo          | ✅   |
| `/payment/result`     | PaymentResultPage     | Kết quả thanh toán | ✅   |

**Admin Routes (AdminProtectedRoute + AdminLayout):**

| Route                      | Component        | Mô tả              | Role  |
| -------------------------- | ---------------- | ------------------ | ----- |
| `/admin/login`             | AdminLoginPage   | Đăng nhập admin    | -     |
| `/admin`                   | Dashboard        | Dashboard          | ADMIN |
| `/admin/products`          | ProductList      | Danh sách sản phẩm | ADMIN |
| `/admin/products/create`   | ProductCreate    | Thêm sản phẩm      | ADMIN |
| `/admin/products/edit/:id` | ProductEdit      | Sửa sản phẩm       | ADMIN |
| `/admin/categories`        | CategoryList     | Danh mục           | ADMIN |
| `/admin/orders`            | OrderList        | Đơn hàng           | ADMIN |
| `/admin/customers`         | CustomerList     | Khách hàng         | ADMIN |
| `/admin/vouchers`          | VoucherList      | Voucher            | ADMIN |
| `/admin/stock`             | StockList        | Tồn kho            | ADMIN |
| `/admin/reviews`           | ReviewList       | Đánh giá           | ADMIN |
| `/admin/banners`           | BannerList       | Banner             | ADMIN |
| `/admin/tickets`           | TicketList       | Hỗ trợ             | ADMIN |
| `/admin/articles`          | ArticleList      | Bài viết           | ADMIN |
| `/admin/analytics`         | AnalyticsPage    | Thống kê           | ADMIN |
| `/admin/shipping-rules`    | ShippingRuleList | Phí ship           | ADMIN |
| `/admin/settings`          | SettingsPage     | Cài đặt            | ADMIN |

#### 🔗 API Client Configuration

**Axios Instance (`api/axiosConfig.js`):**

```javascript
// Base URL
baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api'

// Token Selection Logic
const getTokenForRequest = (url) => {
    // Admin routes → adminToken
    // User routes → userToken
}

// Request Interceptor
- Auto attach token based on URL
- Separate User/Admin tokens

// Response Interceptor
- Error handling: 400, 401, 403, 404, 422, 500
- Auto logout on 401 (token expired)
```

#### 🏪 State Management (Context API)

**AppContext (`context/AppContext.js`):**

```javascript
const initialState = {
  cart: [], // Giỏ hàng
  favorites: [], // Sản phẩm yêu thích
  user: null, // User info
  isAuthenticated: false,
  loading: false,
  notification: null,
};

// Actions: ADD_TO_CART, REMOVE_FROM_CART, UPDATE_QUANTITY...
// LocalStorage persistence cho cart & favorites
```

**AuthContext (`context/AuthContext.js`):**

- User authentication state
- Login, logout, register functions

**AdminAuthContext (`context/AdminAuthContext.js`):**

- Admin authentication (separate from user)
- Admin-specific token management

#### 🎨 UI/UX Features

**TailwindCSS Configuration (`tailwind.config.js`):**

```javascript
theme: {
    extend: {
        colors: {
            primary: { 50-900 },    // Pink palette
            secondary: { 50-900 },  // Green palette
            accent: { gold, cream, rose }
        },
        fontFamily: {
            'display': ['Playfair Display'],
            'body': ['Montserrat']
        },
        animation: {
            'fade-in', 'slide-up', 'slide-in-left',
            'bounce-soft', 'pulse-soft'
        },
        boxShadow: {
            'soft', 'card', 'card-hover'
        }
    }
}
```

**UX Components:**
| Component | Mô tả | Evidence |
|-----------|-------|----------|
| Loading.js | Spinner component | `components/common/Loading.js` |
| GlobalNotification.js | Toast notifications | `components/common/GlobalNotification.js` |
| Modal.js | Modal dialogs | `components/common/Modal.js` |
| Pagination.js | Pagination | `components/common/Pagination.js` |
| SearchBar.js | Search với debounce | `components/common/SearchBar.js` |
| StarRating.js | Rating stars | `components/common/StarRating.js` |
| ChatWidget.js | Live chat widget | `components/common/ChatWidget.js` |

**Custom Hooks:**
| Hook | Mô tả | Evidence |
|------|-------|----------|
| useDebounce | Debounce value | `hooks/useDebounce.js` |
| useCheckout | Checkout logic | `hooks/useCheckout.js` |
| useLocalStorage | Persist to localStorage | `hooks/useLocalStorage.js` |

#### 🐳 Deployment

**Dockerfile (Multi-stage build):**

```dockerfile
# Stage 1: Build với Node
FROM node:20-alpine AS builder
# npm ci, npm run build

# Stage 2: Serve với Nginx
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/build /usr/share/nginx/html
```

**nginx.conf:** SPA routing configured

**Environment Variables (.env.example):**

```properties
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_UPLOAD_URL=http://localhost:8080/api/upload
REACT_APP_NAME=FlowerCorner
REACT_APP_HOTLINE=1900 633 045
REACT_APP_GOOGLE_CLIENT_ID=xxx.apps.googleusercontent.com
```

---

## 3. BẢNG CHẤM ĐIỂM THEO RUBRIC

### 📊 TỔNG HỢP ĐIỂM

| STT | Tiêu chí                               | Điểm tối đa | Điểm đạt | Trạng thái |
| --- | -------------------------------------- | ----------- | -------- | ---------- |
| 1   | Chức năng cơ bản (Core Features)       | 3.0         | **2.8**  | ✅ ĐẠT     |
| 2   | Chức năng nâng cao (Advanced Features) | 2.5         | **2.3**  | ✅ ĐẠT     |
| 3   | Chất lượng mã & Kiến trúc              | 1.5         | **1.4**  | ✅ ĐẠT     |
| 4   | Testing & QA                           | 1.0         | **0.8**  | ✅ ĐẠT     |
| 5   | UI/UX (Giao diện & Trải nghiệm)        | 0.5         | **0.45** | ✅ ĐẠT     |
| 6   | Tài liệu & Trình bày                   | 0.5         | **0.5**  | ✅ ĐẠT     |
| 7   | Deploy & Quản lý môi trường            | 1.0         | **0.9**  | ✅ ĐẠT     |
|     | **TỔNG ĐIỂM**                          | **10.0**    | **9.15** | ✅ **ĐẠT** |

---

### 3.1 CHỨC NĂNG CƠ BẢN (Core Features)

**Điểm: 2.8 / 3.0** | **Trạng thái: ✅ ĐẠT**

#### ✅ Checklist đánh giá:

| Chức năng                  | Trạng thái    | Evidence                              |
| -------------------------- | ------------- | ------------------------------------- |
| **Product Management**     | ✅ Hoàn chỉnh | Entity, Controller, Service, FE pages |
| - Entity với đầy đủ fields | ✅            | `entity/Product.java` (134 lines)     |
| - CRUD API                 | ✅            | `controller/product/` (2 files)       |
| - Admin CRUD UI            | ✅            | `pages/admin/product/` (4 files)      |
| - User view UI             | ✅            | `ShopPage.js`, `ProductDetailPage.js` |
| **Category Management**    | ✅ Hoàn chỉnh |                                       |
| - Entity                   | ✅            | `entity/Category.java`                |
| - CRUD API                 | ✅            | `controller/category/` (2 files)      |
| - Admin UI                 | ✅            | `pages/admin/category/` (3 files)     |
| **User Authentication**    | ✅ Hoàn chỉnh |                                       |
| - Đăng ký                  | ✅            | `AuthController.register()`           |
| - Đăng nhập                | ✅            | `AuthController.login()`              |
| - JWT Token                | ✅            | `JwtUtils.java`                       |
| - Google OAuth             | ✅            | `GoogleAuthController.java`           |
| - Forgot/Reset Password    | ✅            | Có API + FE pages                     |
| **Order Management**       | ✅ Hoàn chỉnh |                                       |
| - Entity với workflow      | ✅            | `entity/Order.java` (196 lines)       |
| - Create order API         | ✅            | `OrderController.java`                |
| - Admin order management   | ✅            | `OrderAdminController.java`           |
| - Order status flow        | ✅            | PENDING→CONFIRMED→...→COMPLETED       |
| **Cart**                   | ✅ Hoàn chỉnh |                                       |
| - Cart + CartItem entities | ✅            | `entity/Cart.java`, `CartItem.java`   |
| - Cart APIs                | ✅            | `CartController.java`                 |
| - FE cart state            | ✅            | `AppContext.js`                       |
| - Cart UI                  | ✅            | `CartPage.js`, `CartIcon.js`          |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ Đầy đủ 5 chức năng cơ bản theo yêu cầu đề tài
- ✅ CRUD hoàn chỉnh cho Product và Category
- ✅ Auth flow với JWT đúng chuẩn + Google OAuth bonus
- ✅ Order workflow với đầy đủ trạng thái (7 states)
- ✅ Cart có cả backend entity và frontend state management

**Điểm cần cải thiện:**

- ⚠️ Cart sử dụng localStorage ở frontend, cần verify sync với backend khi user login
- ⚠️ User management chưa có CRUD đầy đủ từ Admin (chỉ có list)

---

### 3.2 CHỨC NĂNG NÂNG CAO (Advanced Features)

**Điểm: 2.3 / 2.5** | **Trạng thái: ✅ ĐẠT (Vượt yêu cầu)**

#### ✅ Checklist đánh giá (≥2 chức năng nâng cao):

| Tính năng                 | Trạng thái | Evidence                                           |
| ------------------------- | ---------- | -------------------------------------------------- |
| **JWT Authentication**    | ✅         | `JwtUtils.java`, `JwtAuthenticationFilter.java`    |
| **Thanh toán MoMo**       | ✅         | `PaymentController.java`, `MoMoService.java`       |
| **Upload ảnh Cloudinary** | ✅         | `CloudinaryConfig.java`, controller files          |
| **Admin Dashboard**       | ✅         | `Dashboard.js` (23KB), `dashboardApi.js`           |
| **Thống kê (Recharts)**   | ✅         | `pages/admin/analytics/`, Recharts library         |
| **Google OAuth**          | ✅         | `GoogleAuthController.java`, `@react-oauth/google` |
| **Tìm kiếm/Lọc**          | ✅         | `SearchResultPage.js`, `SearchBar.js`              |
| **REST API đầy đủ**       | ✅         | Swagger UI: `/swagger-ui.html`                     |
| **WebSocket Real-time**   | ✅         | `WebSocketConfig.java`, `ChatWidget.js`            |
| **AI Chatbot (Gemini)**   | ✅         | `GeminiConfig.java`, integration                   |
| **Voucher System**        | ✅         | `Voucher.java`, CRUD + apply logic                 |
| **Review/Rating**         | ✅         | `Review.java`, `StarRating.js`                     |
| **Email (JavaMail)**      | ✅         | `spring-boot-starter-mail`, email service          |
| **Rate Limiting**         | ✅         | Bucket4j, `RateLimitInterceptor.java`              |
| **Shipping Rules**        | ✅         | `ShippingDistrictRule.java`                        |
| **Notification System**   | ✅         | `Notification.java`, real-time                     |
| **Maps Integration**      | ✅         | Leaflet, `AddressAutocomplete.js`                  |
| **Article AI Generate**   | ✅         | `ArticleAIGenerate.js`                             |

**Số lượng tính năng nâng cao: 18+ (Yêu cầu: ≥2)**

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ **Vượt xa yêu cầu** với 18+ tính năng nâng cao
- ✅ JWT implementation chuẩn industry
- ✅ Real payment integration (MoMo) - không phải mock
- ✅ AI integration (Gemini chatbot) - cutting-edge
- ✅ Real-time WebSocket cho live chat
- ✅ Dashboard với nhiều metrics và charts

**Điểm cần cải thiện:**

- ⚠️ MoMo cần verify sandbox vs production environment
- ⚠️ AI API key cần bảo mật trong production

---

### 3.3 CHẤT LƯỢNG MÃ & KIẾN TRÚC

**Điểm: 1.4 / 1.5** | **Trạng thái: ✅ ĐẠT**

#### ✅ Checklist đánh giá:

| Tiêu chí                                  | Trạng thái | Evidence                               |
| ----------------------------------------- | ---------- | -------------------------------------- |
| **Controller/Service/Repository pattern** | ✅         | Layered architecture đúng chuẩn        |
| - Controllers rõ ràng                     | ✅         | 37 files trong `controller/`           |
| - Services với interface                  | ✅         | 52 files, interface + impl pattern     |
| - Repositories                            | ✅         | 22 JPA repositories                    |
| **Spring Boot + JPA đúng cách**           | ✅         |                                        |
| - Entity annotations                      | ✅         | @Entity, @Table, @Id, @GeneratedValue  |
| - Relationships                           | ✅         | @ManyToOne, @OneToMany, @OneToOne      |
| - Audit fields                            | ✅         | @PrePersist, @PreUpdate                |
| - Indexes                                 | ✅         | `@Index` trong Order entity            |
| **DTO Pattern**                           | ✅         |                                        |
| - Request/Response separation             | ✅         | 84 DTO files                           |
| - Module organization                     | ✅         | `dto/auth/`, `dto/product/`...         |
| **SOLID/DRY**                             | ✅         |                                        |
| - Interface-based services                | ✅         | All services have interfaces           |
| - Lombok usage                            | ✅         | @Getter, @Setter, @Builder             |
| - Mapper classes                          | ✅         | `mapper/` folder                       |
| **Exception Handling**                    | ✅         |                                        |
| - GlobalExceptionHandler                  | ✅         | 255 lines, 15+ exception types         |
| - Custom exceptions                       | ✅         | BusinessException, ResourceNotFound... |
| - ErrorCode enum                          | ✅         | HTTP status mapping                    |
| **FE Architecture**                       | ✅         |                                        |
| - Component structure                     | ✅         | components/, pages/, layouts/          |
| - API layer                               | ✅         | api/ folder với service files          |
| - Custom hooks                            | ✅         | hooks/ folder                          |
| - Context for state                       | ✅         | context/ folder                        |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ Clean architecture với proper layering (Controller → Service → Repository)
- ✅ DTO pattern đúng chuẩn với request/response separation
- ✅ GlobalExceptionHandler đầy đủ, format error response cho FE
- ✅ Lombok giảm boilerplate code
- ✅ FE organized với reusable components

**Điểm cần cải thiện:**

- ⚠️ Một số repetitive code trong FE pages
- ⚠️ Có thể thêm validation annotations cho DTOs

---

### 3.4 TESTING & QA

**Điểm: 0.8 / 1.0** | **Trạng thái: ✅ ĐẠT (MỘT PHẦN)**

#### ✅ Checklist đánh giá:

| Tiêu chí                   | Trạng thái | Evidence                          |
| -------------------------- | ---------- | --------------------------------- |
| **Unit Tests**             | ✅         | 6 test files cho services         |
| **Integration Tests**      | ✅         | 1 controller test                 |
| **Test Cases + Test Data** | ✅         | ~35 test cases total              |
| **Test Report**            | ✅         | Surefire reports tự động          |
| **Manual Test Checklist**  | ✅         | `TESTING_QA_GUIDE.md` (366 lines) |

#### Test Files:

| File                                 | Loại        | Test Cases    |
| ------------------------------------ | ----------- | ------------- |
| `ProductServiceTest.java`            | Unit        | 4             |
| `OrderServiceImplTest.java`          | Unit        | ~15 (@Nested) |
| `AuthServiceImplTest.java`           | Unit        | ~5            |
| `CartServiceImplTest.java`           | Unit        | ~4            |
| `VoucherServiceImplTest.java`        | Unit        | ~5            |
| `ProductControllerTest.java`         | Controller  | ~3            |
| `FlowerManagerApplicationTests.java` | Integration | 1             |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ Có unit tests cho core services
- ✅ OrderServiceImplTest sử dụng @Nested rất chuyên nghiệp
- ✅ Manual test checklist đầy đủ với 33 test cases
- ✅ Test documentation chi tiết

**Điểm cần cải thiện:**

- ⚠️ Test coverage chưa cao (7 files / nhiều services)
- ⚠️ FE tests: testing-library setup có nhưng chưa có test files
- ⚠️ Chưa có E2E tests

---

### 3.5 UI/UX (Giao diện & Trải nghiệm)

**Điểm: 0.45 / 0.5** | **Trạng thái: ✅ ĐẠT**

#### ✅ Checklist đánh giá:

| Tiêu chí                | Trạng thái | Evidence                        |
| ----------------------- | ---------- | ------------------------------- |
| **Responsive Design**   | ✅         | TailwindCSS breakpoints         |
| **Form Validation**     | ✅         | Required, error messages        |
| **Loading States**      | ✅         | `Loading.js`, spinners          |
| **Error States**        | ✅         | Error displays với styling      |
| **Toast Notifications** | ✅         | React Toastify                  |
| **Auth UX**             | ✅         | Route guards, token handling    |
| **Search với Debounce** | ✅         | `useDebounce` hook              |
| **Pagination**          | ✅         | `Pagination.js` component       |
| **Custom Animations**   | ✅         | `tailwind.config.js` animations |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ TailwindCSS với custom theme đẹp
- ✅ Responsive design
- ✅ Toast notifications cho feedback
- ✅ Loading states cho async operations
- ✅ useDebounce cho search optimization

**Điểm cần cải thiện:**

- ⚠️ Handmade validation (không dùng React Hook Form + Yup)
- ⚠️ Double-submit prevention cần verify

---

### 3.6 TÀI LIỆU & TRÌNH BÀY

**Điểm: 0.5 / 0.5** | **Trạng thái: ✅ ĐẠT**

#### ✅ Checklist đánh giá:

| Tiêu chí                 | Trạng thái | Evidence                          |
| ------------------------ | ---------- | --------------------------------- |
| **README Backend**       | ✅         | 206 lines, đầy đủ hướng dẫn       |
| **README Frontend**      | ✅         | 251 lines, tech stack + structure |
| **API Documentation**    | ✅         | Swagger UI `/swagger-ui.html`     |
| **Environment Examples** | ✅         | `.env.example` cả 2 repos         |
| **Testing Guide**        | ✅         | `TESTING_QA_GUIDE.md`             |
| **Project Structure**    | ✅         | Documented trong READMEs          |
| **Git Usage**            | ✅         | .gitignore, .git present          |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ README đầy đủ cả BE và FE
- ✅ Swagger UI cho API exploration
- ✅ Environment examples rõ ràng
- ✅ Multiple documentation folders (`docs/`, `docbaocao/`)

---

### 3.7 DEPLOY & QUẢN LÝ MÔI TRƯỜNG

**Điểm: 0.9 / 1.0** | **Trạng thái: ✅ ĐẠT**

#### ✅ Checklist đánh giá:

| Tiêu chí                | Trạng thái | Evidence               |
| ----------------------- | ---------- | ---------------------- |
| **Backend Dockerfile**  | ✅         | Multi-stage, optimized |
| **Frontend Dockerfile** | ✅         | Node → Nginx           |
| **Environment Config**  | ✅         | .env.example files     |
| **CORS Configuration**  | ✅         | `SecurityConfig.java`  |
| **Health Checks**       | ✅         | Docker HEALTHCHECK     |
| **Security (non-root)** | ✅         | Docker non-root user   |
| **nginx.conf**          | ✅         | SPA routing            |
| **.dockerignore**       | ✅         | Cả 2 repos             |

#### 📝 Nhận xét chi tiết:

**Điểm mạnh:**

- ✅ Docker ready cho cả FE và BE
- ✅ Multi-stage builds tối ưu image size
- ✅ nginx config cho SPA routing
- ✅ Security best practices (non-root user)

**Điểm cần cải thiện:**

- ⚠️ Chưa có docker-compose.yml trong repo chính
- ⚠️ Production CORS nên giới hạn allowed origins

---

## 4. KẾT LUẬN & ƯU TIÊN CẢI THIỆN

### 📊 TỔNG ĐIỂM: 9.15 / 10

### ✅ NHẬN ĐỊNH: **ĐẠT YÊU CẦU MÔN**

Dự án **FlowerCorner E-Commerce Platform** là một dự án thương mại điện tử **hoàn chỉnh và chất lượng cao**:

- ✅ **23 entities** (vượt yêu cầu 10 bảng)
- ✅ **37+ controllers** với đầy đủ CRUD APIs
- ✅ **72+ frontend pages** covering user + admin
- ✅ **18+ tính năng nâng cao** (vượt yêu cầu 2 tính năng)
- ✅ **Clean architecture** với proper layering
- ✅ **Unit tests** cho core services
- ✅ **Docker-ready** cho deployment
- ✅ **Documentation** đầy đủ

---

### 🚀 TOP 5 VIỆC LÀM ĐỂ TĂNG ĐIỂM NHANH

| #   | Việc cần làm                                     | Điểm có thể tăng | Thời gian |
| --- | ------------------------------------------------ | ---------------- | --------- |
| 1   | Thêm 3-5 unit tests cho services còn thiếu       | +0.15            | 2-3h      |
| 2   | Tích hợp React Hook Form + Yup cho FE validation | +0.05            | 3-4h      |
| 3   | Cart sync với backend khi user login             | +0.05            | 2-3h      |
| 4   | Tạo docker-compose.yml cho full stack            | +0.1             | 1-2h      |
| 5   | Thêm 2-3 FE component tests                      | +0.1             | 2-3h      |

---

### 📋 ROADMAP CẢI THIỆN

#### Mức 1: Tối thiểu để đạt (1-2 ngày)

| Việc cần làm                     | Cách kiểm tra                    |
| -------------------------------- | -------------------------------- |
| ✅ Chạy `./mvnw test` thành công | Screenshot BUILD SUCCESS         |
| ✅ Điền Manual Test Checklist    | PASS/FAIL cho 33 test cases      |
| ✅ Screenshot test results       | Lưu vào `docbaocao/screenshots/` |
| ✅ Copy kết luận vào báo cáo     | Sử dụng template trong guide     |

#### Mức 2: Chuẩn rubric (3-7 ngày)

| Việc cần làm                | Cách kiểm tra                  |
| --------------------------- | ------------------------------ |
| Thêm 3-5 unit tests mới     | `./mvnw test` reports          |
| Thêm JaCoCo coverage report | `./mvnw test jacoco:report`    |
| Tạo docker-compose.yml      | `docker-compose up` thành công |
| Cart sync khi login         | Test thủ công luồng            |

#### Mức 3: Production-ish (7-14 ngày)

| Việc cần làm        | Cách kiểm tra                        |
| ------------------- | ------------------------------------ |
| FE component tests  | `npm test` với coverage              |
| Security hardening  | CORS restrict, rate limit verify     |
| Production profiles | `spring.profiles.active=prod`        |
| CI/CD pipeline      | GitHub Actions / GitLab CI           |
| Monitoring/Logging  | Structured logging, health endpoints |

---

## 5. CHECKLIST KIỂM TRA NHANH

### 🖥️ Local Run Checklist

#### Backend:

- [ ] `cd flower-manager`
- [ ] `cp .env.example .env` (edit values)
- [ ] `./mvnw clean package -DskipTests`
- [ ] `./mvnw spring-boot:run`
- [ ] Access `http://localhost:8080/swagger-ui.html`
- [ ] Verify `http://localhost:8080/api` returns info

#### Frontend:

- [ ] `cd flower-shop-frontend`
- [ ] `cp .env.example .env` (edit if needed)
- [ ] `npm install`
- [ ] `npm start`
- [ ] Access `http://localhost:3000`
- [ ] `npm run build` (verify production build)

---

### 🔌 API Checklist (Postman/curl)

| #   | API                    | Method      | Body                           | Expected            |
| --- | ---------------------- | ----------- | ------------------------------ | ------------------- |
| 1   | `/api/auth/register`   | POST        | {username, email, password...} | 200 + token         |
| 2   | `/api/auth/login`      | POST        | {identifier, password}         | 200 + token         |
| 3   | `/api/products`        | GET         | -                              | 200 + product list  |
| 4   | `/api/categories`      | GET         | -                              | 200 + category list |
| 5   | `/api/cart`            | GET (auth)  | Header: Bearer token           | 200 cart / 401      |
| 6   | `/api/cart/add`        | POST (auth) | {productId, quantity}          | 200                 |
| 7   | `/api/orders`          | POST (auth) | {order info}                   | 201                 |
| 8   | `/api/admin/dashboard` | GET (admin) | Header: Bearer admin_token     | 200 / 403           |

---

### 🖱️ Frontend Manual Checklist

| #   | Chức năng      | Test steps                     | Kết quả           |
| --- | -------------- | ------------------------------ | ----------------- |
| 1   | Đăng ký        | /register → fill form → submit | ⬜ PASS / ⬜ FAIL |
| 2   | Đăng nhập      | /login → enter credentials     | ⬜ PASS / ⬜ FAIL |
| 3   | Đăng xuất      | Click logout button            | ⬜ PASS / ⬜ FAIL |
| 4   | Xem sản phẩm   | /shop → browse list            | ⬜ PASS / ⬜ FAIL |
| 5   | Chi tiết SP    | Click product → view detail    | ⬜ PASS / ⬜ FAIL |
| 6   | Thêm giỏ hàng  | Click "Thêm vào giỏ"           | ⬜ PASS / ⬜ FAIL |
| 7   | Xem giỏ hàng   | /cart → view items             | ⬜ PASS / ⬜ FAIL |
| 8   | Cập nhật SL    | Change quantity in cart        | ⬜ PASS / ⬜ FAIL |
| 9   | Xóa khỏi giỏ   | Click remove button            | ⬜ PASS / ⬜ FAIL |
| 10  | Checkout COD   | /checkout → COD → confirm      | ⬜ PASS / ⬜ FAIL |
| 11  | Lịch sử đơn    | /profile/orders                | ⬜ PASS / ⬜ FAIL |
| 12  | Admin login    | /admin/login                   | ⬜ PASS / ⬜ FAIL |
| 13  | Admin products | /admin/products → CRUD         | ⬜ PASS / ⬜ FAIL |
| 14  | Tìm kiếm       | Search bar → enter keyword     | ⬜ PASS / ⬜ FAIL |
| 15  | Responsive     | Resize browser window          | ⬜ PASS / ⬜ FAIL |

---

### 🧪 Test Checklist

- [ ] `./mvnw test` - All tests PASS
- [ ] Screenshot terminal BUILD SUCCESS
- [ ] Check `target/surefire-reports/`
- [ ] (Optional) `./mvnw test jacoco:report`
- [ ] (Optional) Open `target/site/jacoco/index.html`

---

### 🐳 Deploy Checklist

#### Frontend:

- [ ] `docker build -t flower-fe .`
- [ ] `docker run -p 3000:80 flower-fe`
- [ ] Test SPA routing (refresh on nested route)
- [ ] Verify API calls work

#### Backend:

- [ ] `docker build -t flower-api .`
- [ ] `docker run -p 8080:8080 -e DB_URL=... flower-api`
- [ ] Check /api endpoint responds
- [ ] Verify Swagger UI accessible

---

## 📎 PHỤ LỤC

### A. Cấu trúc thư mục bằng chứng

```
docbaocao/
├── PROJECT_REVIEW_REPORT.md    # File báo cáo này
├── TESTING_QA_GUIDE.md         # Hướng dẫn testing
├── README_AUDIT.md             # (nếu có)
├── screenshots/
│   ├── test_terminal_success.png
│   ├── surefire_report.png
│   ├── swagger_ui.png
│   └── manual_test_evidence/
│       ├── login_success.png
│       ├── cart_add.png
│       └── checkout_success.png
└── test-reports/
    └── (copy từ target/surefire-reports/)
```

### B. Lệnh thường dùng

```bash
# Backend
cd flower-manager
./mvnw clean package -DskipTests     # Build JAR
./mvnw spring-boot:run               # Run dev
./mvnw test                          # Run tests
./mvnw test jacoco:report            # Tests + coverage

# Frontend
cd flower-shop-frontend
npm install                          # Install deps
npm start                            # Run dev
npm run build                        # Production build
npm test                             # Run tests

# Docker
docker build -t flower-api ./flower-manager
docker build -t flower-fe ./flower-shop-frontend
docker run -p 8080:8080 flower-api
docker run -p 3000:80 flower-fe
```

---

**Reviewer:** Senior Fullstack (Spring Boot + React)  
**Ngày review:** 27/01/2026  
**Phiên bản báo cáo:** 1.0

---

<div align="center">

**✅ DỰ ÁN ĐẠT YÊU CẦU VỚI ĐIỂM SỐ: 9.15/10**

</div>
