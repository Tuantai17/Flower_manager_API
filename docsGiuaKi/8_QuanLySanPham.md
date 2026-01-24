# CHƯƠNG 7: QUẢN LÝ SẢN PHẨM (PRODUCT MANAGEMENT)

Tài liệu này phân tích chi tiết luồng xử lý của tính năng **Quản lý Sản Phẩm**, tập trung sâu vào quy trình **Cập Nhật Sản Phẩm (Update)** và các logic liên quan.

---

## 1. Kiến Trúc Tổng Quan

Tính năng quản lý sản phẩm được phân chia theo mô hình 3 lớp chuẩn:

1.  **Controller Layer (`ProductAdminController`)**: Tiếp nhận yêu cầu HTTP từ FE/Postman.
2.  **Service Layer (`ProductServiceImpl`)**: Xử lý logic nghiệp vụ (kiểm tra rỗng, trùng lặp, logic giá...).
3.  **Repository Layer (`ProductRepository`)**: Giao tiếp trực tiếp với Database.

---

## 2. Phân Tích Luồng Cập Nhật (Update Product)

Quy trình cập nhật một sản phẩm diễn ra theo các bước sau:

### Bước A: Định Nghĩa Dữ Liệu (`ProductUpdateDTO`)

Trước khi cập nhật, Client gửi một JSON body chứa các thông tin cần thay đổi. Các trường trong DTO này đều là **Optional** (có thể null), nghĩa là gửi trường nào thì chỉ cập nhật trường đó.

- **File:** `src/main/java/com/flower/manager/dto/product/ProductUpdateDTO.java`

```java
@Data
public class ProductUpdateDTO {
    // Các validation cơ bản
    @Size(max = 200, message = "Tên tối đa 200 ký tự")
    private String name;

    @DecimalMin(value = "0.0", message = "Giá không âm")
    private BigDecimal price;

    // Status: 1=Active, 0=Inactive, 2=Out of Stock
    @Max(value = 2)
    private Integer status;

    private Long categoryId; // Thay đổi danh mục
}
```

### Bước B: Tiếp Nhận Yêu Cầu (`ProductAdminController`)

API chỉ dành cho **Admin** (`@PreAuthorize("hasRole('ADMIN')")`).

- **Endpoint:** `PUT /api/admin/products/{id}`
- **Luồng Code:**

```java
@RestController
@RequestMapping("/api/admin/products")
public class ProductAdminController {

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateDTO dto) {

        // Gọi xuống Service để xử lý
        ProductDTO updated = productService.update(id, dto);

        return ResponseEntity.ok(ApiResponse.success(updated));
    }
}
```

### Bước C: Xử Lý Logic Nghiệp Vụ (`ProductServiceImpl`)

Đây là nơi chứa logic phức tạp nhất. Hàm `update` thực hiện 4 nhiệm vụ chính:

1.  **Kiểm tra Tồn Tại**: Sản phẩm có ID này có trong DB không?
2.  **Kiểm tra Hợp Lệ (Validate)**: Slug có bị trùng với sản phẩm khác không?
3.  **Cập Nhật Thông Tin**: Map dữ liệu từ DTO vào Entity.
4.  **Xử Lý Quan Hệ (Category)**: Nếu đổi danh mục, phải tìm và set lại danh mục mới.

- **File:** `src/main/java/com/flower/manager/service/product/ProductServiceImpl.java`

```java
@Override
public ProductDTO update(Long id, ProductUpdateDTO dto) {
    // 1. Tìm sản phẩm cũ, nếu không thấy -> Lỗi 404
    Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

    // 2. Nếu đổi Slug, kiểm tra xem Slug mới có bị trùng với SP khác không?
    if (dto.getSlug() != null && !dto.getSlug().equals(product.getSlug())) {
        if (productRepository.existsBySlugAndIdNot(dto.getSlug(), id)) {
            throw new IllegalArgumentException("Slug đã tồn tại");
        }
    }

    // 3. Cập nhật các trường cơ bản (Tên, Giá, Mô tả...)
    // Mapper sẽ tự động bỏ qua các trường null trong DTO
    productMapper.updateEntity(product, dto);

    // 4. Xử lý logic thay đổi Danh Mục (Category)
    Long newCategoryId = dto.getCategoryId();
    if (newCategoryId != null) {
        // Tìm category mới trong DB
        Category newCategory = categoryRepository.findById(newCategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        // Gán sản phẩm vào danh mục mới
        product.setCategory(newCategory);
    }

    // 5. Lưu vào Database
    Product saved = productRepository.save(product);

    return productMapper.toDTO(saved);
}
```

---

## 3. Các Logic Liên Quan Khác

### 3.1. Tìm Kiếm Nâng Cao & Sắp Xếp

Hệ thống hỗ trợ tìm kiếm và sắp xếp đa tiêu chí trong hàm `advancedSearch`:

- **File:** `ProductServiceImpl.java`
- **Logic:** Sử dụng `switch-case` để xác định kiểu sắp xếp (`price_asc`, `price_desc`, `best_selling`...).
- **Đặc biệt:** Logic `best_selling` sẽ truy vấn các Product ID bán chạy nhất từ bảng Order, sau đó sắp xếp danh sách kết quả theo thứ tự bán chạy.

### 3.2. Kiểm Tra Slug (SEO Friendly URLs)

Slug là đường dẫn thân thiện (ví dụ: `hoa-hong-do` thay vì `product/123`).

- Khi tạo/sửa, hệ thống luôn kiểm tra `existsBySlug` để đảm bảo duy nhất.
- Khi sửa, dùng `existsBySlugAndIdNot` để bỏ qua chính sản phẩm đang sửa (tránh tự báo lỗi trùng với chính mình).

### 3.3. Xử Lý Hình Ảnh (Thumbnail)

- Trong `ProductUpdateDTO`, thông tin hình ảnh chỉ là một chuỗi String URL (`thumbnail`).
- Việc upload file ảnh vật lý được xử lý bởi một API Upload riêng (thường là `UploadController`), sau khi upload xong FE sẽ nhận được URL và gửi kèm vào payload update sản phẩm.

---

## 4. Tổng Kết

Quy trình cập nhật sản phẩm đảm bảo tính toàn vẹn dữ liệu thông qua:

1.  **Validation chặt chẽ** ở Controller (`@Valid`).
2.  **Kiểm tra logic** ở Service (check slug trùng, check category tồn tại).
3.  **Transactional**: Đảm bảo toàn bộ quá trình cập nhật thành công hoặc rollback nếu có lỗi.
