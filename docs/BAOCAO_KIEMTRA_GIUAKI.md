# BÁO CÁO KIỂM TRA GIỮA KỲ - PHÁT TRIỂN ỨNG DỤNG WEB

**Đề tài**: Hệ thống quản lý và kinh doanh cửa hàng hoa (Flower Shop Manager)
**Sinh viên thực hiện**: [Tên của bạn]

---

## A. Mô tả nghiệp vụ

Hệ thống quản lý cửa hàng hoa là một giải pháp thương mại điện tử trọn vẹn, bao gồm:

- **Frontend (Khách hàng)**: Cho phép người dùng xem danh sách hoa, tìm kiếm nâng cao (theo giá, danh mục), thêm vào giỏ hàng, đặt hàng và thanh toán.
- **Backend (Quản trị)**: Cung cấp API cho trang web, tích hợp quản lý sản phẩm, đơn hàng, khách hàng, kho hàng và báo cáo doanh thu.
- **Công nghệ**: Spring Boot 3 (Backend), MySQL (Database), Next.js (Frontend).

---

## B. Thiết kế Database (MySQL)

Hệ thống sử dụng cơ sở dữ liệu MySQL với hơn 5 bảng chính, quan hệ chặt chẽ:

1.  **users**: Lưu thông tin người dùng (id, username, password, role, email...).
2.  **products**: Lưu thông tin hoa (id, name, price, stock_quantity, category_id...).
3.  **categories**: Danh mục hoa (id, name, parent_id...).
4.  **orders**: Đơn hàng (id, user_id, total_price, status...).
5.  **order_items**: Chi tiết đơn hàng (id, order_id, product_id, quantity, price...).
6.  **carts / cart_items**: Giỏ hàng tạm thời.

**Mối quan hệ chính:**

- `users` 1 - N `orders`
- `orders` 1 - N `order_items`
- `products` 1 - N `order_items`
- `categories` 1 - N `products`

---

## C. Kiến trúc Dự án (Spring Boot Standard)

Dự án tuân thủ kiến trúc Layered Architecture (Kiến trúc phân lớp) chuẩn của Spring Boot:

- `controller`: Tiếp nhận request (API).
- `service`: Xử lý nghiệp vụ logic.
- `repository`: Tương tác với Database (JPA).
- `entity`: Mapping bảng Database.
- `dto`: Truyền tải dữ liệu giữa các lớp.
- `exception`: Xử lý lỗi tập trung.
- `security`: Cấu hình bảo mật JWT.

---

## D. Lập trình Hướng đối tượng (OOP) (2 điểm)

Dự án thể hiện đầy đủ 4 tính chất OOP:

### 1. Đóng gói (Encapsulation)

Thể hiện qua việc sử dụng `private` cho các thuộc tính trong Entity và DTO, truy xuất thông qua Getter/Setter (sử dụng Lombok).
**Ví dụ (`User.java`):**

```java
@Entity
public class User {
    @Id
    private Long id; // private field

    @Column(nullable = false)
    private String password;

    // Getter/Setter được sinh tự động bởi Lombok @Data/@Getter/@Setter
}
```

### 2. Kế thừa (Inheritance)

Thể hiện qua việc Entity `User` kế thừa (implements) interface `UserDetails` của Spring Security để tận dụng các phương thức xác thực có sẵn.
**Ví dụ:**

```java
public class User implements UserDetails { // Kế thừa interface
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { ... }
}
```

### 3. Trừu tượng (Abstraction)

Sử dụng Interface cho tầng Service để che giấu chi tiết cài đặt. Controller chỉ làm việc với Interface.
**Ví dụ (`ProductService.java`):**

```java
public interface ProductService {
    ProductDTO getById(Long id); // Method trừu tượng
    List<ProductDTO> searchByName(String keyword);
}
```

### 4. Đa hình (Polymorphism)

Thể hiện qua việc `ProductServiceImpl` thực thi (`implements`) `ProductService`. Dễ dàng thay thế implement khác mà không sửa code Controller.
**Ví dụ:**

```java
@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public ProductDTO getById(Long id) { ... } // Override lại hành vi
}
```

---

## E. REST API + HATEOAS (1 điểm)

API được thiết kế chuẩn RESTful và áp dụng HATEOAS để điều hướng client.

**Ví dụ tại `ProductController.java`:**
Khi lấy chi tiết sản phẩm, API trả về thêm các liên kết (`_links`) như `self`, `category` để client biết các thao tác tiếp theo.

```java
// ProductController.java
private ProductDTO addLinks(ProductDTO product) {
    // Link "self" trỏ về chính nó
    product.add(linkTo(methodOn(ProductController.class).getById(product.getId())).withSelfRel());

    // Link "category" trỏ về danh mục
    if (product.getCategoryId() != null) {
        product.add(linkTo(methodOn(ProductController.class).getByCategory(product.getCategoryId()))
                .withRel("category"));
    }
    return product;
}
```

**Output JSON mẫu:**

```json
{
  "id": 1,
  "name": "Red Rose",
  "_links": {
    "self": { "href": "http://localhost:8080/api/products/1" },
    "category": { "href": "http://localhost:8080/api/products/category/1" }
  }
}
```

---

## F. Truy vấn (Query) (2 điểm)

Sử dụng JPA Repository kết hợp cả Basic và Complex Queries trong `ProductRepository.java`.

### 1. Truy vấn cơ bản

Sử dụng Derived Query Methods (Spring Data JPA tự tạo query):

```java
// Tìm theo slug
Optional<Product> findBySlug(String slug);

// Tìm theo trạng thái và sắp xếp
List<Product> findByStatusOrderByCreatedAtDesc(Integer status);
```

### 2. Truy vấn phức tạp (JPQL & Native)

Sử dụng `@Query` để join nhiều bảng, filter động và aggregate dữ liệu.

**Ví dụ 1: Tìm kiếm nâng cao (JOIN Category, điều kiện động)**

```java
@Query("SELECT p FROM Product p " +
       "LEFT JOIN FETCH p.category c " +
       "WHERE p.active = true " +
       "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
       "AND (:priceFrom IS NULL OR p.price >= :priceFrom) " +
       "ORDER BY p.price ASC")
List<Product> advancedSearchOrderByPriceAsc(...);
```

**Ví dụ 2: Thống kê bán chạy (GROUP BY, JOIN, SUM)**

```java
@Query("SELECT oi.product.id FROM OrderItem oi " +
       "JOIN oi.order o " +
       "WHERE o.status = 'COMPLETED' " +
       "GROUP BY oi.product.id " +
       "ORDER BY SUM(oi.quantity) DESC")
List<Long> findBestSellingProductIds();
```

---

## G. Giao diện (UI) (1 điểm)

Dự án có giao diện Frontend hoàn chỉnh viết bằng **Next.js** (trong thư mục `flower-shop-frontend`), giao tiếp với Backend qua REST API.

- Trang chủ: Hiển thị Banner, danh sách sản phẩm nổi bật.
- Trang chi tiết: Hiển thị hình ảnh, giá, mô tả, nút thêm vào giỏ.
- Trang quản trị (Admin): Quản lý sản phẩm, đơn hàng (được bảo vệ).

---

## H. Unit Test (1 điểm)

Sử dụng thư viện **Mockito** và **JUnit 5** để test tầng Service độc lập với Database.

**Ví dụ `ProductServiceTest.java`:**

```java
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository; // Giả lập Repository

    @InjectMocks
    private ProductServiceImpl productService; // Inject Mock vào Service

    @Test
    void create_ShouldReturnProductDTO_WhenSuccessful() {
        // Given: Giả lập hành vi của Repository
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // When: Gọi hàm service
        ProductDTO result = productService.create(createDTO);

        // Then: Kiểm tra kết quả
        assertNotNull(result);
        verify(productRepository, times(1)).save(any(Product.class)); // Verfiy đã gọi save đúng 1 lần
    }
}
```

---

## I. Security (1 điểm)

Sử dụng **Spring Security** kết hợp **JWT (JSON Web Token)** để xác thực và phân quyền (Stateless).

**Luồng hoạt động (`SecurityConfig.java`):**

1.  User đăng nhập -> Server trả về Token (JWT).
2.  Client gửi request kèm Header `Authorization: Bearer <token>`.
3.  `JwtAuthenticationFilter` chặn request, validate token, lấy thông tin User và set vào Context.

**Cấu hình phân quyền:**

```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/public/**").permitAll() // Ai cũng vào được
    .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ Admin
    .anyRequest().authenticated() // Còn lại phải đăng nhập
);
```
