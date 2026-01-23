# 🐛 KHẮC PHỤC LỖI GEMINI API - 500 INTERNAL SERVER ERROR

## ⚠️ TRIỆU CHỨNG

```
POST http://localhost:8080/api/admin/articles/ai-generate 500 (Internal Server Error)
```

**Lỗi từ Gemini API:**

- "You exceeded your current quota"
- "RESOURCE_EXHAUSTED"
- "429 Too Many Requests"

---

## 🔍 NGUYÊN NHÂN

### 1. **API Key hết quota** (Phổ biến nhất)

Gemini API Free tier có giới hạn:

- **60 requests/minute**
- **1500 requests/day**
- **32,000 tokens/minute**

### 2. **API Key không hợp lệ**

- Key bị revoke
- Key chưa enable Gemini API
- Key không có quyền truy cập

### 3. **Model không tồn tại**

- `gemini-2.0-flash` có thể chưa available
- Nên dùng `gemini-1.5-flash` hoặc `gemini-1.5-pro`

---

## ✅ CÁCH KHẮC PHỤC

### Bước 1: Tạo API Key mới

1. **Truy cập:** https://aistudio.google.com/apikey
2. **Đăng nhập** với tài khoản Google
3. **Click "Create API Key"**
4. **Copy** API key mới

### Bước 2: Cập nhật API Key

**File:** `application.properties`

```properties
# Gemini AI Configuration
gemini.api-key=YOUR_NEW_API_KEY_HERE
gemini.model=gemini-1.5-flash
gemini.max-tokens=4096
gemini.timeout=30
gemini.base-url=https://generativelanguage.googleapis.com/v1beta
gemini.temperature=0.7
```

**Hoặc dùng Environment Variable:**

```bash
# Windows CMD
set GEMINI_API_KEY=YOUR_API_KEY_HERE
.\mvnw spring-boot:run

# Windows PowerShell
$env:GEMINI_API_KEY="YOUR_API_KEY_HERE"
.\mvnw spring-boot:run

# Linux/Mac
export GEMINI_API_KEY=YOUR_API_KEY_HERE
./mvnw spring-boot:run
```

### Bước 3: Kiểm tra Model

Đổi model sang **stable version**:

```properties
gemini.model=gemini-1.5-flash
```

**Available models:**

- ✅ `gemini-1.5-flash` (Recommended - nhanh, rẻ)
- ✅ `gemini-1.5-pro` (Chất lượng cao hơn)
- ❌ `gemini-2.0-flash` (Beta - có thể không stable)

### Bước 4: Enable Billing (Nếu cần)

Nếu cần quota cao hơn:

1. Vào: https://console.cloud.google.com/
2. Chọn project
3. **Enable Billing**
4. **Enable Generative Language API**

---

## 🧪 TEST API KEY

Test trực tiếp bằng `curl`:

```bash
curl -X POST "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=YOUR_API_KEY" ^
  -H "Content-Type: application/json" ^
  -d "{ \"contents\": [{ \"parts\": [{ \"text\": \"Hello AI\" }] }] }"
```

**Expected response:**

```json
{
  "candidates": [
    {
      "content": {
        "parts": [{ "text": "..." }]
      }
    }
  ]
}
```

**Nếu lỗi:**

```json
{
  "error": {
    "code": 429,
    "message": "Resource has been exhausted",
    "status": "RESOURCE_EXHAUSTED"
  }
}
```

→ **API key hết quota**, cần tạo key mới hoặc enable billing.

---

## 💡 GIẢI PHÁP TẠM THỜI

Nếu không có API key hoặc đang hết quota, có thể:

### Option 1: Skip AI Generation (Manual mode)

Tắt tính năng AI, nhập content thủ công.

### Option 2: Dùng Mock Data

Update `ArticleAIService.java`:

```java
private String callGeminiAPI(String prompt) {
    // MOCK MODE - Remove this in production
    log.warn("🚧 MOCK MODE: Returning fake AI response");
    return """
        {
            "title": "Bài viết về hoa tươi",
            "summary": "Khám phá vẻ đẹp của hoa tươi trong cuộc sống",
            "content": "<h2>Giới thiệu</h2><p>Hoa tươi mang lại niềm vui...</p>",
            "tags": ["hoa", "tươi", "quà tặng"],
            "thumbnailPrompt": "Beautiful fresh flowers"
        }
        """;
}
```

### Option 3: Dùng API key khác

Tạo nhiều tài khoản Google → Nhiều free API keys → Rotate keys.

---

## 📊 MONITORING QUOTA

Check quota usage tại:
https://console.cloud.google.com/apis/api/generativelanguage.googleapis.com/quotas

**Free tier limits:**

- 60 RPM (requests per minute)
- 1500 RPD (requests per day)
- 32K TPM (tokens per minute)

---

## 🚀 RESTART BACKEND

Sau khi update API key:

```bash
# Stop backend (Ctrl + C nếu đang chạy)

# Rebuild
.\mvnw clean compile

# Run
.\mvnw spring-boot:run
```

---

## 🔐 BẢO MẬT API KEY

**KHÔNG** commit API key vào Git!

`.gitignore`:

```
application-local.properties
.env
```

**Dùng env file:**

```properties
# application-local.properties (local only, không commit)
gemini.api-key=YOUR_SECRET_KEY
```

---

## ✨ KIỂM TRA HOẠT ĐỘNG

1. **Restart backend**
2. **Vào**: `http://localhost:3000/admin/articles/ai-generate`
3. **Nhập topic**: "Hoa hồng đỏ"
4. **Click "Tạo bài viết"**
5. **Kiểm tra logs backend**:

```
🤖 Generating article with AI: topic=Hoa hồng đỏ
🌐 Calling Gemini API: model=gemini-1.5-flash
✅ Gemini API returned 1234 characters
✅ AI article generated and saved: id=5, title=...
```

**Nếu thấy lỗi:**

```
❌ HTTP Client Error: 429 - {...}
❌ Đã vượt quá quota API...
```

→ Làm theo các bước trên để fix.

---

## 📞 HỖ TRỢ

**Nếu vẫn lỗi:**

1. Check backend logs chi tiết
2. Verify API key tại: https://aistudio.google.com/apikey
3. Test với `curl` command ở trên
4. Share error logs để debug

---

**Last updated:** 2026-01-21  
**Author:** FlowerCorner Dev Team
