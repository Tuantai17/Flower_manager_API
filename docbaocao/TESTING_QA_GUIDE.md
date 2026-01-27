# HƯỚNG DẪN TESTING & QA - ĐỒ ÁN CUỐI KHÓA

> **Mục tiêu:** Đạt trọn **1 điểm** mục Testing & QA trong phiếu chấm điểm
>
> **Tiêu chí cần đạt:**
>
> - ✅ Unit tests
> - ✅ Integration tests
> - ✅ Test cases + test data
> - ✅ Test report
> - ✅ Manual test checklist

---

# PHẦN 1 — TEST REPORT (AUTOMATED TEST)

## 1.1. Test Report là gì?

**Test Report** là bản báo cáo kết quả chạy các bài kiểm thử tự động (Unit Test, Integration Test), cho thấy:

- Số lượng test cases đã chạy
- Số test PASS / FAIL
- Thời gian thực thi
- (Tùy chọn) Coverage - độ phủ mã nguồn

**Giảng viên cần thấy:**

1. ✅ Có test files trong project (`src/test/java/...`)
2. ✅ Lệnh chạy test thành công (screenshot terminal)
3. ✅ Kết quả: BUILD SUCCESS, tất cả test PASS
4. ✅ (Bonus) File report HTML hoặc coverage report

---

## 1.2. Hướng dẫn chạy Test Report

### Bước 1: Mở Terminal tại thư mục Backend

```bash
cd e:\DeAn_Java_Flowers\flower-manager
```

### Bước 2: Chạy Unit Test bằng Maven Wrapper

```bash
# Chạy tất cả tests
./mvnw test

# Hoặc trên Windows:
mvnw.cmd test
```

### Bước 3: Xác nhận Test PASS

Kết quả mong đợi:

```
[INFO] Tests run: X, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Bước 4: Xem Test Report (Surefire)

Sau khi chạy test, Maven tự sinh report tại:

```
target/surefire-reports/
├── TEST-*.xml              # XML report
├── *.txt                   # Text summary
```

---

## 1.3. (Tùy chọn) Sinh Coverage Report với JaCoCo

### Bước 1: Thêm JaCoCo plugin vào `pom.xml`

Trong phần `<build> → <plugins>`, thêm:

```xml
<!-- JaCoCo - Code Coverage -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Bước 2: Chạy test với coverage

```bash
./mvnw clean test jacoco:report
```

### Bước 3: Xem Coverage Report

Mở file: `target/site/jacoco/index.html` trong trình duyệt

---

## 1.4. Bằng chứng cần thu thập

| #   | Bằng chứng                               | File/Screenshot                 | Nơi lưu      |
| --- | ---------------------------------------- | ------------------------------- | ------------ |
| 1   | Screenshot terminal chạy test thành công | `test_result_terminal.png`      | `docbaocao/` |
| 2   | File Surefire report                     | `target/surefire-reports/*.txt` | Tự động sinh |
| 3   | (Bonus) JaCoCo coverage HTML             | `target/site/jacoco/index.html` | Tự động sinh |
| 4   | Screenshot coverage summary              | `coverage_report.png`           | `docbaocao/` |

---

## 1.5. Nội dung copy vào README / Báo cáo

### 📝 Đoạn mô tả Testing Strategy:

```markdown
## 🧪 Testing Strategy

### Automated Testing

Dự án sử dụng các công cụ testing của Spring Boot ecosystem:

- **JUnit 5**: Framework unit testing
- **Mockito**: Mock dependencies cho unit tests
- **Spring Boot Test**: Integration testing với `@SpringBootTest`
- **Spring Security Test**: Testing authentication/authorization

### Test Coverage

Các modules được cover bởi automated tests:

- ✅ AuthService - Đăng ký, đăng nhập, JWT
- ✅ ProductService - CRUD sản phẩm
- ✅ CartService - Thêm/xóa/cập nhật giỏ hàng
- ✅ OrderService - Tạo và quản lý đơn hàng
- ✅ VoucherService - Áp dụng mã giảm giá
- ✅ ProductController - REST API endpoints

### Chạy Tests

\`\`\`bash

# Chạy tất cả tests

./mvnw test

# Chạy test với coverage report

./mvnw clean test jacoco:report

# Xem coverage report

open target/site/jacoco/index.html
\`\`\`
```

### 📝 Đoạn mô tả Test Report:

```markdown
## 📊 Test Report

### Kết quả Automated Test

| Metric           | Giá trị |
| ---------------- | ------- |
| Total Test Cases | 7 files |
| Test PASS        | ✅ All  |
| Test FAIL        | 0       |
| Build Status     | SUCCESS |

### Vị trí Test Files

\`\`\`
src/test/java/com/flower/manager/
├── FlowerManagerApplicationTests.java
├── controller/
│ └── product/ProductControllerTest.java
└── service/
├── auth/AuthServiceImplTest.java
├── cart/CartServiceImplTest.java
├── order/OrderServiceImplTest.java
├── product/ProductServiceTest.java
└── voucher/VoucherServiceImplTest.java
\`\`\`

### Surefire Report

Vị trí: `target/surefire-reports/`
```

---

# PHẦN 2 — MANUAL TEST CHECKLIST

## 2.1. Bảng Manual Test Checklist

### A. CORE FEATURES

| TC ID        | Chức năng                  | Các bước thực hiện                                                                                                                    | Kết quả mong đợi                                             | Kết quả thực tế | Trạng thái        | Ghi chú |
| ------------ | -------------------------- | ------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------ | --------------- | ----------------- | ------- |
| **AUTH-01**  | Đăng ký tài khoản          | 1. Vào trang /register<br>2. Nhập email, password, họ tên<br>3. Click "Đăng ký"                                                       | Hiển thị thông báo thành công, chuyển sang trang login       |                 | ⬜ PASS / ⬜ FAIL |         |
| **AUTH-02**  | Đăng ký - Email đã tồn tại | 1. Vào /register<br>2. Nhập email đã có trong hệ thống<br>3. Click "Đăng ký"                                                          | Hiển thị lỗi "Email đã được sử dụng"                         |                 | ⬜ PASS / ⬜ FAIL |         |
| **AUTH-03**  | Đăng nhập thành công       | 1. Vào trang /login<br>2. Nhập email/password đúng<br>3. Click "Đăng nhập"                                                            | Chuyển về trang chủ, hiển thị tên user                       |                 | ⬜ PASS / ⬜ FAIL |         |
| **AUTH-04**  | Đăng nhập - Sai mật khẩu   | 1. Vào /login<br>2. Nhập email đúng, password sai<br>3. Click "Đăng nhập"                                                             | Hiển thị lỗi "Sai thông tin đăng nhập"                       |                 | ⬜ PASS / ⬜ FAIL |         |
| **AUTH-05**  | Đăng xuất                  | 1. Đăng nhập thành công<br>2. Click "Đăng xuất"                                                                                       | Xóa session, chuyển về trang chủ                             |                 | ⬜ PASS / ⬜ FAIL |         |
| **PROD-01**  | Xem danh sách sản phẩm     | 1. Vào trang /shop                                                                                                                    | Hiển thị danh sách sản phẩm với hình ảnh, tên, giá           |                 | ⬜ PASS / ⬜ FAIL |         |
| **PROD-02**  | Xem chi tiết sản phẩm      | 1. Click vào 1 sản phẩm                                                                                                               | Hiển thị trang chi tiết: ảnh, mô tả, giá, nút thêm giỏ hàng  |                 | ⬜ PASS / ⬜ FAIL |         |
| **PROD-03**  | Lọc theo danh mục          | 1. Vào /shop<br>2. Click chọn 1 category                                                                                              | Chỉ hiển thị sản phẩm thuộc category đó                      |                 | ⬜ PASS / ⬜ FAIL |         |
| **CART-01**  | Thêm sản phẩm vào giỏ      | 1. Xem chi tiết sản phẩm<br>2. Click "Thêm vào giỏ"                                                                                   | Thông báo thành công, icon giỏ hàng cập nhật số lượng        |                 | ⬜ PASS / ⬜ FAIL |         |
| **CART-02**  | Xem giỏ hàng               | 1. Click icon giỏ hàng<br>2. Vào /cart                                                                                                | Hiển thị danh sách sản phẩm trong giỏ, tổng tiền             |                 | ⬜ PASS / ⬜ FAIL |         |
| **CART-03**  | Cập nhật số lượng          | 1. Vào /cart<br>2. Thay đổi số lượng sản phẩm                                                                                         | Số lượng và tổng tiền được cập nhật                          |                 | ⬜ PASS / ⬜ FAIL |         |
| **CART-04**  | Xóa sản phẩm khỏi giỏ      | 1. Vào /cart<br>2. Click nút xóa sản phẩm                                                                                             | Sản phẩm bị remove, tổng tiền cập nhật                       |                 | ⬜ PASS / ⬜ FAIL |         |
| **ORDER-01** | Checkout - Tạo đơn hàng    | 1. Có sản phẩm trong giỏ<br>2. Click "Thanh toán"<br>3. Nhập thông tin giao hàng<br>4. Chọn phương thức thanh toán<br>5. Xác nhận đơn | Đơn hàng được tạo, hiển thị mã đơn hàng                      |                 | ⬜ PASS / ⬜ FAIL |         |
| **ORDER-02** | Xem lịch sử đơn hàng       | 1. Đăng nhập<br>2. Vào /my-orders                                                                                                     | Hiển thị danh sách đơn hàng đã đặt                           |                 | ⬜ PASS / ⬜ FAIL |         |
| **ORDER-03** | Xem chi tiết đơn hàng      | 1. Vào /my-orders<br>2. Click 1 đơn hàng                                                                                              | Hiển thị chi tiết: sản phẩm, số lượng, tổng tiền, trạng thái |                 | ⬜ PASS / ⬜ FAIL |         |

### B. ADVANCED FEATURES

| TC ID         | Chức năng             | Các bước thực hiện                                                                    | Kết quả mong đợi                                 | Kết quả thực tế | Trạng thái        | Ghi chú |
| ------------- | --------------------- | ------------------------------------------------------------------------------------- | ------------------------------------------------ | --------------- | ----------------- | ------- |
| **JWT-01**    | JWT Authentication    | 1. Đăng nhập thành công<br>2. Kiểm tra localStorage/cookie                            | JWT token được lưu trữ                           |                 | ⬜ PASS / ⬜ FAIL |         |
| **JWT-02**    | Token hết hạn         | 1. Xóa token<br>2. Truy cập trang cần auth                                            | Redirect về trang login                          |                 | ⬜ PASS / ⬜ FAIL |         |
| **PAY-01**    | Thanh toán COD        | 1. Checkout<br>2. Chọn "Thanh toán khi nhận hàng"                                     | Đơn hàng tạo thành công, status: PENDING         |                 | ⬜ PASS / ⬜ FAIL |         |
| **PAY-02**    | Thanh toán MoMo       | 1. Checkout<br>2. Chọn "MoMo"<br>3. Redirect sang MoMo                                | Chuyển sang trang thanh toán MoMo                |                 | ⬜ PASS / ⬜ FAIL |         |
| **PAY-03**    | MoMo callback         | 1. Hoàn tất thanh toán MoMo                                                           | Quay về trang kết quả, đơn hàng isPaid=true      |                 | ⬜ PASS / ⬜ FAIL |         |
| **UPLOAD-01** | Upload ảnh sản phẩm   | 1. Admin vào trang thêm sản phẩm<br>2. Chọn file ảnh<br>3. Upload                     | Ảnh được upload lên Cloudinary, hiển thị preview |                 | ⬜ PASS / ⬜ FAIL |         |
| **ADMIN-01**  | Admin - Thêm sản phẩm | 1. Login admin<br>2. Vào Products > Add<br>3. Điền thông tin, upload ảnh<br>4. Submit | Sản phẩm mới xuất hiện trong danh sách           |                 | ⬜ PASS / ⬜ FAIL |         |
| **ADMIN-02**  | Admin - Sửa sản phẩm  | 1. Admin vào Products<br>2. Click Edit<br>3. Sửa thông tin, Save                      | Thông tin được cập nhật                          |                 | ⬜ PASS / ⬜ FAIL |         |
| **ADMIN-03**  | Admin - Xóa sản phẩm  | 1. Admin vào Products<br>2. Click Delete<br>3. Xác nhận                               | Sản phẩm bị xóa khỏi danh sách                   |                 | ⬜ PASS / ⬜ FAIL |         |
| **SEARCH-01** | Tìm kiếm sản phẩm     | 1. Nhập keyword vào ô search<br>2. Enter                                              | Hiển thị sản phẩm có tên/mô tả chứa keyword      |                 | ⬜ PASS / ⬜ FAIL |         |
| **SEARCH-02** | Lọc theo giá          | 1. Vào /shop<br>2. Chọn khoảng giá                                                    | Chỉ hiển thị sản phẩm trong khoảng giá           |                 | ⬜ PASS / ⬜ FAIL |         |
| **VOUCH-01**  | Áp dụng voucher       | 1. Checkout<br>2. Nhập mã voucher<br>3. Click áp dụng                                 | Giảm giá được tính, tổng tiền cập nhật           |                 | ⬜ PASS / ⬜ FAIL |         |
| **VOUCH-02**  | Voucher không hợp lệ  | 1. Checkout<br>2. Nhập mã sai                                                         | Hiển thị lỗi "Mã không hợp lệ"                   |                 | ⬜ PASS / ⬜ FAIL |         |

### C. VALIDATION & ERROR HANDLING

| TC ID      | Chức năng                  | Các bước thực hiện                | Kết quả mong đợi                    | Kết quả thực tế | Trạng thái        | Ghi chú |
| ---------- | -------------------------- | --------------------------------- | ----------------------------------- | --------------- | ----------------- | ------- |
| **VAL-01** | Form validation - Email    | 1. Đăng ký với email không hợp lệ | Hiển thị lỗi validation             |                 | ⬜ PASS / ⬜ FAIL |         |
| **VAL-02** | Form validation - Password | 1. Đăng ký với password < 6 ký tự | Hiển thị lỗi "Password quá ngắn"    |                 | ⬜ PASS / ⬜ FAIL |         |
| **VAL-03** | Giỏ hàng trống             | 1. Checkout khi giỏ hàng trống    | Hiển thị thông báo "Giỏ hàng trống" |                 | ⬜ PASS / ⬜ FAIL |         |
| **ERR-01** | 404 Page                   | 1. Truy cập URL không tồn tại     | Hiển thị trang 404                  |                 | ⬜ PASS / ⬜ FAIL |         |
| **ERR-02** | API Error Handling         | 1. Server trả về lỗi              | Hiển thị thông báo lỗi thân thiện   |                 | ⬜ PASS / ⬜ FAIL |         |

---

## 2.2. Tổng hợp kết quả Manual Test

| Nhóm chức năng | Tổng TC | PASS | FAIL | Tỷ lệ |
| -------------- | ------- | ---- | ---- | ----- |
| Authentication | 5       |      |      |       |
| Product        | 3       |      |      |       |
| Cart           | 4       |      |      |       |
| Order          | 3       |      |      |       |
| JWT/Security   | 2       |      |      |       |
| Payment        | 3       |      |      |       |
| Admin CRUD     | 4       |      |      |       |
| Search/Filter  | 2       |      |      |       |
| Voucher        | 2       |      |      |       |
| Validation     | 5       |      |      |       |
| **TỔNG**       | **33**  |      |      |       |

---

# PHẦN 3 — KẾT LUẬN NỘP BÀI

## 3.1. Đoạn kết luận cho báo cáo

```markdown
## 🎯 Kết luận Testing & QA

### Kiểm thử tự động (Automated Testing)

Dự án đã triển khai **7 test files** covering các modules chính:

- AuthServiceImplTest - Kiểm thử đăng ký, đăng nhập
- ProductServiceTest & ProductControllerTest - Kiểm thử CRUD sản phẩm
- CartServiceImplTest - Kiểm thử chức năng giỏ hàng
- OrderServiceImplTest - Kiểm thử luồng đặt hàng
- VoucherServiceImplTest - Kiểm thử mã giảm giá

**Kết quả:** ✅ BUILD SUCCESS - Tất cả tests PASS

### Kiểm thử thủ công (Manual Testing)

Đã thực hiện kiểm thử thủ công với **33 test cases** covering:

- ✅ Core Features: Authentication, Product, Cart, Order
- ✅ Advanced Features: JWT, Payment, Upload, Admin CRUD, Search
- ✅ Validation & Error Handling

**Kết quả:** ✅ XX/33 test cases PASS (thay XX bằng số thực tế)

### Bằng chứng

1. Test Report: `target/surefire-reports/`
2. Manual Test Checklist: `docbaocao/TESTING_QA_GUIDE.md`
3. Screenshots: `docbaocao/screenshots/`

### Kết luận

Dự án **ĐÃ ĐÁP ỨNG** tiêu chí Testing & QA theo phiếu chấm điểm:

- ✅ Unit tests - Có
- ✅ Integration tests - Có (Spring Boot Test)
- ✅ Test cases + test data - 33 manual test cases
- ✅ Test report - Surefire report tự động
- ✅ Manual test checklist - Đầy đủ, chi tiết
```

---

## 3.2. Checklist trước khi nộp bài

- [ ] Chạy `./mvnw test` thành công
- [ ] Screenshot kết quả BUILD SUCCESS
- [ ] Export Surefire reports
- [ ] (Bonus) Chạy JaCoCo coverage
- [ ] Điền kết quả thực tế vào Manual Test Checklist
- [ ] Đánh dấu PASS/FAIL cho từng test case
- [ ] Tính tổng tỷ lệ PASS
- [ ] Copy kết luận vào báo cáo
- [ ] Lưu tất cả screenshots vào `docbaocao/screenshots/`

---

## 3.3. Cấu trúc thư mục bằng chứng đề xuất

```
docbaocao/
├── TESTING_QA_GUIDE.md          # File này
├── screenshots/
│   ├── test_terminal_success.png
│   ├── surefire_report.png
│   ├── coverage_report.png       # (nếu có JaCoCo)
│   └── manual_test_evidence/
│       ├── auth_login_success.png
│       ├── cart_add_product.png
│       └── order_checkout.png
└── test-reports/
    └── (copy từ target/surefire-reports/)
```

---

**Ngày tạo:** 27/01/2026  
**Phiên bản:** 1.0  
**Người tạo:** [Tên sinh viên]
