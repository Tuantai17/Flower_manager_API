Bạn là Senior Fullstack Architect (Spring Boot + React Vite JS + MySQL) kiêm DevOps. Nhiệm vụ: đọc và hiểu project của tôi để lập kế hoạch deploy MIỄN PHÍ, ưu tiên “dễ làm – copy/paste – chạy được”.

BỐI CẢNH

- Backend: Spring Boot (Java), build bằng Maven/Gradle (hãy tự phát hiện).
- Frontend: React dùng Vite.
- Database hiện tại: MySQL chạy bằng XAMPP trên máy local.
- Mục tiêu deploy miễn phí: FE + BE public, có DB cloud miễn phí (không dùng XAMPP local khi deploy).

YÊU CẦU ĐỌC DỮ LIỆU PROJECT (CHỈ ĐỌC, KHÔNG SỬA FILE)

1. Liệt kê cấu trúc thư mục quan trọng:
   - Root tree (tối đa 4 cấp)
   - Backend: src/main/java, src/main/resources, pom.xml/build.gradle, Dockerfile (nếu có)
   - Frontend: package.json, vite.config._, src, public, .env_ (nếu có)

2. Trích xuất cấu hình cần cho deploy (chỉ trích nội dung liên quan):
   - Backend: server.port, spring.datasource.\*, profiles, CORS config, base-path/api prefix, security config (JWT nếu có).
   - Frontend: base API URL (VITE\_\*), proxy cấu hình, routes, build output (dist).

3. Xác định cách chạy local hiện tại:
   - Lệnh chạy backend (mvn spring-boot:run / gradle bootRun / java -jar)
   - Lệnh chạy frontend (npm start/build/preview)
   - DB: host/port/user/dbname hiện đang dùng.

4. Xác định các “điểm dễ lỗi khi deploy”:
   - CORS, cookies/JWT, redirect OAuth (nếu có), websocket, file upload, đường dẫn static, env variables.
   - Phụ thuộc vào localhost/XAMPP.

5. Sau khi đọc xong, xuất ra 3 phần:
   A. Tóm tắt kiến trúc (Backend/Frontend/DB) + ports + endpoints chính.
   B. Phương án deploy MIỄN PHÍ tốt nhất (chọn 1):
   - FE: Vercel hoặc Netlify
   - BE: Render (hoặc Railway nếu phù hợp free tier)
   - DB: MySQL/MariaDB free (Railway/Freemysqlhosting/PlanetScale nếu còn) — ưu tiên ổn định, dễ làm.
     C. Checklist triển khai step-by-step (copy/paste commands), gồm:
   - Chuẩn hoá backend đọc PORT từ env, dùng profile prod, env SPRING*DATASOURCE*\*
   - Tạo .env.production cho FE với VITE_API_URL
   - Build backend ra jar + build FE ra dist
   - Thiết lập biến môi trường trên nền tảng deploy
   - Test sau deploy (health endpoint, gọi API từ FE, kiểm tra CORS)

ĐỊNH DẠNG TRẢ LỜI

- Trả lời bằng tiếng Việt.
- Có tiêu đề rõ ràng: “Kết quả đọc project”, “Vấn đề có thể gặp”, “Phương án deploy miễn phí”, “Checklist lệnh”.
- Mỗi bước checklist có lệnh cụ thể cho Windows CMD/PowerShell (ưu tiên CMD).
- Nếu thiếu thông tin quan trọng, hãy đưa ra 3 câu hỏi ngắn gọn nhất để tôi cung cấp, nhưng vẫn phải đưa phương án tạm thời theo giả định hợp lý.
