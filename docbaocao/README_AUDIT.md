# BÁO CÁO RÀ SOÁT ĐỒ ÁN CUỐI KHÓA – WEBSITE TMĐT (Spring Boot + React)

> Mục tiêu: Đối chiếu dự án với **phiếu tính điểm tham khảo** và yêu cầu đề tài.
> Quy tắc: Báo cáo dựa trên **bằng chứng** (file path, endpoint, screenshot, log).

---

## 0. Thông tin dự án

- Tên dự án:
- Thành viên:
- Link repo (nếu có):
- Link deploy FE:
- Link deploy BE:
- Công nghệ:
  - Backend:
  - Frontend:
  - Database:
  - Deploy:

---

# A. Tóm tắt kiến trúc

## A1. Backend (Spring Boot)

- Kiến trúc: Controller / Service / Repository
- Authentication: (JWT/OAuth/…)
- Modules chính: Product, Category, User, Cart, Order, Payment, Upload, Voucher, ...
- Exception/Error handling:
- DB schema (số bảng, quan hệ chính):

## A2. Frontend (React)

- Framework: CRA/Vite/...
- Routing:
- State management:
- Các trang chính: Home, Product list/detail, Cart, Checkout, Login/Register, Profile/Orders, Admin (nếu có)

## A3. DevOps/Deploy

- Docker: có/không
- Biến môi trường:
- Cách chạy local:
- Cách deploy:

---

# B. Bảng kiểm theo phiếu tính điểm (ĐẠT/CHƯA ĐẠT/THIẾU)

> Ghi chú: “Bằng chứng” bắt buộc là **file path / endpoint / screenshot / log**.

## B1. Core Features (3 điểm)

| Hạng mục              | Trạng thái               | Bằng chứng                                  |
| --------------------- | ------------------------ | ------------------------------------------- |
| Product management    | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU | (VD: ProductController.java, /api/products) |
| Category management   | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                                             |
| User (Register/Login) | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                                             |
| Order management      | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                                             |
| Cart                  | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                                             |

## B2. Advanced Features >= 2 (2.5 điểm)

| Hạng mục nâng cao      | Trạng thái               | Bằng chứng |
| ---------------------- | ------------------------ | ---------- |
| JWT                    | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Payment (mock/real)    | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Upload ảnh             | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| OAuth                  | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Search/Filter nâng cao | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Admin dashboard        | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Thống kê               | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Khác: **\_\_**         | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |

✅ Tổng số advanced đạt: \_\_\_ (yêu cầu >= 2)

## B3. Chất lượng mã & Kiến trúc (1.5 điểm)

| Tiêu chí                         | Trạng thái               | Bằng chứng |
| -------------------------------- | ------------------------ | ---------- |
| Controller/Service/Repository rõ | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| JPA đúng (mapping/transaction)   | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| DTO                              | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Xử lý lỗi (ControllerAdvice)     | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| SOLID/DRY, module rõ             | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |

## B4. Testing & QA (1 điểm)

| Tiêu chí               | Trạng thái               | Bằng chứng              |
| ---------------------- | ------------------------ | ----------------------- |
| Unit tests             | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU | (VD: src/test/java/...) |
| Integration tests      | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                         |
| Test cases + test data | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                         |
| Test report            | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                         |
| Manual checklist       | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |                         |

## B5. UI/UX (0.5 điểm)

| Tiêu chí         | Trạng thái               | Bằng chứng   |
| ---------------- | ------------------------ | ------------ |
| UI rõ ràng       | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU | (ảnh/screen) |
| Responsive       | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |              |
| Form validation  | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |              |
| Trải nghiệm mượt | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |              |

## B6. Tài liệu & Trình bày (0.5 điểm)

| Tiêu chí                     | Trạng thái               | Bằng chứng |
| ---------------------------- | ------------------------ | ---------- |
| README (setup/run/api docs)  | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Seed data hướng dẫn          | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |
| Git usage (commit/branch/PR) | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU |            |

## B7. Deploy & Quản lý mã nguồn (1 điểm)

| Tiêu chí         | Trạng thái               | Bằng chứng     |
| ---------------- | ------------------------ | -------------- |
| Deploy chạy được | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU | (link deploy)  |
| Env config rõ    | ☐ ĐẠT ☐ CHƯA ĐẠT ☐ THIẾU | (.env.example) |

---

# C. Lỗ hổng / rủi ro / thiếu chuẩn

## C1. Security

- JWT lưu ở đâu? (localStorage/cookie)
- CORS/CSRF
- Phân quyền (ROLE_USER/ROLE_ADMIN)
- Validation dữ liệu

## C2. Data & Transaction

- Ràng buộc dữ liệu
- N+1 query
- Transaction cho checkout

## C3. API & Maintainability

- REST conventions, status codes
- Versioning
- Logging & exception handling

## C4. Deploy

- Env tách biệt dev/prod
- Upload file trên host ephemeral
- Healthcheck

---

# D. Kết luận đạt “Website TMĐT” chưa?

- Trạng thái: **✅ Đạt / ⚠️ Gần đạt / ❌ Chưa đạt**
- Lý do (bám rubric, ghi thiếu gì còn mất điểm)

---

# E. Roadmap theo giai đoạn (Mục tiêu – Đầu ra – Ưu tiên – Độ khó)

| Giai đoạn | Mục tiêu          | Đầu ra                         | Ưu tiên | Độ khó  |
| --------- | ----------------- | ------------------------------ | ------- | ------- |
| Phase 1   | Chốt Core         | Checklist + endpoints + screen | Cao     | Dễ/Vừa  |
| Phase 2   | Chốt >=2 Advanced | Demo flow + bằng chứng         | Cao     | Vừa     |
| Phase 3   | QA/Testing        | Test report + manual checklist | Trung   | Vừa     |
| Phase 4   | README + Deploy   | README chuẩn + link deploy     | Cao     | Vừa/Khó |
