# CHƯƠNG 6: BẢO MẬT & PHÂN QUYỀN (SPRING SECURITY + JWT)

## 1. Tổng Quan Cơ Chế Bảo Mật

Hệ thống sử dụng **Spring Security** kết hợp với **JWT (JSON Web Token)** để bảo mật. Cơ chế hoạt động theo mô hình **Stateless** (không lưu session server-side), phù hợp với kiến trúc RESTful API.

**Luồng xác thực (Authentication Flow):**

1. Client gửi Username/Password.
2. Server xác thực, nếu đúng -> Trả về JWT Token.
3. Client gửi Request kèm Token (Header `Authorization: Bearer <token>`).
4. `JwtAuthenticationFilter` chặn request, kiểm tra Token.
5. Nếu Token hợp lệ -> Set User vào `SecurityContext` -> Cho phép truy cập Controller.

---

## 2. Phân Tích Chi Tiết Code

### 2.1. Cấu Hình Bảo Mật Chính (`SecurityConfig.java`)

Đây là bộ não của hệ thống bảo mật, định nghĩa ai được vào đâu.

- **File:** `src/main/java/com/flower/manager/config/SecurityConfig.java`

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Tắt CSRF do dùng API/JWT
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Không dùng Session cookie
            .authorizeHttpRequests(auth -> auth
                // 1. Cho phép Public Access (Không cần đăng nhập)
                .requestMatchers("/api/auth/**").permitAll() // Đăng ký/Đăng nhập
                .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll() // Xem sản phẩm
                .requestMatchers(HttpMethod.GET, "/api/news/**").permitAll() // Xem tin tức

                // 2. Phân quyền theo Role (RBAC)
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Chỉ Admin
                .requestMatchers("/api/staff/**").hasAnyRole("ADMIN", "STAFF") // Staff hoặc Admin

                // 3. Các request còn lại bắt buộc phải đăng nhập
                .anyRequest().authenticated())

            // 4. Thêm Filter kiểm tra JWT trước khi check user/pass
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

**Giải thích:**

- **`authorizeHttpRequests`**: Định nghĩa "luật giao thông". Public API (sản phẩm, tin tức) được mở cửa tự do. API Admin bị khóa chặt.
- **`SessionCreationPolicy.STATELESS`**: Bắt buộc mọi request phải gửi lại Token, Server không nhớ Client là ai sau mỗi request.
- **`addFilterBefore`**: Đặt chốt kiểm soát JWT đứng trước chốt kiểm soát User/Pass mặc định.

---

### 2.2. Bộ Lọc JWT (`JwtAuthenticationFilter.java`)

Lớp "người gác cổng", kiểm tra mỗi request gửi đến.

- **File:** `src/main/java/com/flower/manager/security/JwtAuthenticationFilter.java`

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, ...) {
        try {
            // 1. Lấy JWT từ Header hoặc Cookie
            String jwt = parseJwt(request);

            // 2. Kiểm tra tính hợp lệ của Token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                // 3. Lấy username từ Token
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                // 4. Load thông tin User từ Database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Tạo đối tượng Authentication
                UsernamePasswordAuthenticationToken authentication = ...

                // 6. "Đóng dấu" xác thực vào Context (cho phép đi qua)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        // 7. Chuyển tiếp request đến bước tiếp theo
        filterChain.doFilter(request, response);
    }
}
```

**Giải thích:**

- Mỗi khi có request, hàm `doFilterInternal` sẽ chạy đầu tiên.
- Nếu Token chuẩn -> Hệ thống "nhận ra" người dùng và gán quyền.
- Nếu Token sai/thiếu -> Hệ thống coi như "khách vãng lai" (Anonymous).

---

### 2.3. Dịch Vụ Người Dùng (`CustomUserDetailsService.java`)

Cầu nối giữa Security và Database MySQL.

- **File:** `src/main/java/com/flower/manager/security/CustomUserDetailsService.java`

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        // Hỗ trợ đăng nhập bằng Username OR Email OR Số điện thoại
        User user = userRepository.findByUsernameOrEmailOrPhoneNumber(identifier)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Kiểm tra tài khoản có bị khóa không
        if (!user.getIsActive()) {
            throw new UsernameNotFoundException("Account disabled");
        }
        return user; // User entity implement UserDetails
    }
}
```

**Điểm đặc biệt:**

- Cho phép người dùng đăng nhập linh hoạt bằng 1 trong 3 thông tin: `username`, `email`, hoặc `phoneNumber`.
- Có kiểm tra cờ `isActive` để chặn ngay các tài khoản đã bị vô hiệu hóa (Ban).

---

### 2.4. Tiện Ích JWT (`JwtUtils.java`)

Công cụ tạo và kiểm tra chữ ký số.

- **File:** `src/main/java/com/flower/manager/security/JwtUtils.java`

```java
@Component
public class JwtUtils {
    // Ký Token bằng thuật toán HMAC-SHA (HS256/HS512)
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Hết hạn sau thời gian config
                .signWith(getSigningKey())
                .compact();
    }

    // Kiểm tra Token có bị giả mạo hoặc hết hạn không
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
            return true;
        } catch (Exception e) {
            // Log lỗi cụ thể: Hết hạn, Chữ ký sai, Token rỗng...
            return false;
        }
    }
}
```

---

## 3. Tổng Kết

Hệ thống bảo mật được xây dựng chặt chẽ với 4 lớp:

1.  **Config**: Luật chơi (PermitAll vs Authenticated).
2.  **Filter**: Người kiểm soát vé (Check Token).
3.  **Service**: Kiểm tra hồ sơ (Check Database).
4.  **Utils**: Công cụ đóng dấu (Sign/Verify Token).

Điều này đảm bảo API được bảo vệ an toàn, chỉ người dùng đã đăng nhập mới thực hiện được các chức năng quan trọng (Mua hàng, Đánh giá, Quản trị).
