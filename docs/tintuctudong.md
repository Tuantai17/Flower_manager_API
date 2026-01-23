Bạn là Senior Fullstack Architect (Spring Boot Java + React JS + MySQL + n8n).
Tôi đang làm website thương mại điện tử shop Hoa tươi chạy LOCAL với stack:

- Backend: Spring Boot Java (port 8080)
- Frontend: React JS (port 5173 hoặc tương tự)
- Database: MySQL chạy bằng XAMPP (port 3306)
- n8n chạy local (ưu tiên Docker, port 5678)

Mục tiêu: TRIỂN KHAI END-TO-END hệ thống “Tự động tạo tin tức/bài viết” kết hợp:

1. Dữ liệu nội bộ từ MySQL (sản phẩm/đơn hàng) để tạo nội dung đúng shop
2. (Tuỳ chọn) Nguồn ngoài (RSS hợp pháp) để lấy chủ đề trend và chỉ dùng làm tham khảo/tóm tắt, KHÔNG sao chép nguyên văn
3. n8n làm điều phối (Cron → query MySQL → tạo context → gọi AI viết bài HTML → gọi API Spring Boot lưu DB → React hiển thị)

Yêu cầu bạn tạo hướng dẫn triển khai CHI TIẾT RÕ RÀNG theo đúng trình tự, copy-paste chạy được, gồm các phần sau:

========================
PHẦN A — DATABASE (MySQL / XAMPP)
=================================

1. Viết SQL tạo bảng:

- news
- news_product_links
  Các cột bắt buộc:
  news: id, title, slug(unique), summary, content_html(LONGTEXT), thumbnail_url, source_type(ENUM: INTERNAL, EXTERNAL_RSS, AI), source_url, status(ENUM: DRAFT, SCHEDULED, PUBLISHED), publish_at, created_at, updated_at
  news_product_links: id, news_id(FK), product_id, rank_num
  Tạo index phù hợp cho status + publish_at.

2. Viết SQL test insert 1 bài và select để kiểm tra.

========================
PHẦN B — SPRING BOOT BACKEND (ENDPOINTS + ENTITY/REPO/SERVICE)
==============================================================

Bạn phải sinh code theo cấu trúc chuẩn Spring Boot:

1. Entity:

- News (map đúng bảng news)
- NewsProductLink (map đúng bảng news_product_links)

2. Repository:

- NewsRepository
- NewsProductLinkRepository

3. DTO:

- NewsAutoDraftRequest: title, summary, contentHtml, sourceType, sourceUrl, thumbnailUrl, publishAt(ISO string nullable), productIds(List<Long> nullable)
- NewsResponse DTO cho public list/detail

4. Service:

- createDraftFromN8n(request): tạo slug tự động (không dấu, lowercase, nối bằng '-', đảm bảo unique bằng cách thêm '-1','-2' nếu trùng)
- linkProductIds(newsId, productIds)
- publishScheduled(): quét status=SCHEDULED và publish_at <= now() → status=PUBLISHED

5. Controller:

- POST /api/news/auto/draft (n8n gọi để lưu bài)
- POST /api/news/auto/publish (tuỳ chọn manual publish)
- GET /api/news (public list, chỉ trả status=PUBLISHED, sort publish_at desc)
- GET /api/news/{slug} (public detail)

6. Bảo mật tối thiểu cho endpoint auto:

- Header x-api-key
- Spring đọc từ env NEWS_AUTOMATION_API_KEY
- Nếu sai key trả 401

7. Cấu hình application.yml để kết nối MySQL XAMPP (ví dụ db flower_shop), bật utf8mb4
8. Hướng dẫn test bằng Postman/curl:

- ví dụ curl gọi /api/news/auto/draft với payload mẫu và header x-api-key
- kiểm tra DB có record

========================
PHẦN C — N8N WORKFLOW END-TO-END (NODE-BY-NODE)
===============================================

Bạn phải mô tả chính xác từng node, cấu hình field nào, mapping ra sao:
Workflow 1: Generate News Daily

1. Cron: chạy mỗi ngày 08:00
2. MySQL node: query dữ liệu nội bộ:

- Top 5 sản phẩm bán chạy 7 ngày (join orders + order_items + products)
- Top 3 sản phẩm mới nhất
  Xuất JSON rõ ràng: products array gồm {id,name,price,soldCount,tags(optional)}

3. (Tuỳ chọn) RSS Read hoặc HTTP Request lấy 3-5 chủ đề trend về hoa (chỉ title + link)
4. Function node: chọn 1 topic (ưu tiên nếu có RSS thì chọn title đầu; nếu không có thì tạo topic theo dịp: sinh nhật/kỷ niệm/khai trương...)
5. AI node (bạn viết theo hướng trung lập provider):

- Prompt mẫu tiếng Việt như biên tập viên
- Input: topic + bestSellerSummary + products list
- Output: HTML (h1/h2/p/ul/li), có “Gợi ý sản phẩm tại shop” cuối bài
- Cấm sao chép nguyên văn nguồn ngoài

6. Function node: tạo payload đúng DTO NewsAutoDraftRequest, gồm productIds lấy từ top 5
7. HTTP Request node: POST [http://host.docker.internal:8080/api/news/auto/draft](http://host.docker.internal:8080/api/news/auto/draft) (nếu n8n chạy docker) hoặc [http://localhost:8080/](http://localhost:8080/)... (nếu chạy native)

- headers: Content-Type: application/json; x-api-key: <value>
- body: mapping từ bước 6

8. Node cuối: log/notify thành công

Workflow 2: Publisher (tuỳ chọn nếu không dùng Spring scheduled)

- Cron mỗi 5 phút gọi POST /api/news/auto/publish hoặc gọi một endpoint publishScheduled
  Hoặc hướng dẫn dùng @Scheduled trong Spring để khỏi cần workflow 2.

Bạn cũng phải ghi rõ:

- Nếu n8n chạy Docker thì dùng host.docker.internal
- Nếu n8n chạy Windows thì dùng localhost
- Cách set ENV x-api-key trong n8n credentials hoặc node

========================
PHẦN D — REACT HIỂN THỊ NEWS (TỐI THIỂU)
========================================

1. Tạo trang danh sách /news: gọi GET /api/news hiển thị title, summary, publish_at
2. Tạo trang chi tiết /news/:slug: gọi GET /api/news/{slug} render HTML an toàn (có cảnh báo XSS và gợi ý dùng DOMPurify)
   Chỉ cần code mẫu tối thiểu.

========================
PHẦN E — CHECKLIST & DEBUG
==========================

- Checklist chạy local end-to-end
- Lỗi thường gặp:
  - n8n không gọi được localhost (docker)
  - CORS
  - sai charset tiếng Việt
  - slug trùng
  - MySQL query top selling sai do schema khác

- Cách kiểm tra từng tầng: DB → Postman → n8n → React

Kết quả mong muốn:
Mỗi ngày 08:00 n8n tự chạy, tạo 1 bài viết HTML và lưu DB qua Spring Boot, status mặc định PUBLISHED hoặc SCHEDULED tùy cấu hình; React hiển thị bài viết ngoài trang News.

Hãy xuất ra hướng dẫn + code snippet đầy đủ theo thứ tự triển khai, rõ ràng, không bỏ bước, có các đoạn copy-paste sẵn.
