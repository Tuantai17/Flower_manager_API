Kết quả đọc project
A. Tóm tắt kiến trúc
Backend (flower-manager):
Framework: Spring Boot 3.4.0 (Java 21).
Build Tool: Maven (mvnw).
Cấu hình: Chạy port 8080, kết nối MySQL qua 3306.
Features: Spring Security (JWT), Email, Cloudinary (upload ảnh), MoMo, Google Auth/Gemini.
Docker: Đã có Dockerfile chuẩn (Multi-stage build).
Frontend (flower-shop-frontend):
Framework: CRA (Create React App) dùng react-scripts, KHÔNG PHẢI VITE như báo cáo ban đầu.
Cấu hình: Chạy port 3000. Dùng axios, react-router-dom.
Biến môi trường: Đọc từ .env (Prefix REACT*APP*).
B. Vấn đề có thể gặp khi Deploy
Lưu trữ file (uploads/): Backend đang dùng chế độ dual (lưu cả local và cloud). Trên các host miễn phí (Render), ổ cứng là "ephemeral" (tạm thời), file lưu local sẽ bị xóa khi server khởi động lại. Giải pháp: Cần chuyển 100% sang Cloudinary.
Frontend Framework: Do là CRA nên lệnh build là npm run build, output ra thực mục build (không phải dist của Vite).
CORS: Cần cấu hình Backend cho phép domain của Frontend (ví dụ https://my-flower-shop.vercel.app) gọi API.
Database: Database XAMPP local không thể kết nối từ Cloud. Cần một Database MySQL Cloud miễn phí.
C. Phương án deploy MIỄN PHÍ tốt nhất
Database: Aiven for MySQL (Free tier) hoặc TiDB Cloud (Serverless MySQL Free). (Railway hiện tại đã tính phí hoặc trial ngắn hạn).
Backend: Render.com (Web Service).
Support Docker (dùng Dockerfile có sẵn).
Gói Free: Ram 512MB (hơi ít cho Java Spring Boot nhưng chạy demo được, sẽ sleep sau thời gian không hoạt động).
Frontend: Vercel.
Tự động phát hiện React (CRA).
Tốc độ cao, deploy dễ dàng từ Git.
D. Checklist triển khai step-by-step
Dưới đây là checklist các bước bạn cần làm.

Bước 1: Chuẩn bị Database (Cloud)
Đăng ký tài khoản tại TiDB Cloud hoặc Aiven Console.
Tạo mới dịch vụ MySQL.
Lấy thông tin kết nối: Host, Port, Username, Password, Database Name. (Lưu lại để dùng ở bước 2).
Bước 2: Deploy Backend lên Render
Push code flower-manager lên GitHub.
Đăng ký Render.com -> New Web Service.
Connect tới repo GitHub flower-manager.
Chọn Runtime: Docker.
Kéo xuống phần Environment Variables, thêm các biến sau (lấy từ application.properties nhưng đổi giá trị thật):
DB_URL: jdbc:mysql://<HOST_CLOUD>:<PORT>/<DB_NAME>?useSSL=true&serverTimezone=Asia/Ho_Chi_Minh
DB_USERNAME: <USER_CLOUD>
DB_PASSWORD: <PASSWORD_CLOUD>
JWT_SECRET: (Tự nghĩ một chuỗi dài ngẫu nhiên)
CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY, CLOUDINARY_API_SECRET: (Lấy từ Cloudinary dashboard)
FILE_STORAGE_MODE: cloudinary (Bắt buộc trên Render)
SPRING_PROFILES_ACTIVE: prod (hoặc docker)
Các key khác (Google, MoMo, Mail): Nếu cần dùng tính năng đó.
Bấm Create Web Service. Chờ build xong (có thể mất 5-10p).
Sau khi xong, Render sẽ cấp link, ví dụ: https://flower-manager.onrender.com.
Bước 3: Deploy Frontend lên Vercel
Push code flower-shop-frontend lên GitHub.
Đăng ký Vercel.com -> Add New... -> Project.
Import repo flower-shop-frontend.
Tại màn hình Configure Project:
Framework Preset: Chọn Create React App.
Build Command: npm run build (Mặc định).
Output Directory: build (Mặc định, lưu ý: Vite là dist nhưng đây là CRA nên là build).
Environment Variables: Thêm biến sau:
REACT_APP_API_URL: https://flower-manager.onrender.com/api (Link Backend ở bước 2 + /api)
REACT_APP_UPLOAD_URL: https://flower-manager.onrender.com/api/upload
Bấm Deploy.
Bước 4: Kiểm tra và Fix lỗi (Thường gặp)
Lỗi CORS:
Mở trình duyệt vào trang Frontend trên Vercel.
Nếu gọi API bị lỗi đỏ lòm chặn CORS -> Bạn cần vào Backend file SecurityConfig.java hoặc WebConfig.java, sửa allowedOrigins thêm domain của Vercel vào (hoặc để \* tạm thời để test, nhưng khuyến cáo nên set cứng domain).
Sau khi sửa code Backend -> Commit & Push -> Render tự động redeploy.
