# BÁO CÁO GIỮA KỲ: TRUY VẤN CƠ BẢN & PHỨC TẠP (2 ĐIỂM)

Tài liệu này phân tích các loại truy vấn Database được sử dụng trong dự án, từ cơ bản đến nâng cao.

---

## 1. Truy Vấn Cơ Bản (Basic Queries)

Dự án sử dụng **Spring Data JPA** để tự động sinh ra các câu lệnh SQL cơ bản mà không cần viết code thủ công.

### 📌 Khái niệm & Áp dụng

Chỉ cần khai báo Interface kế thừa `JpaRepository`, ta có ngay các hàm CRUD chuẩn.

- **File tham chiếu:** `src/main/java/com/flower/manager/repository/ProductRepository.java`

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // 1. Tìm theo ID: findById(1L)
    // -> SELECT * FROM products WHERE id = 1

    // 2. Lưu/Cập nhật: save(product)
    // -> INSERT INTO products... hoặc UPDATE products...

    // 3. Xóa: deleteById(1L)
    // -> DELETE FROM products WHERE id = 1

    // 4. Derived Query (Truy vấn dựa trên tên hàm)
    // -> SELECT * FROM products WHERE category_id = ? ORDER BY created_at DESC
    List<Product> findByCategoryIdOrderByCreatedAtDesc(Long categoryId);
}
```

---

## 2. Truy Vấn Phức Tạp (Complex Queries)

Với các yêu cầu nghiệp vụ khó hơn, dự án sử dụng **JPQL (Java Persistence Query Language)** kết hợp với annotation `@Query`.

### a. Tối ưu hiệu năng với JOIN FETCH (Tránh lỗi N+1)

Khi hiển thị danh sách sản phẩm, nếu không dùng `JOIN FETCH`, Hibernate sẽ chạy 1 câu truy vấn lấy danh sách Product, sau đó chạy thêm N câu truy vấn để lấy Category của từng Product (Vấn đề N+1).

**Giải pháp trong code:**

```java
// Trích đoạn ProductRepository.java

@Query("SELECT p FROM Product p " +
       "LEFT JOIN FETCH p.category " + // Lấy luôn dữ liệu Category trong 1 lần query
       "WHERE p.active = true " +
       "ORDER BY p.createdAt DESC")
List<Product> findAllActiveWithCategory();
```

### b. Tìm kiếm nâng cao (Advanced Search & Filtering)

Sử dụng JPQL động để lọc dữ liệu theo nhiều tiêu chí cùng lúc (Tên, Giá từ-đến, Danh mục). Nếu tham số nào `NULL` thì sẽ bỏ qua điều kiện đó.

**Giải pháp trong code:**

```java
// Trích đoạn ProductRepository.java

@Query("SELECT p FROM Product p " +
       "LEFT JOIN FETCH p.category c " +
       "WHERE p.active = true " +
       "AND (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " + // Tìm gần đúng
       "AND (:priceFrom IS NULL OR COALESCE(p.salePrice, p.price) >= :priceFrom) " + // Logic giá
       "AND (:priceTo IS NULL OR COALESCE(p.salePrice, p.price) <= :priceTo) " +
       "AND (:categoryId IS NULL OR c.id = :categoryId OR c.parent.id = :categoryId) " +
       "ORDER BY COALESCE(p.salePrice, p.price) ASC")
List<Product> advancedSearchOrderByPriceAsc(
                @Param("keyword") String keyword,
                @Param("priceFrom") java.math.BigDecimal priceFrom,
                // ... params
);
```

### c. Thống kê & Tổng hợp (Aggregation)

Sử dụng các hàm `SUM`, `COUNT`, `GROUP BY` để làm báo cáo hoặc lấy sản phẩm bán chạy.

**Giải pháp trong code (Tìm sản phẩm bán chạy nhất):**

```java
// Trích đoạn ProductRepository.java

@Query("SELECT oi.product.id FROM OrderItem oi " +
       "JOIN oi.order o " +
       "WHERE o.status IN (com.flower.manager.enums.OrderStatus.COMPLETED, com.flower.manager.enums.OrderStatus.DELIVERED) " +
       "GROUP BY oi.product.id " + // Gom nhóm theo Product ID
       "ORDER BY SUM(oi.quantity) DESC") // Sắp xếp theo tổng số lượng bán giảm dần
List<Long> findBestSellingProductIds();
```

---

## 3. Tổng Kết

Dự án đã đáp ứng trọn vẹn yêu cầu về truy vấn:

1.  **Cơ bản:** Tận dụng tối đa Spring Data JPA.
2.  **Phức tạp:** Xử lý tốt các bài toán khó như:
    - **Performance:** Dùng `JOIN FETCH`.
    - **Logic:** Dùng `COALESCE`, `CASE WHEN`.
    - **Statistics:** Dùng `GROUP BY`, `SUM`.
