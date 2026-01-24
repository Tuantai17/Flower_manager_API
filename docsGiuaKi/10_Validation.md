# CHƯƠNG 9: CƠ CHẾ KIỂM TRA DỮ LIỆU (VALIDATION)

Hệ thống sử dụng cơ chế **Validation 3 lớp** để đảm bảo tính toàn vẹn và an toàn của dữ liệu đầu vào.

---

## 1. Lớp 1: DTO Validation (Hibernate Validator)

Đây là lớp phòng thủ đầu tiên. Các quy tắc được định nghĩa trực tiếp trên các Class DTO (Data Transfer Object) bằng các Annotation.

**Ví dụ:** `RegisterRequest.java`

```java
public class RegisterRequest {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải từ 3-50 ký tự")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên đăng nhập chỉ chứa chữ, số, gạch dưới")
    private String username;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
             message = "Password phải có chữ hoa, chữ thường và số")
    private String password;
}
```

- **@NotBlank**: Không được null và không được là chuỗi rỗng.
- **@Size**: Độ dài chuỗi quy định.
- **@Pattern**: Kiểm tra theo biểu thức chính quy (Regex) phức tạp (như mật khẩu mạnh, định dạng SĐT).

---

## 2. Lớp 2: Controller Validation (`@Valid`)

Tại Controller, ta kích hoạt việc kiểm tra các rule ở lớp 1 bằng annotation `@Valid`.

**Ví dụ:** `AuthController.java`

```java
@PostMapping("/register")
public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    // Nếu dữ liệu vi phạm Annotation ở DTO, code sẽ KHÔNG chạy vào đây
    // Spring Boot sẽ tự động ném ra ngoại lệ MethodArgumentNotValidException
    return authService.register(request);
}
```

---

## 3. Lớp 3: Xử Lý Lỗi Tập Trung (`GlobalExceptionHandler`)

Khi lớp 2 phát hiện lỗi, nó ném ra `MethodArgumentNotValidException`. Thay vì trả về lỗi mặc định xấu xí của Tomcat, ta dùng Exception Handler để bắt và "làm đẹp" thông báo lỗi trả về cho Frontend.

**Ví dụ:** `GlobalExceptionHandler.java`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        // Lấy danh sách tất cả các lỗi từ DTO
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Trả về JSON chuẩn với mã lỗi 400 Bad Request
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.builder()
                .success(false)
                .message("Dữ liệu không hợp lệ")
                .data(errors) // {"username": "quá ngắn", "email": "sai định dạng"}
                .build());
    }
}
```

---

## 4. Lớp 4: Service Validation (Business Logic)

Sau khi qua được 3 lớp trên (dữ liệu đúng định dạng), hệ thống tiếp tục kiểm tra **Logic Nghiệp Vụ** tại Service.

**Ví dụ:** `AuthServiceImpl.java`

```java
public AuthResponse register(RegisterRequest request) {
    // 1. Kiểm tra Confirm Password khớp với Password
    if (!request.getPassword().equals(request.getConfirmPassword())) {
        throw new BusinessException("Mật khẩu xác nhận không khớp");
    }

    // 2. Kiểm tra Username đã tồn tại trong DB chưa
    if (userRepository.existsByUsername(request.getUsername())) {
        throw new ResourceAlreadyExistsException("Tên đăng nhập đã được sử dụng");
    }

    // ...
}
```

---

## Tổng Kết

Nhờ việc kết hợp `@Valid` và `GlobalExceptionHandler`, hệ thống đảm bảo:

1.  **Chặt chẽ**: Không có dữ liệu rác vào được DB.
2.  **User Friendly**: Frontend nhận được thông báo lỗi cụ thể (bằng tiếng Việt) để hiển thị ngay dưới ô input.
3.  **Clean Code**: Controller không cần viết if-else kiểm tra từng trường.
