# 🔍 DEBUG - TẠI SAO KHÔNG HIỂN THỊ BÀI VIẾT?

## Vấn đề

Admin đã import/tạo bài viết nhưng danh sách bài viết hiển thị "Chua co bai viet nao"

---

## ✅ CHECKLIST DEBUG

### 1️⃣ Kiểm tra Database

```sql
-- Xem tất cả bài viết
SELECT id, title, status, published_at, created_at
FROM articles
ORDER BY created_at DESC;
```

**Expected:**

- Có ít nhất 1 bài viết
- Nếu muốn hiển thị công khai: `status = 'PUBLISHED'`
- Nếu muốn hiển thị trong admin: bất kỳ status nào

**Fix nếu status = DRAFT:**

```sql
UPDATE articles
SET status = 'PUBLISHED', published_at = NOW()
WHERE id = 4;
```

---

### 2️⃣ Kiểm tra Backend API

**Admin endpoint:**

```
GET http://localhost:8080/api/admin/articles?page=0&size=10
```

**Public endpoint:**

```
GET http://localhost:8080/api/news?page=0&size=10
```

**Expected response structure:**

```json
{
  "success": true,
  "status": 200,
  "message": null,
  "data": {
    "content": [
      {
        "id": 4,
        "title": "Bài viết test",
        "slug": "bai-viet-test",
        "status": "PUBLISHED",
        "author": "FlowerCorner Team"
      }
    ],
    "totalPages": 1,
    "totalElements": 1,
    "number": 0,
    "size": 10
  }
}
```

**Nếu `totalElements = 0`:**

- Kiểm tra lại database
- Kiểm tra filter status trong query params

---

### 3️⃣ Kiểm tra Frontend Console

1. Mở browser DevTools (F12)
2. Tab **Console**
3. Refresh trang `/admin/articles`
4. Tìm log:

```
Admin articles response: { ... }
Extracted articles: [ ... ]
Total: X
```

**Case 1: Thấy response nhưng Total = 0**
→ Vấn đề: Logic parse response sai

**Fix:**

- File: `ArticleList.js`
- Dòng ~70-100
- Check: `response.data.data.content` vs `response.data.content`

**Case 2: Không thấy log gì**
→ Vấn đề: API call failed hoặc code cũ chưa refresh

**Fix:**

- Hard reload: Ctrl + F5
- Clear cache: Ctrl + Shift + Delete
- Restart dev server: `npm start`

**Case 3: Response 401/403**
→ Vấn đề: Chưa login hoặc không có quyền admin

**Fix:**

- Login với tài khoản ADMIN
- Check JWT token trong localStorage

---

### 4️⃣ Kiểm tra Response Parsing

Trong `ArticleList.js`, logic hiện tại:

```javascript
// Handle nested ApiResponse wrapper
let pageData = null;

if (response?.data?.data?.content) {
  // ApiResponse wrapper: { data: { data: { content: [...] } } }
  pageData = response.data.data;
} else if (response?.data?.content) {
  // Direct axios response: { data: { content: [...] } }
  pageData = response.data;
} else if (response?.content) {
  // Already unwrapped: { content: [...] }
  pageData = response;
}
```

**Debug trong console:**

```javascript
console.log("Full response:", JSON.stringify(response, null, 2));
console.log("response.data:", response.data);
console.log("response.data.data:", response.data?.data);
console.log("response.data.data.content:", response.data?.data?.content);
```

---

### 5️⃣ Kiểm tra Network Tab

1. DevTools → **Network** tab
2. Filter: **Fetch/XHR**
3. Refresh trang
4. Tìm request: `admin/articles?page=...`
5. Click vào request → **Response** tab

**Check:**

- Status code: 200 OK
- Response body có `"success": true`
- `data.content` là array
- `data.totalElements > 0`

---

## 🚨 COMMON ISSUES

### Issue 1: "Chua co bai viet nao" mặc dù đã có bài

**Nguyên nhân:** Status filter đang chọn không khớp với status của bài viết

**Fix:**

1. Vào filter dropdown
2. Chọn "Tất cả trạng thái"
3. Hoặc chọn đúng status của bài (VD: DRAFT)

### Issue 2: Console log "Total: 0"

**Nguyên nhân:** Backend không trả về data hoặc parse sai

**Fix:**

1. Kiểm tra endpoint URL (xem Network tab)
2. Kiểm tra authentication token
3. Check backend logs: `.\mvnw spring-boot:run`

### Issue 3: Cannot read property 'content' of undefined

**Nguyên nhân:** Response structure không đúng expectations

**Fix:**

1. Add defensive coding:
   ```javascript
   articlesData = pageData?.content || [];
   ```
2. Add error boundary

---

## 📊 RESPONSE FORMAT EXAMPLES

### Format 1: ApiResponse Wrapper (Most common)

```json
{
  "success": true,
  "status": 200,
  "message": null,
  "data": {
    "content": [...],
    "totalPages": 1,
    "totalElements": 1
  }
}
```

→ Access: `response.data.data.content`

### Format 2: Direct Spring Page

```json
{
  "content": [...],
  "totalPages": 1,
  "totalElements": 1,
  "number": 0
}
```

→ Access: `response.data.content`

### Format 3: Plain Array (Legacy)

```json
[...]
```

→ Access: `response.data`

---

## 🎯 QUICK FIX COMMANDS

### Backend

```bash
# Rebuild backend
cd e:\DeAn_Java_Flowers\flower-manager
.\mvnw clean compile

# Run backend
.\mvnw spring-boot:run
```

### Frontend

```bash
# Restart dev server
cd e:\DeAn_Java_Flowers\flower-shop-frontend
npm start
```

### Database

```sql
-- Check articles count
SELECT COUNT(*) FROM articles;

-- Publish all drafts
UPDATE articles SET status = 'PUBLISHED', published_at = NOW() WHERE status = 'DRAFT';

-- View all articles
SELECT * FROM articles ORDER BY created_at DESC;
```

---

## ✨ SUCCESS CRITERIA

✅ Backend API trả về `totalElements > 0`  
✅ Frontend console log hiển thị array có data  
✅ UI hiển thị danh sách bài viết  
✅ Có thể click vào bài viết để xem chi tiết

---

**Last updated:** 2026-01-21  
**Author:** FlowerCorner Dev Team
