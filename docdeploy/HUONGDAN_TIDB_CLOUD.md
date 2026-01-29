# 📘 HƯỚNG DẪN ĐĂNG KÝ VÀ SỬ DỤNG TIDB CLOUD (MySQL Miễn Phí)

> **TiDB Cloud** là dịch vụ database cloud **tương thích MySQL** với **free tier 5GB storage**.  
> Hướng dẫn này dành cho người mới, từng bước có hình ảnh minh họa.

---

## 📋 MỤC LỤC

1. [Đăng ký tài khoản TiDB Cloud](#1-đăng-ký-tài-khoản-tidb-cloud)
2. [Tạo Cluster miễn phí](#2-tạo-cluster-miễn-phí)
3. [Lấy thông tin kết nối](#3-lấy-thông-tin-kết-nối)
4. [Tạo Database](#4-tạo-database)
5. [Import file SQL](#5-import-file-sql)
6. [Kết nối từ ứng dụng](#6-kết-nối-từ-ứng-dụng)
7. [Xử lý lỗi thường gặp](#7-xử-lý-lỗi-thường-gặp)

---

## 1. ĐĂNG KÝ TÀI KHOẢN TIDB CLOUD

### Bước 1.1: Truy cập trang web

**Lệnh:** Mở trình duyệt và truy cập:

```
https://tidbcloud.com/
```

### Bước 1.2: Bắt đầu đăng ký

**Thao tác:**

1. Click nút **"Start Free"** hoặc **"Sign Up Free"** (màu xanh, góc phải trên)
2. Chọn phương thức đăng ký:
   - ✅ **GitHub** (khuyến nghị - nhanh nhất)
   - ✅ Google
   - ✅ Email

### Bước 1.3: Đăng ký bằng GitHub (Khuyến nghị)

**Thao tác:**

```
1. Click "Sign up with GitHub"
2. Authorize PingCAP Cloud (cho phép truy cập)
3. Điền thông tin (nếu được yêu cầu):
   - Company/Organization: [Tên trường/công ty hoặc "Personal"]
   - Country: Vietnam
4. Click "Submit" hoặc "Get Started"
```

**Kiểm tra thành công:**

```
✓ Chuyển đến Dashboard của TiDB Cloud
✓ Thấy giao diện với menu bên trái
```

---

## 2. TẠO CLUSTER MIỄN PHÍ

### Bước 2.1: Chọn tạo Cluster mới

**Thao tác:**

```
1. Trong Dashboard, click "+ Create Cluster" (nút màu xanh)
   Hoặc: Nếu lần đầu, hệ thống sẽ tự động hiện form tạo cluster
```

### Bước 2.2: Chọn Serverless (Miễn phí)

**Thao tác:**

```
1. Chọn "Serverless" (có ghi "Free" hoặc "Always Free")
   ⚠️ KHÔNG chọn "Dedicated" (mất phí)

2. Cluster Name: flower-shop-db (hoặc tên tùy ý)

3. Cloud Provider: AWS (mặc định, giữ nguyên)

4. Region: Chọn GẦN VIỆT NAM nhất:
   - ap-southeast-1 (Singapore) ✅ KHUYẾN NGHỊ
   - ap-northeast-1 (Tokyo)

5. Click "Create" hoặc "Create Cluster"
```

**Kiểm tra thành công:**

```
✓ Cluster đang được tạo (Status: "Creating...")
✓ Đợi 1-3 phút cho đến khi Status = "Available" (màu xanh lá)
```

---

## 3. LẤY THÔNG TIN KẾT NỐI

### Bước 3.1: Mở cửa sổ Connect

**Thao tác:**

```
1. Click vào tên Cluster vừa tạo (flower-shop-db)
2. Click nút "Connect" (góc phải trên, màu xanh)
```

### Bước 3.2: Tạo mật khẩu

**Thao tác:**

```
1. Trong popup "Connect to Cluster":
   - Chọn tab "General" (hoặc "Password")
   - Click "Generate Password" hoặc "Create Password"

2. ⚠️ QUAN TRỌNG: LƯU LẠI MẬT KHẨU NÀY!
   - Copy password và lưu vào file text an toàn
   - Password chỉ hiển thị 1 lần, không thể xem lại!

3. Click "Download CA cert" (nếu có) - file này dùng cho SSL
```

### Bước 3.3: Copy thông tin kết nối

**Thao tác:**

```
1. Trong popup Connect, chọn:
   - Connect With: "General" hoặc "MySQL CLI"

2. Copy các thông tin sau:
```

**Thông tin cần lưu (mẫu):**

```properties
# === THÔNG TIN KẾT NỐI TIDB CLOUD ===
HOST=gateway01.ap-southeast-1.prod.aws.tidbcloud.com
PORT=4000
USERNAME=xxxxx.root
PASSWORD=[password-đã-generate]
DATABASE=java_flower
```

**Connection String mẫu:**

```
jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/java_flower?sslMode=VERIFY_IDENTITY
```

---

## 4. TẠO DATABASE

### Cách 1: Dùng SQL Editor trên Web (Đơn giản nhất)

**Thao tác:**

```
1. Trong TiDB Dashboard, menu trái → Click "SQL Editor" hoặc "Chat2Query"
2. Đợi SQL Editor load xong
3. Gõ lệnh:
```

```sql
CREATE DATABASE IF NOT EXISTS java_flower;
```

```
4. Click nút "Run" (▶️) hoặc nhấn Ctrl+Enter
5. Chạy tiếp lệnh để kiểm tra:
```

```sql
SHOW DATABASES;
```

**Kiểm tra thành công:**

```
✓ Thấy database "java_flower" trong danh sách
```

### Cách 2: Dùng MySQL Workbench (Nếu quen dùng)

**Bước 4.2.1: Mở MySQL Workbench**

```
1. Mở MySQL Workbench
2. Click "+" để tạo connection mới
```

**Bước 4.2.2: Cấu hình connection**

```
Connection Name: TiDB Cloud - Flower Shop
Connection Method: Standard TCP/IP

Hostname: gateway01.ap-southeast-1.prod.aws.tidbcloud.com
Port: 4000
Username: xxxxx.root (username từ TiDB)

Click "Store in Vault" → Nhập password
```

**Bước 4.2.3: Cấu hình SSL (Bắt buộc)**

```
1. Tab "SSL"
2. Use SSL: "Require"
3. SSL CA File: Chọn file CA cert đã download (nếu có)
   Hoặc để trống nếu dùng sslMode=VERIFY_IDENTITY
```

**Bước 4.2.4: Test và Connect**

```
1. Click "Test Connection"
2. Nếu thành công → Click "OK" để lưu
3. Double-click connection để kết nối
```

---

## 5. IMPORT FILE SQL

### Cách 1: Import qua SQL Editor (Web) - File nhỏ < 10MB

**Thao tác:**

```
1. Mở SQL Editor trên TiDB Dashboard
2. Chọn database:
   USE java_flower;

3. Copy toàn bộ nội dung file .sql của bạn
4. Paste vào SQL Editor
5. Click "Run" (▶️)

⚠️ Nếu file lớn, chia nhỏ và chạy từng phần:
   - Phần 1: CREATE TABLE statements
   - Phần 2: INSERT statements (chia theo từng table)
```

### Cách 2: Import qua MySQL CLI (File lớn)

**Bước 5.2.1: Mở PowerShell/CMD**

```powershell
# Di chuyển đến thư mục chứa file SQL
cd E:\DeAn_Java_Flowers\flower-manager
```

**Bước 5.2.2: Chạy lệnh import**

```powershell
# Lệnh import (thay thế các giá trị)
mysql -h gateway01.ap-southeast-1.prod.aws.tidbcloud.com -P 4000 -u xxxxx.root -p --ssl-mode=VERIFY_IDENTITY java_flower < your_database.sql
```

**Giải thích:**

- `-h`: Host của TiDB
- `-P`: Port (4000, không phải 3306)
- `-u`: Username
- `-p`: Sẽ hỏi password
- `--ssl-mode=VERIFY_IDENTITY`: Bắt buộc SSL
- `java_flower`: Tên database
- `< your_database.sql`: File SQL cần import

### Cách 3: Import qua MySQL Workbench (Giao diện)

**Thao tác:**

```
1. Kết nối đến TiDB Cloud bằng Workbench
2. Menu: Server → Data Import
3. Chọn "Import from Self-Contained File"
4. Browse chọn file .sql của bạn
5. Default Target Schema: java_flower
6. Click "Start Import"
7. Đợi hoàn thành (có progress bar)
```

### Cách 4: Import bằng TiDB Cloud Data Import (Khuyến nghị cho file lớn)

**Thao tác:**

```
1. TiDB Dashboard → Menu trái → "Import"
2. Click "Import Data"
3. Data Source: "Local File"
4. Upload file .sql của bạn
5. Target Database: java_flower
6. Click "Import"
7. Đợi quá trình hoàn thành
```

---

## 6. KẾT NỐI TỪ ỨNG DỤNG

### 6.1. Cập nhật biến môi trường Backend

**File:** `application-prod.properties` (đã tạo sẵn)

**Cập nhật các giá trị:**

```properties
# Thay thế bằng thông tin TiDB Cloud của bạn
spring.datasource.url=jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/java_flower?sslMode=VERIFY_IDENTITY
spring.datasource.username=xxxxx.root
spring.datasource.password=YOUR_GENERATED_PASSWORD
```

### 6.2. Test kết nối từ local

**Lệnh (PowerShell):**

```powershell
cd E:\DeAn_Java_Flowers\flower-manager

# Set biến môi trường
$env:DB_URL = "jdbc:mysql://gateway01.ap-southeast-1.prod.aws.tidbcloud.com:4000/java_flower?sslMode=VERIFY_IDENTITY"
$env:DB_USERNAME = "xxxxx.root"
$env:DB_PASSWORD = "YOUR_PASSWORD"

# Chạy ứng dụng với profile prod
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod
```

**Kiểm tra thành công:**

```
✓ Ứng dụng khởi động không có lỗi database
✓ Log hiển thị "HikariPool-1 - Start completed"
✓ Có thể truy cập http://localhost:8080/swagger-ui.html
```

---

## 7. XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi 1: "Access denied for user"

**Nguyên nhân:** Sai username hoặc password

**Giải pháp:**

```
1. Kiểm tra lại username (có dạng xxxxx.root, không phải chỉ root)
2. Generate password mới trên TiDB Dashboard:
   - Connect → Reset Password
3. Đảm bảo không có khoảng trắng thừa khi copy password
```

### Lỗi 2: "Communications link failure"

**Nguyên nhân:** Không kết nối được đến server

**Giải pháp:**

```
1. Kiểm tra host và port (port là 4000, KHÔNG phải 3306)
2. Kiểm tra kết nối internet
3. Thử ping đến host:
   ping gateway01.ap-southeast-1.prod.aws.tidbcloud.com
```

### Lỗi 3: "SSL connection required"

**Nguyên nhân:** Thiếu cấu hình SSL

**Giải pháp:**

```
Thêm sslMode=VERIFY_IDENTITY vào connection string:
jdbc:mysql://host:4000/database?sslMode=VERIFY_IDENTITY
```

### Lỗi 4: "Unknown database"

**Nguyên nhân:** Database chưa được tạo

**Giải pháp:**

```sql
-- Chạy trong SQL Editor
CREATE DATABASE IF NOT EXISTS java_flower;
```

### Lỗi 5: Import SQL thất bại - Syntax error

**Nguyên nhân:** Một số syntax MySQL không tương thích 100% với TiDB

**Giải pháp:**

```
1. Xóa các dòng không tương thích:
   - SET @@SESSION.SQL_LOG_BIN
   - LOCK TABLES / UNLOCK TABLES

2. Thay thế:
   - ENGINE=MyISAM → ENGINE=InnoDB

3. Chia file SQL thành nhiều phần nhỏ và import từng phần
```

---

## 📊 THÔNG TIN FREE TIER

| Tính năng     | Giới hạn miễn phí |
| ------------- | ----------------- |
| Storage       | 5 GB              |
| Request Units | 50 triệu RU/tháng |
| Bandwidth     | 10 GB/tháng       |
| Clusters      | 5 clusters        |

> 💡 **Tip:** 5GB đủ cho ~100,000 sản phẩm + orders cho demo/development

---

## ✅ CHECKLIST HOÀN THÀNH

- [ ] Đã đăng ký tài khoản TiDB Cloud
- [ ] Đã tạo Cluster Serverless (miễn phí)
- [ ] Đã generate và lưu password
- [ ] Đã copy thông tin kết nối (Host, Port, Username)
- [ ] Đã tạo database `java_flower`
- [ ] Đã import file SQL thành công
- [ ] Đã test kết nối từ ứng dụng local

---

## 🔗 LIÊN KẾT HỮU ÍCH

- [TiDB Cloud Documentation](https://docs.pingcap.com/tidbcloud/)
- [TiDB vs MySQL Compatibility](https://docs.pingcap.com/tidb/stable/mysql-compatibility)
- [TiDB Cloud Pricing](https://www.pingcap.com/tidb-cloud-pricing/)

---

> **Tổng thời gian:** ~10-15 phút cho người mới
