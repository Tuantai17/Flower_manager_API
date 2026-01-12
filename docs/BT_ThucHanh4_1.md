Bài thực hành sử dụng Spring Security
Yêu cầu nộp bài: Giải thích khái niệm và quy trình hoạt động của Spring Security trong bài này và chụp kết quả.
Tiếp tục bài thực hành trước (đã xong quản lý product, category), tạo đối tượng user (id, username, password, role) sử dụng các thành phần:
●	Xác thực với InMemoryUserDetailsManager 
●	Chuyển sang xác thực với UserDetailsService + Database (MySQL/PostgreSQL)
●	Tạo UserDetailsService để lấy user từ MySQL 
●	Băm mật khẩu với BcryptPasswordEncoder
●	Cấu hình Authentication Provider
Với các chức năng:
Có các chức năng như đăng kí, đăng nhập, đăng xuất. Admin có thể xem danh sách user (có trong hướng dẫn), thao tác với CRUD product, category còn user chỉ xem được danh sách sản phẩm, category
Cài đặt thêm vào thư viện: Spring Security
 

Hoặc làm theo hướng dẫn
 
Cấu hình application.properties để kết nối MySQL hoặc cái khác
spring.datasource.url=jdbc:mysql://localhost:3306/demo-test?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.springframework.security=DEBUG //???
Bước 1:	Tạo Entity User
 
Entity UserEntity tương ứng với bảng users trong MySQL.
Chạy chương trình để tạo bảng
Trường username phải là duy nhất.
Bước 2:	Tạo Admin có sẵn
Trong Config sử dụng SecurityConfig để:
●	Mật khẩu được băm bằng BCryptPasswordEncoder.
●	Dùng InMemoryUserDetailsManager để lưu trữ user.
 
Chuyển sang xác thực với UserDetailsService + Database

Bước 3:	Tạo DTO và Mapper
@Data
@AllArgsConstructor
public class UserDTO {
   private Long id;
   private String username;
   private String role;
}
Mapper
….
Bước 4:	Tạo Repository UserRepository
 
findByUsername(String username): Tìm user theo username.
Bước 5:	Tạo UserDetailsServiceImpl để load user từ MySQL
 
Load user từ MySQL bằng UserRepository.
Nếu user không tồn tại, ném UsernameNotFoundException.
Trả về UserDetails chứa thông tin user.

Bước 6:	Cấu hình SecurityConfig với UserDetailsService
 
 
Bước 7: Controller
 
 
Kiểm tra
ở postman
1.	Đăng kí
http://localhost:8080/api/auth/register
 
2.	Đăng nhập
 
3.	Lấy danh sách user (GET)
http://localhost:8080/api/auth/users
