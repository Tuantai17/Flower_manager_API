# 🌸 Flower Manager API

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.0-green?style=for-the-badge&logo=spring)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![JWT](https://img.shields.io/badge/JWT-Auth-red?style=for-the-badge)

**Backend API cho FlowerCorner E-Commerce Platform**

</div>

---

## 🚀 Quick Start

### **Option 1: Với Docker (Khuyến nghị)**

Xem hướng dẫn đầy đủ tại [infra-docker/production-ish.md](../infra-docker/production-ish.md)

### **Option 2: Chạy Local**

```bash
# 1. Clone và vào thư mục
cd flower-manager

# 2. Cấu hình environment
cp .env.example .env
# Edit .env với các giá trị thực tế

# 3. Chạy MySQL (có thể dùng Docker)
docker run -d --name mysql-dev \
  -e MYSQL_ROOT_PASSWORD=flower123 \
  -e MYSQL_DATABASE=java_flower \
  -p 3306:3306 mysql:8.0

# 4. Chạy Spring Boot
./mvnw spring-boot:run
```

**API sẽ chạy tại:** http://localhost:8080

---

## 📋 Tech Stack

| Technology      | Version | Description             |
| --------------- | ------- | ----------------------- |
| Spring Boot     | 3.4.0   | Application Framework   |
| Spring Security | 6.x     | JWT Authentication      |
| Spring Data JPA | 3.x     | Database ORM            |
| MySQL           | 8.0     | Database                |
| Lombok          | Latest  | Code Generation         |
| Cloudinary      | 1.36    | Image Storage           |
| Gemini AI       | 1.2     | AI Chatbot              |
| WebSocket       | STOMP   | Real-time Communication |
| Swagger         | 2.7     | API Documentation       |

---

## 🔧 Cấu hình

### **Environment Variables (.env)**

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

# Email
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Google
GOOGLE_CLIENT_ID=your_client_id
GEMINI_API_KEY=your_gemini_key

# MoMo Payment
MOMO_PARTNER_CODE=MOMO
MOMO_ACCESS_KEY=your_access_key
MOMO_SECRET_KEY=your_secret_key
```

---

## 📁 Project Structure

```
src/main/java/com/flower/manager/
├── config/           # Spring Configurations
│   ├── SecurityConfig.java
│   ├── WebSocketConfig.java
│   └── CorsConfig.java
├── controller/       # REST Controllers
│   ├── AuthController.java
│   ├── ProductController.java
│   ├── OrderController.java
│   └── AdminController.java
├── service/          # Business Logic
│   ├── impl/
│   └── interfaces/
├── repository/       # JPA Repositories
├── entity/           # Database Entities
├── dto/              # Data Transfer Objects
│   ├── request/
│   └── response/
├── security/         # JWT & OAuth
│   ├── JwtTokenProvider.java
│   └── JwtAuthFilter.java
└── exception/        # Exception Handlers
```

---

## 🔌 API Endpoints

### **Swagger UI:** http://localhost:8080/swagger-ui.html

### **Main APIs**

| Method | Endpoint               | Description            | Auth     |
| ------ | ---------------------- | ---------------------- | -------- |
| POST   | `/api/auth/login`      | Đăng nhập              | ❌       |
| POST   | `/api/auth/register`   | Đăng ký                | ❌       |
| GET    | `/api/products`        | Lấy danh sách sản phẩm | ❌       |
| GET    | `/api/products/{id}`   | Chi tiết sản phẩm      | ❌       |
| GET    | `/api/cart`            | Lấy giỏ hàng           | ✅       |
| POST   | `/api/cart/add`        | Thêm vào giỏ           | ✅       |
| POST   | `/api/orders`          | Tạo đơn hàng           | ✅       |
| GET    | `/api/admin/dashboard` | Dashboard              | ✅ Admin |

---

## 🧪 Testing

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=ProductServiceTest

# Run with coverage
./mvnw test jacoco:report
```

---

## 🐳 Docker

### **Build Docker Image**

```bash
docker build -t flower-manager-api .
```

### **Run Container**

```bash
docker run -d --name flower-api \
  -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/java_flower \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  flower-manager-api
```

---

## 📝 Development Notes

### **Database Migration**

JPA tự động tạo/cập nhật schema với `spring.jpa.hibernate.ddl-auto=update`

### **Logging**

```bash
# Development: SQL queries logged
logging.level.org.hibernate.SQL=DEBUG

# Production: Reduce noise
logging.level.org.hibernate.SQL=INFO
```

### **Hot Reload**

DevTools enabled - tự động restart khi code thay đổi

---

## 📄 License

MIT License
