# Hướng Dẫn Cấu Hình n8n Tự Động Tạo Tin Tức

Tài liệu này hướng dẫn chi tiết cách cấu hình các Node trong n8n để thực hiện workflow: **Lấy dữ liệu MySQL -> Tạo nội dung bằng AI -> Đăng bài lên Spring Boot Backend**.

## Yêu cầu Tiên quyết

1.  **n8n** đã được cài đặt và đang chạy (Docker hoặc Local).
2.  **MySQL** Backend đang chạy (XAMPP).
3.  **Spring Boot Backend** đang chạy tại port 8080.

---

## Bước 1: Tạo Workflow Mới

1.  Mở n8n (thường là `http://localhost:5678`).
2.  Click **"Add Workflow"**.
3.  Đặt tên: `Auto Generate News`.

## Bước 2: Cấu Hình Node "Schedule Trigger"

Node này sẽ kích hoạt quy trình tự động theo lịch.

- **Tìm Node**: Gõ `Schedule`.
- **Cấu hình**:
  - **Trigger Interval**: `Days` (hoặc `Weeks` tùy nhu cầu).
  - **Time**: `08:00` (hoặc giờ bạn muốn).
  - **Mode**: `Every Day`.

## Bước 3: Cấu Hình Node "MySQL"

Node này lấy dữ liệu sản phẩm để làm ý tưởng viết bài.

- **Tìm Node**: Gõ `MySQL`.
- **Cấu hình Credential**:
  - Click `Select Credential` -> `Create New`.
  - **Host**: `host.docker.internal` (Nếu n8n chạy Docker) hoặc `localhost`.
  - **Choose User/Password**: Nhập user/pass của XAMPP (thường là `root` / rỗng).
  - **Database**: Tên DB của bạn (ví dụ: `flower_shop_db`).
  - **Port**: `3306`.
- **Cấu hình Query**:
  - **Operation**: `Execute Query`.
  - **Query**:
    ```sql
    SELECT name, description, price
    FROM products
    ORDER BY created_at DESC
    LIMIT 5;
    ```
  - Mục đích: Lấy 5 sản phẩm mới nhất để AI tham khảo.

## Bước 4: Cấu Hình Node "AI Agent" (Hoặc HTTP Request gọi OpenAI/Gemini)

Nếu bạn có Key OpenAI/Gemini, dùng node HTTP Request là đơn giản nhất.

**Cách dùng HTTP Request gọi OpenAI API:**

- **Tìm Node**: Gõ `HTTP Request`.
- **Method**: `POST`.
- **URL**: `https://api.openai.com/v1/chat/completions`.
- **Authentication**: `Header Auth`.
  - Name: `Authorization`.
  - Value: `Bearer YOUR_OPENAI_API_KEY`.
- **Body Parameters**:
  - **Send Body**: `JSON`.
  - **Body Content**:
    ```json
    {
      "model": "gpt-3.5-turbo",
      "messages": [
        {
          "role": "system",
          "content": "Bạn là một chuyên gia content marketing cho shop hoa tươi. Hãy viết bài dưới định dạng JSON."
        },
        {
          "role": "user",
          "content": "Dựa vào danh sách sản phẩm sau: {{ JSON.stringify($json) }}, hãy viết một bài blog ngắn hấp dẫn.\n\nYêu cầu output JSON thuần (không markdown):\n{\n  \"title\": \"Tiêu đề bài viết\",\n  \"slug\": \"tieu-de-bai-viet-slug\",\n  \"summary\": \"Tóm tắt ngắn gọn 2 câu.\",\n  \"content\": \"<p>Nội dung chi tiết định dạng HTML...</p>\",\n  \"author\": \"AI Bot\",\n  \"thumbnail\": \"https://example.com/flower.jpg\"\n}"
        }
      ],
      "temperature": 0.7
    }
    ```

_Lưu ý: Bạn có thể dùng **Basic LLM Chain** node nếu dùng bản n8n mới có tích hợp sẵn AI._

## Bước 5: Cấu Hình Node "JSON Parse" (Nếu cần)

Nếu AI trả về chuỗi JSON, bạn cần parse nó ra object.

- **Tìm Node**: `Code` (hoặc `Function`).
- **Javascript Code**:
  ```javascript
  const content = items[0].json.choices[0].message.content;
  return JSON.parse(content);
  ```

## Bước 6: Cấu Hình Node "Post to Backend"

Gửi bài viết đã tạo về Spring Boot API để lưu vào database.

- **Tìm Node**: `HTTP Request`.
- **Method**: `POST`.
- **URL**: `http://host.docker.internal:8080/api/public/articles`
  - _Lưu ý_: Dùng `host.docker.internal` để Docker container nhìn thấy localhost của máy chủ.
- **Authentication**: `None`.
- **Body Parameters**:
  - **Send Body**: `JSON`.
  - **Body Content**:
    ```json
    {
      "title": "{{ $json.title }}",
      "slug": "{{ $json.slug }}",
      "summary": "{{ $json.summary }}",
      "content": "{{ $json.content }}",
      "author": "{{ $json.author }}",
      "thumbnail": "{{ $json.thumbnail }}"
    }
    ```

## Bước 7: Kích Hoạt (Activate)

- Bật công tắc **Active** ở góc trên bên phải.
- Bấm **Execute Workflow** để test thử ngay lập tức.

---

## Kiểm Tra Kết Quả

1.  Vào trang Admin hoặc trang Tin Tức (`http://localhost:5173/news`) để xem bài viết mới tạo.
2.  Kiểm tra Database bảng `articles`.
