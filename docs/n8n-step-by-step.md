# Hướng Dẫn Kích Hoạt Workflow n8n Từ A-Z

Làm theo đúng trình tự dưới đây để có hệ thống tự động hoàn chỉnh mà không cần tạo thủ công từng bước.

## Bước 1: Chuẩn Bị File Workflow

1.  Tôi đã tạo sẵn file workflow chuẩn tại đường dẫn sau trong máy bạn:
    `e:\DeAn_Java_Flowers\flower-manager\docs\auto-news-workflow.json`
2.  Hãy mở file này bằng Notepad hoặc Code Editor và **COPY TOÀN BỘ NỘI DUNG**.

## Bước 2: Import vào n8n

1.  Mở trình duyệt truy cập n8n (thường là `http://localhost:5678`).
2.  Ở màn hình **Workflows**, bấm nút **"Add workflow"** (hoặc nút `+` màu cam).
3.  Tìm menu (thường là dấu 3 chấm `...` ở góc trên bên phải) -> Chọn **"Import from File"** hoặc **"Import from URL"**.
    - _Cách nhanh nhất_: Chỉ cần click vào vùng trống của workflow và ấn `Ctrl + V` (Paste). Toàn bộ node sẽ hiện ra.

## Bước 3: Cấu Hình Credential (Quan Trọng)

Các node hiện ra sẽ có dấu chấm than đỏ ❗ vì chưa có mật khẩu kết nối. Bạn cần click vào từng node để điền.

### 1. Node "Get Newest Products" (MySQL)

- Double click vào node này.
- Mục **Credential**, chọn **Create New**.
- Điền thông tin XAMPP MySQL của bạn:
  - **Host**: `host.docker.internal` (Nếu n8n chạy Docker) hoặc `localhost`.
  - **Port**: `3306`.
  - **User**: `root`.
  - **Password**: (Để trống nếu mặc định XAMPP) hoặc điền pass của bạn.
  - **Database**: Tên DB của bạn (vd `flower_shop`).
- Ấn **Save** -> Đóng cửa sổ node.

### 2. Node "AI Content Generator" (OpenAI)

- Double click vào node này.
- Mục **Credential**, chọn **Create New**.
- Điền **API Key** từ OpenAI (hoặc Gemini nếu bạn đổi loại node).
- _Lưu ý_: Nếu bạn không có key OpenAI, bạn có thể xóa node này và thay bằng node "HTTP Request" gọi đến một API AI miễn phí khác hoặc tự mock dữ liệu để test.

### 3. Node "Post to Backend" (HTTP Request)

- Double click vào node này.
- Kiểm tra URL: `http://host.docker.internal:8080/api/public/articles`.
- Nếu Backend bạn chạy port khác 8080, hãy sửa lại số port.
- Không cần Credential cho node này (vì API public).

## Bước 4: Chạy Thử (Test)

1.  Ấn nút **"Execute Workflow"** (nút Play ▶️) ở dưới cùng màn hình.
2.  Quan sát các node chạy:
    - ✅ Xanh lá: Thành công.
    - ❌ Đỏ: Lỗi. Bấm vào node đỏ để xem chi tiết lỗi.

## Bước 5: Kích Hoạt Tự Động

1.  Nếu test thành công, hãy bật công tắc **Inactive** sang **Active** (màu xanh lá) ở góc trên bên phải màn hình.
2.  Hệ thống sẽ tự động chạy mỗi ngày theo lịch đã cài ở node đầu tiên (Schedule Trigger).

---

**Mẹo xử lý lỗi thường gặp:**

- **Lỗi kết nối MySQL**: Đảm bảo user MySQL cho phép kết nối từ xa (`%`). Nếu dùng XAMPP, mặc định root chỉ cho phép localhost. Bạn có thể cần tạo user mới.
- **Lỗi kết nối Backend**: Đảm bảo Spring Boot đang chạy và port 8080 đang mở.
