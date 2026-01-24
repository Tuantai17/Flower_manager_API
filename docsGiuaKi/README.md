# Tài Liệu Báo Cáo Giữa Kỳ (Documentation Index)

Thư mục này chứa toàn bộ tài liệu kỹ thuật, phân tích và hướng dẫn phục vụ cho báo cáo dự án.

## Danh Sách Tài Liệu

| STT   | Tên Tài Liệu (File)                                      | Mô Tả Nội Dung                                                                                           |
| :---- | :------------------------------------------------------- | :------------------------------------------------------------------------------------------------------- |
| **1** | [2_HATEOAS.md](2_HATEOAS.md)                             | Phân tích chuẩn RESTful HATEOAS và ứng dụng trong dự án (Hypermedia as the Engine of Application State). |
| **2** | [3_TruyVan_CoBan_PhucTap.md](3_TruyVan_CoBan_PhucTap.md) | Tổng hợp các câu truy vấn Database từ cơ bản đến nâng cao (JPA, JPQL, Native Query).                     |
| **3** | [5_Unit_Test.md](5_Unit_Test.md)                         | Các nguyên lý cơ bản về Unit Test, JUnit 5 và Mockito.                                                   |
| **4** | [6_PhanTichLoi_Test.md](6_PhanTichLoi_Test.md)           | Phân tích các lỗi thường gặp khi test (NPE, Stubbing) và cách khắc phục.                                 |
| **5** | [6_PhanTich_TestAuth.md](6_PhanTich_TestAuth.md)         | **(Quan trọng)** Báo cáo chi tiết về quy trình test chức năng Auth (Login, Register, ForgotPW).          |
| **6** | [7_Security.md](7_Security.md)                           | **(Mới)** Phân tích kiến trúc bảo mật Spring Security và JWT (SecurityConfig, Filter, Provider).         |
| **7** | [8_QuanLySanPham.md](8_QuanLySanPham.md)                 | **(Mới)** Phân tích luồng nghiệp vụ Quản lý sản phẩm (Đặc biệt là Update Flow).                          |
| **8** | [9_QuyTrinh_Auth.md](9_QuyTrinh_Auth.md)                 | **(Mới)** Phân tích chi tiết quy trình nghiệp vụ Đăng ký & Đăng nhập (Controller -> Service).            |
| **9** | [10_Validation.md](10_Validation.md)                     | **(Mới)** Phân tích cơ chế Validate 3 lớp: DTO, @Valid (Controller) và GlobalExceptionHandler.           |

## Hướng Dẫn Sử Dụng

- Các file được đánh số để dễ dàng tham chiếu theo thứ tự ưu tiên hoặc logic bài báo cáo.
- File `6_PhanTich_TestAuth.md` và `7_Security.md` là hai tài liệu cốt lõi về chất lượng và bảo mật của dự án.
