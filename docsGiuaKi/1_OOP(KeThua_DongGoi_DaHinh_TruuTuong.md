# BÁO CÁO GIỮA KỲ: GIẢI THÍCH LẬP TRÌNH HƯỚNG ĐỐI TƯỢNG (OOP)

Tài liệu này giải thích chi tiết 4 tính chất của Lập trình hướng đối tượng (OOP) và cách chúng được áp dụng vào dự án **Flower Manager**.

---

## 1. Đóng Gói (Encapsulation)

### 📌 Khái niệm

Đóng gói là kỹ thuật che giấu thông tin quan trọng và ngăn chặn truy cập trực tiếp từ bên ngoài vào các thuộc tính nội bộ của đối tượng. Dữ liệu chỉ được truy cập thông qua các phương thức công khai (getter/setter). Mọi logic kiểm tra dữ liệu có thể được đặt trong các phương thức này.

### 💻 Áp dụng trong dự án

Trong project này, tính đóng gói được thể hiện rõ nhất ở các **Entity** và **DTO**.

- **File tham chiếu:** `src/main/java/com/flower/manager/entity/Product.java`

**Giải thích code:**

- Các thuộc tính như `id`, `name`, `price` đều được khai báo là `private`. Điều này ngăn chặn việc gán giá trị tùy tiện từ bên ngoài.
- Sử dụng Lombok (`@Getter`, `@Setter`) để tự động sinh ra các phương thức truy cập an toàn.
- Các logic nghiệp vụ được đóng gói trong các phương thức nội tại của class, ví dụ method `isOnSale()` tính toán dựa trên `price` và `salePrice`.

```java
// Trích đoạn từ Product.java
@Getter
@Setter
public class Product {
    @Id
    private Long id; // private: không thể truy cập trực tiếp Product.id

    @Column(nullable = false)
    private BigDecimal price; // Dữ liệu được bảo vệ

    // Phương thức công khai cung cấp thông tin đã qua xử lý
    public boolean isOnSale() {
        return salePrice != null && salePrice.compareTo(BigDecimal.ZERO) > 0;
    }
}
```

---

## 2. Kế Thừa (Inheritance)

### 📌 Khái niệm

Kế thừa cho phép một class mới (class con) kế thừa các thuộc tính và phương thức của một class đã tồn tại (class cha). Điều này giúp tái sử dụng mã nguồn (code reusability) và tạo nên cấu trúc phân cấp.

### 💻 Áp dụng trong dự án

Tính kế thừa được sử dụng mạnh mẽ trong tầng **Repository** và các cấu hình Security.

- **File tham chiếu:** `src/main/java/com/flower/manager/repository/ProductRepository.java`

**Giải thích code:**

- Interface `ProductRepository` kế thừa từ `JpaRepository`.
- Nhờ kế thừa, `ProductRepository` sở hữu ngay lập tức hàng chục phương thức có sẵn như `save()`, `findById()`, `findAll()`, `delete()` mà không cần viết lại dù chỉ 1 dòng code.

```java
// Trích đoạn từ ProductRepository.java
// JpaRepository là class cha (thực ra là interface), ProductRepository là con
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Kế thừa toàn bộ sức mạnh CRUD từ JpaRepository
    // Chỉ cần viết thêm các query custom
    List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
}
```

Ngoài ra, **Kế thừa** còn được sử dụng ở tầng DTO để hỗ trợ **HATEOAS**:

- **File tham chiếu:** `src/main/java/com/flower/manager/dto/product/ProductDTO.java`

**Giải thích code:**

- `ProductDTO` kế thừa từ `RepresentationModel<ProductDTO>`.
- Class cha `RepresentationModel` cung cấp sẵn danh sách `List<Link>` và các phương thức như `add()`, `getLinks()`.
- Nhờ kế thừa, `ProductDTO` có thể chứa các đường dẫn API (hypermedia) mà không cần tự khai báo lại, phục vụ cho yêu cầu HATEOAS của đề tài.

```java
// Trích đoạn từ ProductDTO.java
public class ProductDTO extends RepresentationModel<ProductDTO> {
    // ProductDTO kế thừa khả năng chứa Link từ RepresentationModel
    // Không cần khai báo field 'links' nhưng vẫn có thể dùng dtp.add(link)
    private Long id;
    private String name;
    // ...
}
```

---

## 3. Đa Hình (Polymorphism)

### 📌 Khái niệm

Đa hình cho phép một hành động có thể được thực hiện theo nhiều cách khác nhau.

- **Đa hình lúc biên dịch (Overloading):** Cùng tên hàm nhưng khác tham số.
- **Đa hình lúc chạy (Overriding):** Class con định nghĩa lại method của class cha/interface để thực hiện hành vi riêng.

### 💻 Áp dụng trong dự án

Tính đa hình thể hiện rõ nhất ở tầng **Service** (sử dụng Interface) và **Controller**.

- **File tham chiếu:**
  - Interface: `src/main/java/com/flower/manager/service/product/ProductService.java`
  - Implementation: `src/main/java/com/flower/manager/service/product/ProductServiceImpl.java` (Giả định file impl)

**Giải thích code:**

- **Upcasting:** Controller chỉ cần gọi `ProductService` (interface/kiểu cha) mà không cần quan tâm đến `ProductServiceImpl` (class con cụ thể). Nếu sau này bạn đổi logic trong `ProductServiceImpl` hoặc tạo `ProductServiceNewImpl`, code ở Controller không cần sửa đổi nhiều.
- **List Interface:** Khai báo kiểu dữ liệu là `List<ProductDTO>` nhưng thực tế đối tượng trả về là `ArrayList` (hoặc `LinkedList`).

```java
// Trong ProductController.java
private final ProductService productService; // Đa hình: Lập trình với Interface

// Khi chạy, Spring sẽ inject instance của ProductServiceImpl vào đây.
// Hàm getById() sẽ chạy code thực thi trong class con (ProductServiceImpl).
ProductDTO product = productService.getById(id);
```

Ngoài ra, **Đa hình** còn được thể hiện qua việc cài đặt interface của framework (**Framework Interface Implementation**):

- **File tham chiếu:** `src/main/java/com/flower/manager/entity/User.java`

**Giải thích code:**

- Class `User` implements interface `UserDetails` của Spring Security.
- Đây là **đa hình**: Đối với Spring Security, nó không quan tâm `User` là ai, nó chỉ cần một đối tượng kiểu `UserDetails`.
- Class `User` định nghĩa lại (override) các method như `getAuthorities()`, `getPassword()`, `isEnabled()`... để cung cấp logic xác thực riêng của ứng dụng nhưng vẫn tuân thủ "hợp đồng" của Spring Security.

```java
// Trích đoạn từ User.java
public class User implements UserDetails {
    // User "là một" UserDetails (IS-A relationship)
    // Spring Security sẽ giao tiếp với User thông qua interface UserDetails

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       // Override logic phân quyền riêng của Project
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}
```

---

## 4. Trừu Tượng (Abstraction)

### 📌 Khái niệm

Trừu tượng là kỹ thuật chỉ trình bày những tính năng thiết yếu của đối tượng và ẩn đi các chi tiết cài đặt phức tạp bên dưới. Người dùng chỉ cần biết "nó làm gì" chứ không cần biết "nó làm như thế nào".

### 💻 Áp dụng trong dự án

Tầng **Service Interface** và **Spring Data JPA** là ví dụ điển hình.

- **File tham chiếu:** `src/main/java/com/flower/manager/service/product/ProductService.java`

**Giải thích code:**

- Interface `ProductService` định nghĩa một bản hợp đồng (contract): "Tôi cung cấp chức năng `create`, `search`, `getById`".
- Controller chỉ nhìn thấy các hàm này (tính trừu tượng) mà không biết bên dưới nó phải gọi DB, map dữ liệu, hay check logic phức tạp như thế nào (chi tiết cài đặt được ẩn trong Impl).
- Ví dụ khác: Chúng ta dùng `save()` của Repository để lưu vào DB mà không cần biết nó phải mở kết nối JDBC, tạo câu lệnh `INSERT INTO...` ra sao. Đó là sự trừu tượng hóa của ORM (Hibernate).

```java
// Trích đoạn Interface ProductService.java
public interface ProductService {
    // Abstract method: Chỉ khai báo tên hành động, tham số, kiểu trả về.
    // Ẩn đi hoàn toàn logic xử lý.
    ProductDTO create(ProductCreateDTO dto);

    List<ProductDTO> searchByName(String keyword);
}
```

---

_Tài liệu này được tạo tự động để hỗ trợ báo cáo đồ án giữa kỳ._
