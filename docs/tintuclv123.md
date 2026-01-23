Bạn là Senior Fullstack Architect (Spring Boot 3 / Java 21 + React) + Database Designer.

Bối cảnh dự án:

- Website shop hoa, trang /news đang trống (“Hiện chưa có bài viết nào”).
- Database MySQL có sẵn table: articles với các cột hiện tại:
  id, author, content, created_at, slug, summary, thumbnail, title, updated_at
- Tôi muốn triển khai hệ thống Tin tức/Blog theo 2 giai đoạn:
  (1) Level 1+2: Admin CRUD + Scheduled Publishing (miễn phí)
  (2) Level 3: Tích hợp Gemini Free vào Spring Boot để AI generate bài viết (miễn phí), chỉ lưu DRAFT, admin duyệt rồi mới đăng.

NGUYÊN TẮC (bắt buộc):

- Trả lời 100% tiếng Việt.
- Chia rõ theo mục: LEVEL 1+2 (A1..A8) rồi LEVEL 3 (B1..B6).
- Mỗi mục phải có: Mục tiêu → Việc cần làm → API/SQL mẫu → Checklist nghiệm thu.
- Public chỉ xem bài PUBLISHED. Admin mới được CRUD, schedule, publish.
- Không auto publish từ AI. AI chỉ generate và lưu DRAFT.
- Có phân trang list bài viết.
- Ưu tiên “copy-paste chạy được”, nhưng không cần viết toàn bộ project; chỉ đưa khung chuẩn và chỉ rõ file/đường dẫn nên tạo.

======================================================================
A. LEVEL 1+2 — ADMIN CRUD + SCHEDULED PUBLISHING (LÀM TRƯỚC)
======================================================================

A1) CẬP NHẬT DATABASE (tận dụng table articles, KHÔNG tạo table mới)
Mục tiêu: bổ sung cột để hỗ trợ DRAFT/SCHEDULED/PUBLISHED và chuẩn bị cho AI.

Yêu cầu bạn cung cấp:

1. SQL ALTER TABLE articles để thêm tối thiểu các cột:
   - status ENUM('DRAFT','SCHEDULED','PUBLISHED','ARCHIVED') DEFAULT 'DRAFT' NOT NULL
   - scheduled_at DATETIME NULL
   - published_at DATETIME NULL
   - tags VARCHAR(255) NULL (tạm lưu CSV)
   - ai_generated TINYINT(1) DEFAULT 0 NOT NULL
   - ai_prompt TEXT NULL
2. Tạo index quan trọng:
   - unique/idx cho slug
   - idx cho (status, published_at)
   - idx cho (status, scheduled_at)
3. Nêu rõ lưu ý timezone (UTC hay Asia/Ho_Chi_Minh).

A2) ENTITY + DTO (Spring Boot)
Mục tiêu: map đúng table articles hiện có + cột mới.

Yêu cầu bạn cung cấp:

1. Entity Article (JPA) map đúng các cột hiện tại + cột mới.
2. Enum ArticleStatus.
3. DTO:
   - ArticleCreateRequest (title, summary, content, thumbnail, tags)
   - ArticleUpdateRequest (title, summary, content, thumbnail, tags)
   - ArticleStatusRequest (status, scheduledAt optional)
   - ArticleResponse (id, title, slug, summary, content, thumbnail, tags, author, status, createdAt, updatedAt, scheduledAt, publishedAt)
   - ArticleListItemResponse (rút gọn: id, title, slug, summary, thumbnail, publishedAt, tags)

A3) SLUG RULE (bắt buộc chuẩn)
Mục tiêu: slug SEO-friendly và không bị trùng.

Yêu cầu:

- Nêu thuật toán tạo slug từ title (bỏ dấu tiếng Việt, thay khoảng trắng bằng '-', lowercase).
- Nếu slug trùng → tự động thêm '-1', '-2', ...
- Khi sửa title: có thể giữ slug cũ hoặc cập nhật slug theo option (nêu rõ option).
- Nếu FE gọi detail theo slug thì cần đảm bảo slug ổn định.

A4) PUBLIC API (User chỉ xem bài đã đăng)
Mục tiêu: /news hiển thị bài PUBLISHED, không lộ draft.

Cần các endpoint:

1. GET /api/news
   - Query: page, size, q (search title), tag (optional)
   - Sort: publishedAt desc
   - Chỉ trả về status = PUBLISHED
   - Response: dạng phân trang (content[], totalElements, totalPages, page, size)
   - Trả JSON mẫu đầy đủ
2. GET /api/news/{slug}
   - Chỉ trả nếu status=PUBLISHED
   - Nếu không tồn tại hoặc chưa publish → 404
   - Trả JSON mẫu

A5) ADMIN API (CRUD + schedule/publish)
Mục tiêu: Admin quản lý toàn bộ bài viết.

Cần các endpoint:

1. POST /api/admin/articles
   - tạo bài: mặc định DRAFT
2. PUT /api/admin/articles/{id}
   - sửa bài
3. DELETE /api/admin/articles/{id}
   - xóa (hoặc soft delete nếu bạn khuyến nghị)
4. GET /api/admin/articles
   - list tất cả status, có filter status, page/size, search q
5. PATCH /api/admin/articles/{id}/status
   - body: { "status": "...", "scheduledAt": "..." optional }
   - Rule validate:
     - Nếu status=SCHEDULED => scheduledAt bắt buộc và phải > now
     - Nếu chuyển sang PUBLISHED => publishedAt = now, scheduledAt = null
     - PUBLISHED -> ARCHIVED hợp lệ
     - DRAFT -> PUBLISHED hợp lệ (publish ngay)
   - Trả JSON mẫu

A6) SECURITY (phân quyền)
Mục tiêu: Public xem tự do, Admin mới CRUD.

Yêu cầu:

- Nêu rõ cách bảo vệ route:
  /api/admin/** require ROLE_ADMIN
  /api/news/** public
- Nếu project đang có JWT Spring Security: chỉ rõ chỗ cấu hình matcher/authorize.
- Nếu chưa có role admin: nêu cách kiểm tra quyền tối thiểu.

A7) SCHEDULED PUBLISHING JOB (LEVEL 2)
Mục tiêu: tự động đăng bài đúng giờ.

Yêu cầu bạn cung cấp:

1. Code mẫu Spring @Scheduled (mỗi 1 phút hoặc 5 phút):
   - Query: status=SCHEDULED AND scheduled_at <= now
   - Update: status=PUBLISHED, published_at=now, scheduled_at=null
2. Lưu ý chống publish trùng:
   - dùng transaction
   - update có điều kiện (where status='SCHEDULED' ...)
3. Checklist verify job chạy:
   - log “Published X scheduled articles”
   - cách test nhanh: schedule bài sau 2 phút

A8) FRONTEND FLOW (tối thiểu để chạy)
Mục tiêu: /news có dữ liệu và admin quản lý được.

Yêu cầu bạn mô tả:

1. Public:
   - /news: gọi GET /api/news, render list + phân trang + search
   - /news/:slug: gọi GET /api/news/{slug}
   - Nếu list rỗng: hiển thị “Hiện chưa có bài viết nào.”
2. Admin:
   - List bài: filter theo status
   - Form create/edit
   - Nút thao tác: Save Draft / Schedule / Publish Now / Archive
3. Nêu rõ payload gọi API từng nút.

A9) CHECKLIST NGHIỆM THU LEVEL 1+2 (bắt buộc)
Bạn phải liệt kê test manual:

1. Tạo bài DRAFT → không hiện public
2. Schedule bài sau 2 phút → chưa hiện public
3. Đợi job chạy → bài xuất hiện public /news
4. /news/{slug} chỉ mở được khi PUBLISHED
5. Publish Now → hiện ngay
6. Archive → biến mất khỏi public nhưng admin vẫn thấy

Kết thúc Level 1+2: phải kết luận “/news có bài, schedule hoạt động”.

======================================================================
B. LEVEL 3 — TÍCH HỢP GEMINI FREE (AI GENERATE DRAFT, LÀM SAU)
======================================================================

B1) Mục tiêu & nguyên tắc

- AI chỉ generate nội dung.
- Lưu DB thành DRAFT (status=DRAFT).
- Admin xem preview, chỉnh sửa, sau đó schedule/publish.

B2) Cấu hình Gemini trong Spring Boot
Yêu cầu bạn cung cấp:

1. Biến môi trường:
   GEMINI_API_KEY=...
   GEMINI_MODEL=gemini-2.0-flash (hoặc model free tương đương)
2. Nơi cấu hình:
   - application.yml + @ConfigurationProperties
3. Http client:
   - timeout, retry cơ bản
4. Cách xử lý lỗi:
   - key sai, quota, 429, timeouts

B3) ADMIN API AI GENERATE
Endpoint:

- POST /api/admin/articles/ai-generate
  Body:
  {
  "topic": "Ý nghĩa hoa hồng đỏ ngày Valentine",
  "tone": "ấm áp, tư vấn",
  "keywords": ["hoa hồng", "valentine", "quà tặng"],
  "length": "vừa",
  "callToAction": true
  }

Response (AI generated):
{
"title": "...",
"slugSuggestion": "...",
"summary": "...",
"content": "...",
"tagsSuggestion": ["...","..."],
"thumbnailPrompt": "optional"
}

Yêu cầu:

1. Prompt engineering chuẩn cho shop hoa:
   - tiếng Việt, tự nhiên
   - có H2/H3, bullet, CTA
   - không bịa dữ liệu nhạy cảm, không copy nguồn
   - tối ưu SEO nhẹ
2. Sau khi generate:
   - Lưu vào articles:
     title/summary/content/thumbnail(optional)/tags
     status=DRAFT
     ai_generated=1
     ai_prompt lưu lại prompt đã gửi
   - Trả về ArticleResponse

B4) Admin UI tích hợp AI
Yêu cầu mô tả:

- Nút “Tạo bài bằng AI”
- Form nhập topic/tone/keywords
- Preview nội dung
- Nút “Lưu Draft”
- Sau đó dùng lại nút Schedule/Publish của Level 1+2

B5) CHECKLIST NGHIỆM THU LEVEL 3

- Generate AI → bài xuất hiện admin list, status=DRAFT, ai_generated=1
- Public không thấy
- Admin chỉnh sửa → schedule → job publish → public thấy

B6) Kết luận cuối

- Xác nhận hệ thống đạt: CMS Tin tức có quản trị + hẹn giờ + AI hỗ trợ tạo nội dung nháp.

Hãy bắt đầu trả lời theo đúng cấu trúc A1..A9 rồi B1..B6.
Trong mỗi mục, đưa SQL/API/JSON mẫu rõ ràng, và file/class gợi ý theo Spring Boot chuẩn.
