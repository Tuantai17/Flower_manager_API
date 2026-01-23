Đề giữa kỳ (cá nhân): Tạo project quản lý nhỏ (đề tài tự chọn loại bất kỳ) sử dụng Spring Boot,
MySQL hoặc PostgerSQL, tầm từ 5 bảng, cấu trúc dự án hoàn chỉnh hợp lý. Câu hỏi lý thuyết
từng phần. (2 điểm)

1. (2 điểm) thể hiện được tính kế thừa, đóng gói, đa hình, trừu tượng
2. (1 điểm) HATEOAS
3. (2 điểm) Các câu truy vấn cơ bản, câu truy vấn phức tạp
4. (1 điểm) Giao diện ( nếu có)
5. (1 điểm) Unit test
6. (1 điểm) Security

Bạn là **Senior Backend Java (Spring Boot) + MySQL/PostgreSQL + Testing + Security**.
Hãy giúp tôi **chuẩn bị kiểm tra giữa kì** theo yêu cầu sau và xuất ra **kế hoạch + checklist + mẫu code khung** để tôi làm nhanh.

## 1) Đề bài (bắt buộc)

Tạo **project quản lý nhỏ** (đề tài tự chọn) dùng **Spring Boot + MySQL hoặc PostgreSQL**.

- Có **từ 5 bảng trở lên**
- **Cấu trúc dự án** rõ ràng, hợp lý
- Có phần **câu hỏi lý thuyết từng phần**
- Điểm theo tiêu chí:
  1. OOP: kế thừa, đóng gói, đa hình, trừu tượng (2đ)
  2. HATEOAS (1đ)
  3. Query: truy vấn cơ bản + truy vấn phức tạp (2đ)
  4. Giao diện (nếu có) (1đ)
  5. Unit test (1đ)
  6. Security (1đ)

## 2) Output tôi cần (trình bày rõ ràng, copy dùng được)

### A. Chọn đề tài + mô tả nghiệp vụ

- Đề xuất **1 đề tài phù hợp kiểm tra** (ưu tiên dễ làm nhanh), ví dụ: _Quản lý Thư viện / Quản lý Lớp học / Quản lý Bán hàng mini / Quản lý Công việc (Todo) nâng cao_.
- Mô tả nghiệp vụ ngắn gọn 8–12 dòng.

### B. Thiết kế database (>= 5 bảng)

- Liệt kê **tên bảng + cột + khóa chính/ngoại + quan hệ** (1-n, n-n nếu có).
- Gợi ý index quan trọng.
- Có sẵn **SQL tạo bảng** (MySQL hoặc PostgreSQL, chọn 1).

### C. Kiến trúc dự án Spring Boot (chuẩn)

- Cấu trúc package đề xuất:
  - controller, service, service/impl, repository, entity, dto, mapper, exception, config, security

- Giải thích ngắn lý do.

### D. OOP (2đ) – phải “thể hiện được”

Tạo ví dụ OOP ngay trong project:

- **Trừu tượng**: interface/abstract class cho nghiệp vụ (vd: BaseService, NotificationSender, PaymentMethod…)
- **Kế thừa**: lớp con extends lớp cha (vd: BaseEntity -> User, Book…)
- **Đa hình**: nhiều implementation cho 1 interface (vd: EmailSender/SmsSender)
- **Đóng gói**: private field + getter/setter + validation
  ➡️ Hãy đưa ra **đúng đoạn code mẫu** thể hiện rõ 4 ý trên.

### E. REST API + HATEOAS (1đ)

- Thiết kế API CRUD cho ít nhất 2–3 resource.
- Áp dụng HATEOAS cho 1 resource chính:
  - ví dụ: GET /api/books/{id} trả về \_links: self, update, delete, list…

- Cho tôi **code controller mẫu** có HATEOAS.

### F. Query (2đ)

Tạo danh sách truy vấn:

1. **Cơ bản**: findById, findAll, findByNameContaining, filter theo status/date…
2. **Phức tạp** (ít nhất 3):
   - JOIN nhiều bảng + điều kiện
   - GROUP BY / HAVING (thống kê)
   - Subquery hoặc native query
   - Phân trang + sort + filter động (Specification nếu được)
     ➡️ Viết luôn **JPQL/Query Method/Native SQL** tương ứng.

### G. Giao diện (1đ) (nếu có)

Đưa 2 lựa chọn:

- (Option 1) Swagger/OpenAPI UI (nhanh nhất, tính là giao diện)
- (Option 2) Một trang HTML/Thymeleaf đơn giản (list + create)
  ➡️ Ưu tiên cách nhanh để ăn điểm.

### H. Unit Test (1đ)

- Viết tối thiểu:
  - 1 test cho Service (Mockito)
  - 1 test cho Controller (MockMvc)

- Cung cấp code mẫu test.

### I. Security (1đ)

- Chọn phương án dễ: **Spring Security + JWT** hoặc Basic Auth (nếu chấp nhận).
- Có:
  - đăng ký/đăng nhập
  - phân quyền USER/ADMIN
  - chặn endpoint admin

- Cung cấp config mẫu + flow.
- Nếu JWT: nêu rõ token ở header “Authorization: Bearer …”.

### J. Kế hoạch học & làm trong 3 ngày (siêu thực tế)

- Day 1: DB + Entity + CRUD
- Day 2: HATEOAS + Query phức tạp + Swagger UI
- Day 3: Security + Test + dọn đẹp README
  ➡️ Mỗi ngày có checklist.

### K. Bộ câu hỏi lý thuyết (để tôi trả lời miệng)

Soạn theo đúng 6 mục chấm điểm:

- OOP: giải thích + ví dụ trong project
- HATEOAS: là gì, lợi ích
- Query: JPQL vs Native, JOIN, GROUP BY
- UI: Swagger là gì
- Unit test: Mockito vs Integration test
- Security: Authentication vs Authorization, JWT là gì

## 3) Ràng buộc

- Dùng **Java 17+**, Spring Boot 3.x
- Có thể chạy bằng Maven
- Không lan man, ưu tiên “đủ điểm – dễ làm – đúng tiêu chí”
- Trình bày theo mục A → K, có code block rõ ràng
