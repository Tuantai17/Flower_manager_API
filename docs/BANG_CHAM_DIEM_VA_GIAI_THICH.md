# BẢNG CHẤM ĐIỂM VÀ GIẢI THÍCH CHI TIẾT DỰ ÁN

**Môn học**: Phát triển ứng dụng Web (Spring Boot)
**Đề tài**: Quản lý cửa hàng hoa

---

Dựa trên yêu cầu của đề bài và mã nguồn hiện tại, dưới đây là bảng đánh giá chi tiết từng tiêu chí:

## 1. OOP - Lập trình hướng đối tượng (2 điểm)

| Tiêu chí                     | Điểm | Bằng chứng trong Code (File References)                               | Giải thích chi tiết                                                                                                                                                                                      |
| :--------------------------- | :--: | :-------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Kế thừa (Inheritance)**    | Đạt  | `ResourceNotFoundException.java`<br>`User.java`                       | - Class `ResourceNotFoundException` kế thừa `RuntimeException`.<br>- Class `User` implements (kế thừa logic) interface `UserDetails` của Spring Security.                                                |
| **Đóng gói (Encapsulation)** | Đạt  | `Product.java`, `User.java`                                           | - Các thuộc tính (`private id`, `private name`...) được ẩn giấu.<br>- Truy cập thông qua Getter/Setter (Annotation `@Data` của Lombok).<br>- Validation logic trong Setter hoặc PrePersist (`onCreate`). |
| **Đa hình (Polymorphism)**   | Đạt  | `ProductService.java` (Interface)<br>`ProductServiceImpl.java` (Impl) | - Controller gọi `ProductService` (Interace) nhưng thực tế chạy code của `ProductServiceImpl`.<br>- Override phương thức `getAuthorities()` trong `User`.                                                |
| **Trừu tượng (Abstraction)** | Đạt  | `ProductService.java`                                                 | - Interface định nghĩa các hành vi nghiệp vụ (`create`, `getById`) mà không quan tâm chi tiết cài đặt bên trong.                                                                                         |

**Đánh giá:** ✅ **2/2 điểm**. Thể hiện tốt các tính chất cơ bản.

---

## 2. HATEOAS (1 điểm)

| Tiêu chí              | Điểm | Bằng chứng trong Code    | Giải thích chi tiết                                                                                                                                                                                                              |
| :-------------------- | :--: | :----------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Implement HATEOAS** | Đạt  | `ProductController.java` | - Phương thức `addLinks(ProductDTO product)` thêm các đường dẫn liên quan vào response.<br>- Sử dụng `WebMvcLinkBuilder.linkTo(...)` chuẩn của Spring HATEOAS.<br>- Response JSON trả về có trường `_links` (self, category...). |

**Đánh giá:** ✅ **1/1 điểm**. Đã cài đặt đúng kỹ thuật HATEOAS.

---

## 3. Các câu truy vấn (2 điểm)

| Tiêu chí              | Điểm | Bằng chứng trong Code (`ProductRepository.java`)       | Giải thích chi tiết                                                                                                                                                                                                             |
| :-------------------- | :--: | :----------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Truy vấn cơ bản**   | Đạt  | `findBySlug`<br>`findByStatusOrderByCreatedAtDesc`     | Sử dụng **Derived Query Methods** (Spring Data JPA tự sinh query dựa trên tên hàm).                                                                                                                                             |
| **Truy vấn phức tạp** | Đạt  | `@Query("SELECT p FROM Product p LEFT JOIN FETCH...")` | - **JOIN**: `LEFT JOIN FETCH p.category` (tránh N+1 query).<br>- **Dynamic Logic**: Check null (`:keyword IS NULL OR...`) để filter linh động.<br>- **Aggregation**: `GROUP BY` và `SUM` trong hàm `findBestSellingProductIds`. |

**Đánh giá:** ✅ **2/2 điểm**. Truy vấn rất mạnh, xử lý tốt cả Performance (N+1) và Complex Business Logic (Best selling).

---

## 4. Giao diện (1 điểm)

| Tiêu chí        | Điểm | Bằng chứng                    | Giải thích chi tiết                                                                                                                                     |
| :-------------- | :--: | :---------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Frontend UI** | Đạt  | Folder `flower-shop-frontend` | - Dự án có source code Frontend riêng biệt dùng **Next.js**.<br>- Cấu trúc đầy đủ (`src/pages`, `components`...).<br>- Có tích hợp gọi API tới Backend. |

**Đánh giá:** ✅ **1/1 điểm**. Có giao diện hoàn chỉnh (vượt yêu cầu chỉ cần Thymeleaf đơn giản).

---

## 5. Unit Test (1 điểm)

| Tiêu chí         | Điểm | Bằng chứng trong Code     | Giải thích chi tiết                                                                                                                                                                                                             |
| :--------------- | :--: | :------------------------ | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Test Service** | Đạt  | `ProductServiceTest.java` | - Sử dụng **Mockito** để giả lập `ProductRepository`.<br>- Test case đầy đủ: Thành công (`create_ShouldReturn...`) và Thất bại (`ThrowException_WhenSlugExists`).<br>- Sử dụng assertions chuẩn `assertEquals`, `assertThrows`. |

**Đánh giá:** ✅ **1/1 điểm**. Code test viết chuẩn, đúng mô hình Unit Test độc lập.

---

## 6. Security (1 điểm)

| Tiêu chí           | Điểm | Bằng chứng trong Code          | Giải thích chi tiết                                                                                                                                                    |
| :----------------- | :--: | :----------------------------- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Authentication** | Đạt  | `JwtAuthenticationFilter.java` | Xác thực người dùng thông qua **JWT Token** (Stateless).                                                                                                               |
| **Authorization**  | Đạt  | `SecurityConfig.java`          | - Phân quyền rõ ràng: `/api/admin/**` chỉ cho `ADMIN`.<br>- Public endpoint: `/api/products/**` cho khách.<br>- Cơ chế `PasswordEncoder` (BCrypt) để bảo mật mật khẩu. |

**Đánh giá:** ✅ **1/1 điểm**. Cấu hình bảo mật mức độ sản phẩm thật (Production-grade) với JWT.

---

# TỔNG KẾT

**Tổng điểm dự kiến: 8/8 điểm (Phần thực hành)**
_(Chưa tính 2 điểm câu hỏi lý thuyết trả lời miệng)_

**Nhận xét chung:**
Dự án được đầu tư bài bản, vượt xa yêu cầu của một bài kiểm tra giữa kỳ thông thường.

- **Điểm mạnh**: Code clean, chia package hợp lý, truy vấn tối ưu, có Frontend xịn.
- **Điểm yếu (cần lưu ý khi vấn đáp)**:
  - Cần giải thích rõ tại sao `Product` không extends một `BaseEntity` chung (có thể trả lời là do thiết kế đơn giản hoá cho bài giữa kỳ, hoặc do các entity khác nhau quá).
  - Security dùng JWT khá phức tạp, cần nắm chắc lồng hoạt động của `Filter` để trả lời giáo viên.

---

**Lời khuyên cho buổi báo cáo:**
Bạn nên mở sẵn các file sau để demo ngay khi giáo viên hỏi:

1.  **OOP**: Mở `Product.java` (Encapsulation) và `ProductServiceImpl.java` (Polymorphism).
2.  **Query**: Mở `ProductRepository.java`, chỉ vào câu `@Query` dài nhất.
3.  **HATEOAS**: Mở Postman hoặc chạy thử GET `/api/products/{id}` để show field `_links`.
