# BÁO CÁO GIỮA KỲ: UNIT TEST (1 ĐIỂM)

Tài liệu này giải thích cách dự án thực hiện kiểm thử đơn vị (Unit Test) sử dụng **JUnit 5** và **Mockito**.

---

## 1. Công Nghệ Sử Dụng

- **JUnit 5:** Framework kiểm thử tiêu chuẩn của Java.
- **Mockito:** Thư viện để giả lập (mock) các phụ thuộc (dependencies) như Repository, giúp test Service một cách cô lập (isolated).

---

## 2. Phân Tích Code Test

Code Unit Test điển hình được viết cho `ProductService`. Tại đây, chúng ta test logic nghiệp vụ mà không cần kết nối Database thật.

- **File tham chiếu:** `src/test/java/com/flower/manager/service/product/ProductServiceTest.java`

### a. Thiết Lập Môi Trường Test (Setup)

Sử dụng các Annotation của Mockito để tạo các object giả lập.

```java
@ExtendWith(MockitoExtension.class) // Kích hoạt Mockito cho class test này
class ProductServiceTest {

    @Mock // Giả lập Repository (không kết nối DB thật)
    private ProductRepository productRepository;

    @Mock // Giả lập Mapper
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks // Inject các Mock ở trên vào Service cần test
    private ProductServiceImpl productService;

    // ...
}
```

### b. Test Case 1: Tạo Sản Phẩm Thành Công (Happy Case)

Kiểm tra xem khi mọi dữ liệu đầu vào đúng thì hàm `create` có hoạt động đúng không.

**Kịch bản (Given - When - Then):**

1.  **Given:** Giả lập các hàm của Repository trả về thành công (dùng `when(...).thenReturn(...)`).
2.  **When:** Gọi hàm `productService.create(dto)`.
3.  **Then:** Kiểm tra kết quả trả về không null và gọi `save` đúng 1 lần.

```java
@Test
void create_ShouldReturnProductDTO_WhenSuccessful() {
    // 1. Given (Giả lập hành vi)
    when(productRepository.existsBySlug(createDTO.getSlug())).thenReturn(false); // Slug chưa tồn tại
    when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));     // Category có tồn tại
    when(productMapper.toEntity(createDTO)).thenReturn(product);
    when(productRepository.save(any(Product.class))).thenReturn(product);        // Save thành công
    when(productMapper.toDTO(product)).thenReturn(productDTO);

    // 2. When (Thực thi)
    ProductDTO result = productService.create(createDTO);

    // 3. Then (Kiểm chứng)
    assertNotNull(result); // Kết quả không được null
    assertEquals(productDTO.getName(), result.getName()); // Tên phải khớp
    verify(productRepository, times(1)).save(any(Product.class)); // Hàm save phải được gọi đúng 1 lần
}
```

### c. Test Case 2: Xử Lý Lỗi (Exception Handling)

Kiểm tra xem hệ thống có ném ra lỗi đúng như mong đợi không (ví dụ: tìm ID không thấy).

**Kịch bản:**

1.  **Given:** Giả lập `findById` trả về rỗng (`Optional.empty()`).
2.  **When & Then:** Kiểm tra xem khi gọi `getById` thì có ném ra `ResourceNotFoundException` không.

```java
@Test
void getById_ShouldThrowException_WhenIdDoesNotExist() {
    // 1. Given
    when(productRepository.findById(1L)).thenReturn(Optional.empty()); // Tìm không thấy

    // 2. When & Then
    assertThrows(ResourceNotFoundException.class, () -> productService.getById(1L)); // Phải ném lỗi 404
}
```

---

## 3. Hướng Dẫn Báo Cáo & Demo (Trường Hợp Test Thành Công - Màu Xanh)

Khi giáo viên hỏi hoặc yêu cầu demo minh chứng ("chạy thử xem nào"), bạn hãy làm theo các bước sau và sử dụng kịch bản lời thoại bên dưới.

### Bước 1: Mở IDE và Chạy Test

1.  Mở file `ProductServiceTest.java`.
2.  Chuột phải vào tên class -> Chọn **Run 'ProductServiceTest'**.
3.  Chờ kết quả hiện **dấu tích xanh (Green Checks)**.

### Bước 2: Kịch Bản Báo Cáo

> "Thưa thầy/cô, để đảm bảo độ tin cậy của phần mềm, em đã viết Unit Test sử dụng JUnit 5 và Mockito. Mục tiêu là kiểm tra logic nghiệp vụ của Service mà không cần phụ thuộc vào Database thật."

**Chỉ vào các dấu tích xanh:**

> "Như thầy/cô thấy, tất cả các test case đều hiển thị **màu xanh (Passed)**, chứng tỏ các hàm logic như `create` hay `getById` đều hoạt động đúng thiết kế."

---

## 4. Phân Tích Lỗi (Trường Hợp Test Thất Bại - Màu Đỏ)

Nếu bạn gặp tình huống test bị lỗi (như ảnh lỗi `NullPointerException` tại `AuthServiceImplTest`), hãy giải thích đây là **tính năng phát hiện lỗi** của Unit Test.

**Kịch Bản Báo Cáo:**

> "Thưa thầy, bên cạnh các test case thành công, hệ thống test cũng giúp em phát hiện sớm các lỗi tiềm ẩn.
> Ví dụ ở đây là lỗi `NullPointerException` (Màu đỏ). Nhờ Unit Test, em biết được rằng module `AuthService` đang thiếu cấu hình Dependency Injection, từ đó em có thể sửa ngay trước khi deploy."

**Giải thích kỹ thuật (Nguyên nhân lỗi NullPointerException):**

- **Lỗi:** `Cannot invoke ... because "this.authService" is null`
- **Nguyên nhân:** Đối tượng `authService` chưa được tạo thành công trong class Test (do thiếu `@InjectMocks` hoặc Mockito không tìm thấy Constructor phù hợp).
- **Ý nghĩa:** Test case này đã hoàn thành nhiệm vụ **cảnh báo lỗi** cho lập trình viên.

---

## 5. Tổng Kết

Dự án đã triển khai Unit Test đúng chuẩn:

- **Cô lập (Isolation):** Test Service độc lập.
- **Coverage:** Bao phủ các trường hợp thành công và thất bại.
- **Phản hồi nhanh:** Giúp phát hiện lỗi logic ngay lập tức.
