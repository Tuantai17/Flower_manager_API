# HỆ THỐNG TIN TỨC - HƯỚNG DẪN TRIỂN KHAI

## 📋 TỔNG QUAN

### Level 1+2: Admin CRUD + Scheduled Publishing ✅

### Level 3: AI Generate với Gemini ✅

---

## 🗂️ CÁC FILE ĐÃ TẠO/CẬP NHẬT

### Backend (Spring Boot)

| File                                         | Mô tả                                      |
| -------------------------------------------- | ------------------------------------------ |
| `docs/sql/V2__articles_status_schedule.sql`  | SQL migration thêm cột mới                 |
| `enums/ArticleStatus.java`                   | Enum DRAFT, SCHEDULED, PUBLISHED, ARCHIVED |
| `entity/Article.java`                        | Entity cập nhật với các cột mới            |
| `dto/article/ArticleCreateRequest.java`      | DTO tạo bài viết                           |
| `dto/article/ArticleUpdateRequest.java`      | DTO cập nhật bài viết                      |
| `dto/article/ArticleStatusRequest.java`      | DTO thay đổi trạng thái                    |
| `dto/article/ArticleResponse.java`           | DTO response đầy đủ                        |
| `dto/article/ArticleListItemResponse.java`   | DTO response rút gọn                       |
| `dto/article/ArticleAIGenerateRequest.java`  | DTO AI generate request                    |
| `dto/article/ArticleAIGenerateResponse.java` | DTO AI generate response                   |
| `repository/ArticleRepository.java`          | Repository với các query mới               |
| `service/article/ArticleService.java`        | Service CRUD + Schedule                    |
| `service/article/ArticleAIService.java`      | Service AI Gemini                          |
| `controller/NewsController.java`             | Public API /api/news                       |
| `controller/AdminArticleController.java`     | Admin API /api/admin/articles              |
| `config/ArticleScheduler.java`               | Scheduled job tự động publish              |
| `config/GeminiConfig.java`                   | Config cho Gemini API                      |
| `config/SecurityConfig.java`                 | Thêm /api/news/\*\* public                 |

---

## 🚀 HƯỚNG DẪN CHẠY

### Bước 1: Chạy SQL Migration

```sql
-- Chạy file: docs/sql/V2__articles_status_schedule.sql
-- Hoặc để Hibernate tự tạo (ddl-auto=update)
```

### Bước 2: Khởi động Backend

```bash
cd flower-manager
mvn spring-boot:run
```

### Bước 3: Test API

---

## 📡 API ENDPOINTS

### PUBLIC API (Không cần auth)

| Method | Endpoint           | Mô tả                        |
| ------ | ------------------ | ---------------------------- |
| GET    | `/api/news`        | Danh sách bài viết PUBLISHED |
| GET    | `/api/news/{slug}` | Chi tiết bài viết theo slug  |

**Query params cho GET /api/news:**

- `page` (default: 0)
- `size` (default: 10)
- `q` - tìm kiếm theo title
- `tag` - filter theo tag

### ADMIN API (Yêu cầu ROLE_ADMIN)

| Method | Endpoint                          | Mô tả                     |
| ------ | --------------------------------- | ------------------------- |
| GET    | `/api/admin/articles`             | Danh sách tất cả bài viết |
| GET    | `/api/admin/articles/{id}`        | Chi tiết bài viết         |
| POST   | `/api/admin/articles`             | Tạo bài viết mới (DRAFT)  |
| PUT    | `/api/admin/articles/{id}`        | Cập nhật bài viết         |
| DELETE | `/api/admin/articles/{id}`        | Xóa bài viết              |
| PATCH  | `/api/admin/articles/{id}/status` | Thay đổi trạng thái       |
| POST   | `/api/admin/articles/ai-generate` | AI tạo bài viết           |

---

## 📝 VÍ DỤ API CALLS

### 1. Tạo bài viết DRAFT

```http
POST /api/admin/articles
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "Y nghia hoa hong do ngay Valentine",
  "summary": "Hoa hong do tuong trung cho tinh yeu...",
  "content": "<h2>Hoa hong do</h2><p>...</p>",
  "thumbnail": "https://example.com/image.jpg",
  "tags": "hoa hong,valentine,tinh yeu",
  "author": "Admin"
}
```

### 2. Đặt lịch đăng bài

```http
PATCH /api/admin/articles/{id}/status
Content-Type: application/json
Authorization: Bearer {token}

{
  "status": "SCHEDULED",
  "scheduledAt": "2024-02-14T08:00:00"
}
```

### 3. Publish ngay

```http
PATCH /api/admin/articles/{id}/status
Content-Type: application/json
Authorization: Bearer {token}

{
  "status": "PUBLISHED"
}
```

### 4. AI Generate bài viết

```http
POST /api/admin/articles/ai-generate
Content-Type: application/json
Authorization: Bearer {token}

{
  "topic": "Y nghia hoa hong do ngay Valentine",
  "tone": "am ap, tu van",
  "keywords": ["hoa hong", "valentine", "qua tang"],
  "length": "vua",
  "callToAction": true,
  "author": "AI Bot"
}
```

---

## ✅ CHECKLIST NGHIỆM THU

### Level 1+2

- [ ] Tạo bài DRAFT → không hiện trên /api/news
- [ ] Schedule bài sau 2 phút → chưa hiện public
- [ ] Đợi job chạy → bài xuất hiện trên /api/news
- [ ] GET /api/news/{slug} chỉ trả về nếu PUBLISHED
- [ ] Publish Now → hiện ngay
- [ ] Archive → biến mất khỏi public, admin vẫn thấy

### Level 3

- [ ] POST /api/admin/articles/ai-generate → tạo bài DRAFT
- [ ] Bài AI có ai_generated = true
- [ ] Admin chỉnh sửa → schedule → job publish → public thấy

---

## ⚙️ CẤU HÌNH GEMINI

Trong `application.properties`:

```properties
gemini.api-key=${GEMINI_API_KEY:your-api-key}
gemini.model=gemini-2.0-flash
gemini.max-tokens=4096
gemini.timeout=30
gemini.base-url=https://generativelanguage.googleapis.com/v1beta
gemini.temperature=0.7
```

**Giới hạn Free Tier:**

- 60 requests/phút
- 1500 requests/ngày

---

## 🔄 SCHEDULED JOB

Job chạy mỗi phút tại giây 0:

- Tìm bài SCHEDULED có `scheduled_at <= now`
- Đổi thành PUBLISHED
- Set `published_at = now`
- Clear `scheduled_at = null`

Log: `ArticleScheduler: Published X scheduled article(s)`
