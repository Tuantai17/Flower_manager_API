# Hướng dẫn Test Quản lý Tồn kho trên Postman

## Tổng quan tính năng đã triển khai

### 1. Kiểm tra khi thêm vào giỏ hàng
- ❌ Không cho thêm khi sản phẩm hết hàng (stock = 0)
- ❌ Không cho thêm khi số lượng vượt quá tồn kho
- ❌ Không cho số lượng âm hoặc bằng 0

### 2. Kiểm tra khi cập nhật giỏ hàng
- ❌ Không cho cập nhật số lượng vượt quá tồn kho
- ❌ Không cho số lượng âm

### 3. Lịch sử tồn kho
- Ghi lại mọi biến động: nhập hàng, xuất hàng, đặt hàng, hủy đơn, điều chỉnh

---

## API Quản lý Tồn kho (Admin)

### 1. Điều chỉnh tồn kho
**POST** `/api/admin/stock/adjust`

**Headers:**
```
Authorization: Bearer <ADMIN_TOKEN>
Content-Type: application/json
```

**Body - Nhập hàng (tăng tồn kho):**
```json
{
    "productId": 1,
    "changeQuantity": 50,
    "reason": "IMPORT",
    "note": "Nhập hàng từ nhà cung cấp ABC"
}
```

**Body - Xuất hàng (giảm tồn kho):**
```json
{
    "productId": 1,
    "changeQuantity": -10,
    "reason": "EXPORT",
    "note": "Xuất hàng cho sự kiện"
}
```

**Các lý do hợp lệ (reason):**
- `ORDER_PLACED` - Trừ khi đặt hàng
- `ORDER_CANCELLED` - Hoàn khi hủy đơn
- `IMPORT` - Nhập hàng
- `EXPORT` - Xuất hàng
- `ADMIN_ADJUST` - Admin điều chỉnh thủ công
- `RETURN` - Khách trả hàng
- `DAMAGED` - Hàng hư hỏng
- `INITIAL` - Khởi tạo

---

### 2. Xem tồn kho hiện tại
**GET** `/api/admin/stock/current/{productId}`

**Response:**
```json
{
    "productId": 1,
    "currentStock": 45
}
```

---

### 3. Kiểm tra đủ tồn kho
**GET** `/api/admin/stock/check/{productId}?quantity=10`

**Response:**
```json
{
    "productId": 1,
    "requestedQuantity": 10,
    "currentStock": 45,
    "hasEnoughStock": true
}
```

---

### 4. Xem lịch sử tồn kho
**GET** `/api/admin/stock/history/{productId}`

**Response:**
```json
[
    {
        "id": 5,
        "productId": 1,
        "productName": "Hoa Hồng Đỏ",
        "changeQuantity": -2,
        "finalQuantity": 43,
        "reason": "ORDER_PLACED",
        "reasonDisplayName": "Đặt hàng",
        "orderCode": "ORD-20231221-ABC123",
        "createdBy": "user1",
        "createdAt": "2023-12-21T10:30:00"
    }
]
```

---

### 5. Lấy danh sách lý do
**GET** `/api/admin/stock/reasons`

---

## Test Giỏ hàng (User)

### Test 1: Thêm sản phẩm hết hàng
**POST** `/api/cart/add`
```json
{
    "productId": 1,
    "quantity": 1
}
```
**Expected:** Lỗi `OUT_OF_STOCK` nếu stockQuantity = 0

---

### Test 2: Thêm vượt quá tồn kho
**POST** `/api/cart/add`
```json
{
    "productId": 1,
    "quantity": 1000
}
```
**Expected:** Lỗi `EXCEED_STOCK`

---

### Test 3: Thêm số lượng âm
**POST** `/api/cart/add`
```json
{
    "productId": 1,
    "quantity": -5
}
```
**Expected:** Lỗi `INVALID_QUANTITY`

---

### Test 4: Cập nhật vượt quá tồn kho
**PUT** `/api/cart/update`
```json
{
    "productId": 1,
    "quantity": 9999
}
```
**Expected:** Lỗi `EXCEED_STOCK`

---

## Quy trình Test đầy đủ

### Bước 1: Đăng nhập Admin
```
POST /api/auth/login
{
    "identifier": "admin",
    "password": "admin123"
}
```

### Bước 2: Kiểm tra tồn kho sản phẩm
```
GET /api/admin/stock/current/1
Authorization: Bearer <ADMIN_TOKEN>
```

### Bước 3: Nhập hàng (tăng tồn kho)
```
POST /api/admin/stock/adjust
{
    "productId": 1,
    "changeQuantity": 100,
    "reason": "IMPORT",
    "note": "Nhập hàng test"
}
```

### Bước 4: Đăng nhập User thường
```
POST /api/auth/login
{
    "identifier": "testuser",
    "password": "password123"
}
```

### Bước 5: Test thêm vào giỏ
```
POST /api/cart/add
Authorization: Bearer <USER_TOKEN>
{
    "productId": 1,
    "quantity": 5
}
```

### Bước 6: Test thêm vượt quá tồn kho
```
POST /api/cart/add
{
    "productId": 1,
    "quantity": 200
}
```
→ Phải báo lỗi `EXCEED_STOCK`

### Bước 7: Kiểm tra lịch sử
```
GET /api/admin/stock/history/1
Authorization: Bearer <ADMIN_TOKEN>
```
