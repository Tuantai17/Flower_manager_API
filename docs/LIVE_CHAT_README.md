# GIAI ĐOẠN 2 — LIVE CHAT REALTIME

## 📋 Tổng quan

Đã triển khai Live Chat realtime giống Shopee cho website thương mại điện tử, sử dụng WebSocket STOMP over SockJS.

## 🏗️ Kiến trúc

```
┌──────────────────┐        WebSocket (STOMP/SockJS)       ┌──────────────────┐
│   USER CLIENT    │───────────────────────────────────────│   ADMIN PANEL    │
│   (ChatWidget)   │                                       │ (AdminChatPanel) │
└────────┬─────────┘                                       └────────┬─────────┘
         │                                                          │
         │ /topic/chat/{sessionId}                                  │ /topic/admin/notifications
         │ /topic/admin-status                                      │ /topic/admin/online-status
         │                                                          │
         └───────────────────────┬──────────────────────────────────┘
                                 │
                     ┌───────────▼───────────┐
                     │   Spring Boot Server   │
                     │   (WebSocket Handler)  │
                     │                        │
                     │  /ws/chat endpoint     │
                     │  /app/chat.send        │
                     │  /app/chat.typing      │
                     └───────────┬───────────┘
                                 │
                     ┌───────────▼───────────┐
                     │       MySQL DB         │
                     │   chat_sessions        │
                     │   chat_messages        │
                     └───────────────────────┘
```

## 📁 Files đã tạo/sửa

### Backend (Spring Boot)

| File                                      | Mô tả                              |
| ----------------------------------------- | ---------------------------------- |
| `pom.xml`                                 | Thêm spring-boot-starter-websocket |
| `config/WebSocketConfig.java`             | Cấu hình WebSocket STOMP           |
| `dto/chat/WebSocketMessageDTO.java`       | DTO cho WebSocket messages         |
| `dto/chat/OnlineStatusDTO.java`           | DTO cho online status              |
| `dto/chat/TypingIndicatorDTO.java`        | DTO cho typing indicator           |
| `dto/chat/ChatSessionDTO.java`            | Thêm isUserOnline, userName        |
| `service/chat/LiveChatService.java`       | Service xử lý live chat            |
| `controller/WebSocketChatController.java` | WebSocket message mappings         |
| `controller/LiveChatController.java`      | REST API cho live chat             |
| `repository/ChatSessionRepository.java`   | Thêm query methods                 |
| `config/SecurityConfig.java`              | Cho phép /ws/**, /api/livechat/**  |

### Frontend (React)

| File                                  | Mô tả                        |
| ------------------------------------- | ---------------------------- |
| `services/webSocketService.js`        | WebSocket client service     |
| `api/liveChatApi.js`                  | REST API cho live chat       |
| `components/common/ChatWidget.js`     | Cập nhật với WebSocket       |
| `components/common/ChatWidget.css`    | Styles cho chat widget       |
| `components/admin/AdminChatPanel.js`  | Panel quản lý chat cho admin |
| `components/admin/AdminChatPanel.css` | Styles cho admin panel       |
| `pages/admin/chat/LiveChatPage.js`    | Trang Live Chat admin        |
| `App.js`                              | Thêm route /admin/live-chat  |
| `components/admin/Sidebar.js`         | Thêm menu Live Chat          |

## 🔌 Endpoints

### WebSocket

| Destination                      | Mô tả                       |
| -------------------------------- | --------------------------- |
| `/ws/chat`                       | WebSocket endpoint (SockJS) |
| `/app/chat.send`                 | Gửi tin nhắn                |
| `/app/chat.typing`               | Typing indicator            |
| `/app/chat.connect`              | User connects               |
| `/app/chat.admin.connect`        | Admin connects              |
| `/app/chat.read`                 | Mark as read                |
| `/topic/chat/{sessionId}`        | Subscribe tin nhắn session  |
| `/topic/chat/{sessionId}/typing` | Subscribe typing indicator  |
| `/topic/admin/notifications`     | Admin nhận thông báo mới    |
| `/topic/admin/online-status`     | Admin theo dõi user online  |
| `/topic/admin-status`            | User theo dõi admin online  |

### REST API

| Endpoint                                    | Method | Mô tả              |
| ------------------------------------------- | ------ | ------------------ |
| `/api/livechat/admin-online`                | GET    | Check admin online |
| `/api/livechat/sessions/{id}/unread`        | GET    | Get unread count   |
| `/api/livechat/sessions/{id}/read`          | POST   | Mark as read       |
| `/api/livechat/admin/sessions`              | GET    | Active sessions    |
| `/api/livechat/admin/sessions/waiting`      | GET    | Waiting sessions   |
| `/api/livechat/admin/status`                | GET    | Admin status info  |
| `/api/livechat/admin/sessions/{id}/message` | POST   | Send admin message |

## ✅ Tính năng đã triển khai

- [x] **Chat Realtime** - WebSocket STOMP over SockJS
- [x] **Phân quyền** - USER chỉ chat với shop, ADMIN chat với nhiều user
- [x] **Session Management** - Guest session tạm, User login gắn userId
- [x] **Lưu lịch sử** - Messages lưu database, reload vẫn xem được
- [x] **Trạng thái Online** - User/Admin online indicator
- [x] **Typing Indicator** - Hiển thị khi đang gõ
- [x] **Thông báo tin nhắn mới** - Admin nhận notification, User thấy badge
- [x] **Admin Panel** - Danh sách conversations, chat realtime

## 🚀 Cách sử dụng

### User Side

1. Click button chat 💬 góc phải dưới
2. Nhập tin nhắn và gửi
3. Nếu admin online, sẽ thấy "🟢 Đang có nhân viên hỗ trợ"

### Admin Side

1. Đăng nhập Admin Panel
2. Click menu "Live Chat" trong sidebar
3. Chọn session từ danh sách bên trái
4. Chat trực tiếp với khách hàng

## 🧪 Test

1. Start backend: `cd flower-manager && mvnw spring-boot:run`
2. Start frontend: `cd flower-shop-frontend && npm start`
3. Mở http://localhost:3000 (User)
4. Mở http://localhost:3000/admin/live-chat (Admin)
5. Test gửi tin nhắn giữa 2 browser tabs

## 📝 Lưu ý

- WebSocket reconnect tự động khi mất kết nối
- Tin nhắn được lưu ngay vào database
- Admin có thể thấy tất cả sessions active
- User chỉ thấy session của mình
