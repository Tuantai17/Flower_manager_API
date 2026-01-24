# BÁO CÁO GIỮA KỲ: HATEOAS (1 ĐIỂM)

Tài liệu này giải thích chi tiết về **HATEOAS** và cách nó được triển khai trong dự án **Flower Manager**.

---

## 1. HATEOAS Là Gì?

**HATEOAS** viết là tắt của **H**ypermedia **a**s **t**he **E**ngine **o**f **A**pplication **S**tate.

Đây là một ràng buộc (constraint) của kiến trúc REST application. Ý tưởng cốt lõi là:

- Khi Client gọi API lấy dữ liệu, Server không chỉ trả về dữ liệu thuần (JSON fields) mà còn trả kèm các **đường dẫn (Links)** liên quan.
- Client có thể dựa vào các Link này để biết "làm gì tiếp theo" hoặc "truy cập tài nguyên liên quan ở đâu" mà không cần biết cứng URL từ trước.

**Ví dụ:** Khi lấy chi tiết một Sản phẩm, Server trả về Link để:

- Xem chính nó (`self`)
- Xem danh mục của nó (`category`)
- Mua hàng, review, v.v.

---

## 2. Triển Khai Trong Dự Án (Code Minh Họa)

Để đạt điểm phần này, bạn cần chỉ ra 3 thành phần chính trong code:

### Bước 1: Thêm Thư Viện (Dependency)

Project sử dụng `spring-boot-starter-hateoas` để hỗ trợ việc tạo Link dễ dàng.

- **File tham chiếu:** `pom.xml`

```xml
<!-- HATEOAS: Hypermedia as the Engine of Application State -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

### Bước 2: DTO Kế Thừa RepresentationModel

Class DTO trả về cho Client cần có khả năng chứa danh sách các Link. Spring HATEOAS cung cấp class `RepresentationModel` cho việc này.

- **File tham chiếu:** `src/main/java/com/flower/manager/dto/product/ProductDTO.java`

```java
import org.springframework.hateoas.RepresentationModel;

// Kế thừa RepresentationModel để có thể chứa Link (links: [])
public class ProductDTO extends RepresentationModel<ProductDTO> {
    private Long id;
    private String name;
    // ... các field khác
}
```

### Bước 3: Gắn Link Vào Response (Controller)

Đây là phần quan trọng nhất. Trong Controller, trước khi trả dữ liệu về, ta dùng `WebMvcLinkBuilder` để tạo Link động dựa trên các method của Controller.

- **File tham chiếu:** `src/main/java/com/flower/manager/controller/product/ProductController.java`

**Logic trong code:**

1.  Sử dụng `linkTo(methodOn(...))` để trỏ đến hàm xử lý (Controller method).
2.  Sử dụng `.withRel("name")` để đặt tên cho mối quan hệ (relationship).

```java
// Trích đoạn method addLinks trong ProductController

private ProductDTO addLinks(ProductDTO product) {
    // 1. Tạo Link "self": Trỏ đến chính API lấy chi tiết sản phẩm này
    // Dạng JSON trả về: "_links": { "self": { "href": ".../api/products/1" } }
    product.add(linkTo(methodOn(ProductController.class).getById(product.getId())).withSelfRel());

    // 2. Tạo Link "by-slug": Trỏ đến API lấy theo slug
    product.add(linkTo(methodOn(ProductController.class).getBySlug(product.getSlug())).withRel("by-slug"));

    // 3. Tạo Link "category": Trỏ đến API lấy danh sách sản phẩm cùng danh mục
    // Giúp Client dễ dàng "click" vào danh mục để xem tiếp
    if (product.getCategoryId() != null) {
        product.add(linkTo(methodOn(ProductController.class).getByCategory(product.getCategoryId()))
                .withRel("category"));
    }
    return product;
}

// Áp dụng khi trả về danh sách
@GetMapping
public ResponseEntity<ApiResponse<List<ProductDTO>>> getAll() {
    List<ProductDTO> products = productService.getAllActive().stream()
            .map(this::addLinks) // Gọi hàm addLinks cho từng sản phẩm
            .collect(Collectors.toList());
    return ResponseEntity.ok(ApiResponse.success(products));
}
```

---

## 3. Kết Quả Trả Về (JSON Output)

Khi gọi API, JSON sẽ trông như thế này (lưu ý phần `_links`):

```json
{
  "id": 1,
  "name": "Hoa Hồng Đỏ",
  "price": 100000,
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/products/1"
    },
    "by-slug": {
      "href": "http://localhost:8080/api/products/slug/hoa-hong-do"
    },
    "category": {
      "href": "http://localhost:8080/api/products/category/5"
    }
  }
}
```

**Kết luận báo cáo:** Dự án đã áp dụng thành công chuẩn HATEOAS mức cơ bản, giúp API trở nên "tự mô tả" (self-descriptive) và Client dễ dàng điều hướng tài nguyên.
