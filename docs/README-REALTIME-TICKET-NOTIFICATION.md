Mình gộp 2 phần lại thành 1 file README hoàn chỉnh để bạn làm 1 lần là xong:

✅ Chat ticket realtime không reload (WebSocket STOMP)
✅ Notification realtime cho Admin + User (chuông thông báo, unread count)
✅ Có lưu DB → reload vẫn còn lịch sử
✅ Có phương án fallback (Polling / SSE)

Bạn chỉ cần copy file này vào dự án:
👉 README-REALTIME-TICKET-NOTIFICATION.md

🚀 README — Realtime Ticket Chat + Realtime Notifications (Spring Boot + React)
🎯 Mục tiêu

Xây dựng hệ thống:

✅ User chat với Admin theo ticket realtime (không reload)
✅ Admin nhận thông báo ngay khi user gửi tin nhắn / tạo ticket
✅ User nhận thông báo ngay khi admin phản hồi / đổi trạng thái
✅ Notification có lưu DB → dropdown hiển thị + unread count
✅ Kiến trúc chuẩn production, dễ mở rộng

🧱 Kiến trúc tổng thể
User Browser ─────┐
│ WebSocket (STOMP)
Admin Browser ─────┼──────────────▶ Spring Boot
│ │
│ ├── Ticket Service
│ ├── Notification Service
│ └── Database (MySQL/TiDB)
│
REST API ◀────────┘

Kênh realtime
Kênh Mục đích
/topic/tickets/{ticketId} Chat realtime cho từng ticket
/topic/notifications/admin/{adminId} Thông báo realtime cho admin
/topic/notifications/user/{userId} Thông báo realtime cho user
🗄️ Database
1️⃣ contact_messages (ticket)
Field Type
id BIGINT PK
user_id BIGINT
subject VARCHAR
message TEXT
status NEW / IN_PROGRESS / RESOLVED / CLOSED
created_at DATETIME
updated_at DATETIME
2️⃣ contact_replies (chat messages)
Field Type
id BIGINT PK
contact_id BIGINT
sender_type ADMIN / USER
sender_id BIGINT
content TEXT
created_at DATETIME
3️⃣ notifications (dropdown thông báo)
Field Type
id BIGINT PK
recipient_id BIGINT
recipient_role ADMIN / USER
type TICKET_NEW / TICKET_MESSAGE / STATUS
title VARCHAR
content VARCHAR
url VARCHAR
is_read BOOLEAN
created_at DATETIME
⚙️ Backend — WebSocket Setup
1️⃣ Dependency

pom.xml

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

2️⃣ WebSocket Config
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
registry.addEndpoint("/ws")
.setAllowedOriginPatterns("\*")
.withSockJS();
}

@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
registry.setApplicationDestinationPrefixes("/app");
registry.enableSimpleBroker("/topic");
}
}

Client connect URL:

http://localhost:8080/ws

📡 Realtime Payload Models
Ticket Realtime Payload
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRealtimePayload {
private Long ticketId;
private String type; // REPLY | STATUS
private Object data;
}

Notification Payload
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealtimeNotificationPayload {
private Long id;
private String type;
private String title;
private String content;
private String url;
private boolean isRead;
private String createdAt;
}

🔔 Notification Service
@Service
public class NotificationService {

@Autowired
private NotificationRepository notificationRepository;

@Autowired
private SimpMessagingTemplate messagingTemplate;

public void notifyAdmin(Long adminId, Notification n) {
Notification saved = notificationRepository.save(n);
messagingTemplate.convertAndSend(
"/topic/notifications/admin/" + adminId,
map(saved)
);
}

public void notifyUser(Long userId, Notification n) {
Notification saved = notificationRepository.save(n);
messagingTemplate.convertAndSend(
"/topic/notifications/user/" + userId,
map(saved)
);
}

private RealtimeNotificationPayload map(Notification n) {
return new RealtimeNotificationPayload(
n.getId(),
n.getType(),
n.getTitle(),
n.getContent(),
n.getUrl(),
n.isRead(),
n.getCreatedAt().toString()
);
}
}

🔁 Gắn realtime vào business flow
✅ A. Admin reply ticket

Endpoint:

POST /admin/tickets/{id}/reply

Service:

ReplyResponse reply = saveReply(...);

// 1. Broadcast chat realtime
messagingTemplate.convertAndSend(
"/topic/tickets/" + ticketId,
new TicketRealtimePayload(ticketId, "REPLY", reply)
);

// 2. Notify user
notificationService.notifyUser(ticketOwnerId,
Notification.builder()
.recipientId(ticketOwnerId)
.recipientRole("USER")
.type("TICKET_MESSAGE")
.title("Ticket có phản hồi mới")
.content("Admin vừa phản hồi ticket #" + ticketId)
.url("/tickets/" + ticketId)
.isRead(false)
.build()
);

✅ B. User gửi message
// Broadcast chat
messagingTemplate.convertAndSend(
"/topic/tickets/" + ticketId,
new TicketRealtimePayload(ticketId, "REPLY", reply)
);

// Notify admin
notificationService.notifyAdmin(adminId,
Notification.builder()
.recipientId(adminId)
.recipientRole("ADMIN")
.type("TICKET_MESSAGE")
.title("Tin nhắn mới")
.content("Ticket #" + ticketId + " có tin nhắn mới")
.url("/admin/tickets/" + ticketId)
.isRead(false)
.build()
);

✅ C. User tạo ticket
notificationService.notifyAdmin(adminId,
Notification.builder()
.type("TICKET_NEW")
.title("Ticket mới")
.content("Có ticket mới từ khách hàng")
.url("/admin/tickets/" + ticketId)
.isRead(false)
.build()
);

✅ D. Admin đổi trạng thái
messagingTemplate.convertAndSend(
"/topic/tickets/" + ticketId,
new TicketRealtimePayload(ticketId, "STATUS", Map.of("status", newStatus))
);

notificationService.notifyUser(userId,
Notification.builder()
.type("TICKET_STATUS")
.title("Trạng thái ticket thay đổi")
.content("Ticket #" + ticketId + " → " + newStatus)
.url("/tickets/" + ticketId)
.isRead(false)
.build()
);

🌐 Frontend — WebSocket Client
1️⃣ Install
npm install @stomp/stompjs sockjs-client

2️⃣ Connect helper
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";

export function connectRealtime(onConnect: (client: Client) => void) {
const client = new Client({
webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
reconnectDelay: 2000,
onConnect: () => onConnect(client),
});

client.activate();
return () => client.deactivate();
}

3️⃣ Ticket realtime (admin + user)
useEffect(() => {
const disconnect = connectRealtime((client) => {
client.subscribe(`/topic/tickets/${ticketId}`, (msg) => {
const payload = JSON.parse(msg.body);

      if (payload.type === "REPLY") {
        setMessages(prev => [...prev, payload.data]);
      }

      if (payload.type === "STATUS") {
        setTicket(t => ({ ...t, status: payload.data.status }));
      }
    });

});

return () => disconnect();
}, [ticketId]);

4️⃣ Notification realtime (chuông)
Admin
useEffect(() => {
if (!admin?.id) return;

const disconnect = connectRealtime((client) => {
client.subscribe(`/topic/notifications/admin/${admin.id}`, (msg) => {
const noti = JSON.parse(msg.body);
setNotifications(prev => [noti, ...prev]);
setUnread(u => u + 1);
});
});

return () => disconnect();
}, [admin?.id]);

User
client.subscribe(`/topic/notifications/user/${user.id}`, ...)

📬 Notification REST API
Method URL
GET /admin/notifications
GET /admin/notifications/unread-count
PATCH /admin/notifications/{id}/read
PATCH /admin/notifications/read-all

(Tương tự cho user)

🛡️ Security

Public REST: permitAll

Admin REST: ROLE_ADMIN

WebSocket:

Dev: mở tự do

Prod: attach JWT khi handshake

🧪 Test Checklist

✅ Admin gửi → user thấy realtime
✅ User gửi → admin thấy realtime
✅ Chuông nhảy thông báo
✅ Reload vẫn còn notification
✅ Badge unread đúng

⚡ Fallback Option (nếu chưa muốn WebSocket)
Polling
setInterval(() => {
fetch(`/tickets/${id}`)
}, 3000);

SSE

Server push 1 chiều, client chỉ nhận.

✅ DONE

Bạn đã có:
✔ Chat realtime
✔ Notification realtime
✔ Lưu DB
✔ Dropdown admin
✔ Không reload
