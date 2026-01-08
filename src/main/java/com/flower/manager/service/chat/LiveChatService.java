package com.flower.manager.service.chat;

import com.flower.manager.dto.chat.*;
import com.flower.manager.entity.*;
import com.flower.manager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Service for Live Chat with WebSocket
 * Handles realtime messaging between users and admins
 * Integrates with AI bot for automatic responses
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LiveChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ProductRepository productRepository;
    private final GeminiService geminiService;
    private final ChatbotResponses chatbotResponses;
    private final CategoryRepository categoryRepository;

    // Track online users: Map<sessionId, OnlineStatusDTO>
    private final Map<String, OnlineStatusDTO> onlineUsers = new ConcurrentHashMap<>();

    // Track online admins: Map<staffId, OnlineStatusDTO>
    private final Map<Long, OnlineStatusDTO> onlineAdmins = new ConcurrentHashMap<>();

    // ==================== Public Test Methods ====================

    /**
     * Check if Gemini is configured
     */
    public boolean isGeminiConfigured() {
        return geminiService.isConfigured();
    }

    /**
     * Build context for testing
     */
    public String buildContextForTest() {
        return buildShopContext("test message");
    }

    /**
     * Test Gemini response directly
     */
    public String testGeminiResponse(String message, String context) {
        log.info("=== TESTING GEMINI RESPONSE ===");
        log.info("Message: {}", message);
        log.info("Context length: {}", context != null ? context.length() : 0);

        String response = geminiService.generateResponse(message, context, new ArrayList<>());
        log.info("Gemini response: {}", response);
        return response;
    }

    // ==================== Message Handling ====================

    /**
     * Send message via WebSocket (realtime)
     * Also saves to database and generates AI response if in BOT mode
     */
    @Transactional
    public ChatMessageDTO sendLiveMessage(WebSocketMessageDTO messageDTO, User sender) {
        log.info("Processing live message from {} to session {}",
                messageDTO.getSenderType(), messageDTO.getSessionId());

        // Get or create session
        ChatSession session = getOrCreateSession(messageDTO, sender);

        // Save message to database
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .senderType(messageDTO.getSenderType())
                .senderId(sender != null ? sender.getId() : null)
                .senderName(messageDTO.getSenderName() != null ? messageDTO.getSenderName()
                        : (sender != null ? sender.getFullName() : "Khách"))
                .content(messageDTO.getContent())
                .messageType(messageDTO.getMessageType() != null ? messageDTO.getMessageType() : "TEXT")
                .metadata(messageDTO.getMetadata())
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        // Update session
        session.setUpdatedAt(LocalDateTime.now());
        if (session.getTitle().equals("Cuộc trò chuyện mới")) {
            String title = messageDTO.getContent().length() > 50
                    ? messageDTO.getContent().substring(0, 47) + "..."
                    : messageDTO.getContent();
            session.setTitle(title);
        }
        sessionRepository.save(session);

        // Create response DTO
        ChatMessageDTO responseDTO = mapToMessageDTO(message);

        // Broadcast to session topic
        broadcastToSession(session.getId(), responseDTO);

        // Handle based on sender type and session status
        if ("USER".equals(messageDTO.getSenderType())) {
            // Notify admin dashboard
            notifyAdminsNewMessage(session, responseDTO);

            // If session is in BOT mode (ACTIVE), generate AI response
            if ("ACTIVE".equals(session.getStatus())) {
                generateAndSendBotResponse(messageDTO.getContent(), session);
            }
        }

        return responseDTO;
    }

    /**
     * Generate AI response and send to user
     */
    private void generateAndSendBotResponse(String userMessage, ChatSession session) {
        log.info("Generating bot response for session: {}", session.getId());

        try {
            // Generate bot response
            String botResponse = generateBotResponse(userMessage, session);

            // Save bot message
            ChatMessage botMessage = ChatMessage.builder()
                    .session(session)
                    .senderType("BOT")
                    .senderName("Trợ lý Flower Shop")
                    .content(botResponse)
                    .messageType("TEXT")
                    .isRead(false)
                    .build();

            botMessage = messageRepository.save(botMessage);

            // Broadcast bot response to session
            ChatMessageDTO botResponseDTO = mapToMessageDTO(botMessage);
            broadcastToSession(session.getId(), botResponseDTO);

            log.info("Bot response sent to session: {}", session.getId());
        } catch (Exception e) {
            log.error("Error generating bot response: {}", e.getMessage(), e);
        }
    }

    /**
     * Generate bot response using Gemini AI or fallback
     * Follows RAG (Retrieval Augmented Generation) pattern
     */
    private String generateBotResponse(String userMessage, ChatSession session) {
        log.info("=== GENERATING BOT RESPONSE ===");
        log.info("User message: {}", userMessage);

        try {
            // STEP 1: Build comprehensive context with REAL product data (RAG Retrieval)
            String context = buildShopContext(userMessage);
            log.info("Context built, length: {} chars", context.length());

            // Log first 500 chars of context for debugging
            if (context.length() > 0) {
                log.info("Context preview: {}...", context.substring(0, Math.min(500, context.length())));
            }

            // STEP 2: Build conversation history
            List<ChatMessage> recentMessages = messageRepository.findTop50BySessionOrderBySentAtDesc(session);
            List<String> history = new ArrayList<>();
            if (recentMessages != null) {
                for (ChatMessage msg : recentMessages) {
                    String prefix = msg.isFromUser() ? "Khách: " : "Bot: ";
                    history.add(prefix + msg.getContent());
                }
                Collections.reverse(history);
            }
            log.info("Conversation history: {} messages", history.size());

            // STEP 3: Call Gemini API (RAG Augmented Generation)
            if (geminiService.isConfigured()) {
                log.info("Calling Gemini API with context...");
                String geminiResponse = geminiService.generateResponse(userMessage, context, history);

                if (geminiResponse != null && !geminiResponse.trim().isEmpty()) {
                    log.info("Gemini response received (length: {})", geminiResponse.length());
                    return geminiResponse;
                } else {
                    log.warn("Gemini returned empty or null response");
                }
            } else {
                log.warn("Gemini API not configured - API key missing or empty");
            }

            // STEP 4: Fallback to pattern matching
            log.info("Using fallback pattern matching...");
            String patternResponse = chatbotResponses.getResponse(userMessage);
            if (patternResponse != null) {
                log.info("Pattern match found");
                return patternResponse;
            }

            // STEP 5: Default response
            log.info("No pattern match, using default response");
            return chatbotResponses.getDefaultResponse();

        } catch (Exception e) {
            log.error("Error generating bot response: {}", e.getMessage(), e);
            return chatbotResponses.getDefaultResponse();
        }
    }

    /**
     * Build comprehensive shop context for AI with real product data (RAG Retrieval
     * step)
     * This is the core of RAG - retrieving relevant data from database
     */
    private String buildShopContext(String userMessage) {
        StringBuilder context = new StringBuilder();

        log.info("Building shop context for message: {}", userMessage);

        // Shop info
        context.append("=== THÔNG TIN CỬA HÀNG ===\n");
        context.append("Tên: FlowerCorner - Cửa hàng hoa tươi chất lượng cao\n");
        context.append("Hotline: 1900 633 045 | 0865 160 360\n");
        context.append("Địa chỉ: TP.HCM và Hà Nội\n");
        context.append("Website: http://localhost:3000\n\n");

        // Add categories
        int categoryCount = 0;
        try {
            context.append("=== DANH MỤC SẢN PHẨM ===\n");
            List<Category> categories = categoryRepository.findAll();
            log.info("Found {} categories in database", categories.size());

            for (Category cat : categories) {
                if (Boolean.TRUE.equals(cat.getActive())) {
                    context.append("• ").append(cat.getName());
                    if (cat.getDescription() != null && !cat.getDescription().isEmpty()) {
                        context.append(": ").append(cat.getDescription());
                    }
                    context.append("\n");
                    categoryCount++;
                }
            }
            context.append("\n");
            log.info("Added {} active categories to context", categoryCount);
        } catch (Exception e) {
            log.error("Error loading categories: {}", e.getMessage(), e);
        }

        // Add all products with details - CRITICAL for RAG
        int productCount = 0;
        try {
            log.info("Loading products from database...");
            List<Product> allProducts = productRepository.findAll();
            log.info("Found {} total products in database", allProducts.size());

            List<Product> products = allProducts.stream()
                    .filter(p -> p.isActive())
                    .limit(50)
                    .collect(Collectors.toList());

            log.info("Filtered to {} active products", products.size());

            context.append("=== DANH SÁCH SẢN PHẨM HOA (Dữ liệu thực từ Database) ===\n");
            context.append("Tổng số: ").append(products.size()).append(" sản phẩm\n\n");

            for (Product p : products) {
                try {
                    BigDecimal price = p.getCurrentPrice();

                    // Build product info
                    context.append("📦 ").append(p.getName() != null ? p.getName().toUpperCase() : "NO NAME")
                            .append("\n");
                    context.append("   • ID: ").append(p.getId()).append("\n");
                    context.append("   • Giá: ").append(formatPrice(price));
                    if (p.isOnSale() && p.getSalePrice() != null) {
                        context.append(" (Giảm giá từ ").append(formatPrice(p.getPrice())).append(")");
                    }
                    context.append("\n");

                    if (p.getStockQuantity() != null) {
                        context.append("   • Tồn kho: ").append(p.getStockQuantity()).append(" sản phẩm");
                        context.append(p.getStockQuantity() > 0 ? " (Còn hàng)" : " (Hết hàng)");
                        context.append("\n");
                    }

                    if (p.getCategory() != null) {
                        context.append("   • Danh mục: ").append(p.getCategory().getName()).append("\n");
                    }

                    if (p.getDescription() != null && !p.getDescription().isEmpty()) {
                        String desc = p.getDescription();
                        if (desc.length() > 100)
                            desc = desc.substring(0, 100) + "...";
                        context.append("   • Mô tả: ").append(desc).append("\n");
                    }

                    if (p.getThumbnail() != null && !p.getThumbnail().isEmpty()) {
                        context.append("   • Hình ảnh: ").append(p.getThumbnail()).append("\n");
                    }

                    if (p.getSlug() != null) {
                        context.append("   • Link xem: /products/").append(p.getSlug()).append("\n");
                    }
                    context.append("\n");
                    productCount++;

                } catch (Exception e) {
                    log.warn("Error processing product {}: {}", p.getId(), e.getMessage());
                }
            }

            log.info("Added {} products to context", productCount);

        } catch (Exception e) {
            log.error("Error loading products: {}", e.getMessage(), e);
            context.append("(Đang tải dữ liệu sản phẩm...)\n\n");
        }

        // Policies
        context.append("=== CHÍNH SÁCH CỬA HÀNG ===\n");
        context.append("• Giao hàng nội thành: 2-4 tiếng, phí 15,000-30,000đ\n");
        context.append("• Miễn phí giao hàng cho đơn từ 500,000đ\n");
        context.append("• Đổi trả miễn phí trong 2 giờ nếu hoa hư hỏng\n");
        context.append("• Thanh toán: COD, MoMo, chuyển khoản ngân hàng\n\n");

        // Instructions for AI
        context.append("=== HƯỚNG DẪN TRẢ LỜI ===\n");
        context.append("• Khi khách hỏi về giá/sản phẩm: ĐỌC DANH SÁCH TRÊN và trích dẫn tên + giá chính xác\n");
        context.append("• Nếu khách có ngân sách (VD: 200.000đ): Tìm các sản phẩm có giá <= ngân sách\n");
        context.append("• Luôn đề xuất 2-3 sản phẩm cụ thể từ danh sách\n");
        context.append("• Luôn kèm link xem chi tiết: /products/{slug}\n\n");

        String result = context.toString();
        log.info("Context built successfully: {} chars, {} categories, {} products",
                result.length(), categoryCount, productCount);

        return result;
    }

    /**
     * Format price to Vietnamese format
     */
    private String formatPrice(BigDecimal price) {
        if (price == null)
            return "0đ";
        java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(java.util.Locale.of("vi", "VN"));
        return formatter.format(price) + "đ";
    }

    /**
     * Get messages for a session
     */
    public List<ChatMessageDTO> getMessagesForSession(Long sessionId) {
        log.info("Getting messages for session: {}", sessionId);

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        List<ChatMessage> messages = messageRepository.findTop50BySessionOrderBySentAtDesc(session);
        if (messages != null) {
            Collections.reverse(messages);
        } else {
            messages = new ArrayList<>();
        }

        return messages.stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());
    }

    /**
     * Admin sends message to user
     */
    @Transactional
    public ChatMessageDTO sendAdminMessage(Long sessionId, String content, User admin) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        // Update session status and assign staff
        if (!"WITH_STAFF".equals(session.getStatus())) {
            session.setStatus("WITH_STAFF");
            session.setStaff(admin);
        }
        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Save message
        ChatMessage message = ChatMessage.builder()
                .session(session)
                .senderType("ADMIN")
                .senderId(admin.getId())
                .senderName(admin.getFullName())
                .content(content)
                .messageType("TEXT")
                .isRead(false)
                .build();

        message = messageRepository.save(message);

        ChatMessageDTO responseDTO = mapToMessageDTO(message);

        // Broadcast to session
        broadcastToSession(sessionId, responseDTO);

        return responseDTO;
    }

    /**
     * Toggle chat mode between AI Bot and Manual Staff
     * 
     * @param sessionId Session ID
     * @param mode      "BOT" or "STAFF"
     * @param admin     Current admin user
     * @return New session status
     */
    @Transactional
    public String toggleChatMode(Long sessionId, String mode, User admin) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        String newStatus;
        String systemMessage;

        if ("BOT".equalsIgnoreCase(mode)) {
            // Switch to BOT mode
            newStatus = "ACTIVE";
            session.setStatus(newStatus);
            session.setStaff(null); // Remove staff assignment
            systemMessage = "🤖 Chế độ AI đã được bật. Bot sẽ tự động trả lời.";
        } else {
            // Switch to STAFF mode
            newStatus = "WITH_STAFF";
            session.setStatus(newStatus);
            session.setStaff(admin); // Assign current admin
            systemMessage = "👨‍💼 Nhân viên " + admin.getFullName() + " đang hỗ trợ bạn.";
        }

        session.setUpdatedAt(LocalDateTime.now());
        sessionRepository.save(session);

        // Send system message to notify user
        ChatMessage sysMessage = ChatMessage.builder()
                .session(session)
                .senderType("SYSTEM")
                .senderName("Hệ thống")
                .content(systemMessage)
                .messageType("TEXT")
                .isRead(false)
                .build();

        sysMessage = messageRepository.save(sysMessage);

        // Broadcast system message
        ChatMessageDTO sysMessageDTO = mapToMessageDTO(sysMessage);
        broadcastToSession(sessionId, sysMessageDTO);

        log.info("Chat mode toggled for session {} to {} by admin {}",
                sessionId, newStatus, admin.getUsername());

        return newStatus;
    }

    // ==================== Online Status ====================

    /**
     * User connects (goes online)
     */
    public void userConnected(Long userId, String guestId, String displayName, Long sessionId) {
        String key = userId != null ? "user_" + userId : "guest_" + guestId;

        OnlineStatusDTO status = OnlineStatusDTO.builder()
                .userId(userId)
                .guestId(guestId)
                .userType("USER")
                .displayName(displayName)
                .online(true)
                .sessionId(sessionId)
                .lastSeen(LocalDateTime.now())
                .build();

        onlineUsers.put(key, status);

        log.info("User connected: {}", key);

        // Notify admins
        messagingTemplate.convertAndSend("/topic/admin/online-status", status);
    }

    /**
     * User disconnects (goes offline)
     */
    public void userDisconnected(Long userId, String guestId) {
        String key = userId != null ? "user_" + userId : "guest_" + guestId;

        OnlineStatusDTO status = onlineUsers.remove(key);
        if (status != null) {
            status.setOnline(false);
            status.setLastSeen(LocalDateTime.now());

            log.info("User disconnected: {}", key);

            // Notify admins
            messagingTemplate.convertAndSend("/topic/admin/online-status", status);
        }
    }

    /**
     * Admin connects
     */
    public void adminConnected(User admin) {
        OnlineStatusDTO status = OnlineStatusDTO.builder()
                .userId(admin.getId())
                .userType("ADMIN")
                .displayName(admin.getFullName())
                .online(true)
                .lastSeen(LocalDateTime.now())
                .build();

        onlineAdmins.put(admin.getId(), status);

        log.info("Admin connected: {}", admin.getUsername());

        // Notify all users about admin availability
        messagingTemplate.convertAndSend("/topic/admin-status", status);
    }

    /**
     * Admin disconnects
     */
    public void adminDisconnected(Long adminId) {
        OnlineStatusDTO status = onlineAdmins.remove(adminId);
        if (status != null) {
            status.setOnline(false);
            status.setLastSeen(LocalDateTime.now());

            log.info("Admin disconnected: {}", adminId);

            // Notify users
            messagingTemplate.convertAndSend("/topic/admin-status", status);
        }
    }

    /**
     * Check if any admin is online
     */
    public boolean isAnyAdminOnline() {
        return !onlineAdmins.isEmpty();
    }

    /**
     * Get list of online admins
     */
    public List<OnlineStatusDTO> getOnlineAdmins() {
        return new ArrayList<>(onlineAdmins.values());
    }

    /**
     * Get online users for admin dashboard
     */
    public List<OnlineStatusDTO> getOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    // ==================== Typing Indicator ====================

    /**
     * Broadcast typing indicator
     */
    public void sendTypingIndicator(TypingIndicatorDTO indicator) {
        messagingTemplate.convertAndSend(
                "/topic/chat/" + indicator.getSessionId() + "/typing",
                indicator);
    }

    /**
     * Get session details by ID with all messages
     */
    public ChatSessionDTO getSessionDetails(Long sessionId) {
        log.info("Getting session details for: {}", sessionId);

        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        return mapToSessionDTOWithLastMessage(session);
    }

    // ==================== Session Management ====================

    /**
     * Get active sessions for admin (waiting or with staff)
     */
    public List<ChatSessionDTO> getActiveSessionsForAdmin() {
        log.info("===== ADMIN SESSIONS LOADING =====");

        List<ChatSessionDTO> result = new ArrayList<>();

        try {
            // Get ALL sessions from database
            List<ChatSession> allSessions = sessionRepository.findAll();
            log.info("Total sessions in DB: {}", allSessions.size());

            if (allSessions.isEmpty()) {
                log.info("No sessions found in database");
                return result;
            }

            // Filter and map sessions
            for (ChatSession s : allSessions) {
                try {
                    String status = s.getStatus();
                    log.info("Processing session {} - status: '{}', userId: {}, guestId: {}",
                            s.getId(), status,
                            s.getUser() != null ? s.getUser().getId() : null,
                            s.getGuestId());

                    if (status == null) {
                        log.info("EXCLUDED session {} - status is null", s.getId());
                        continue;
                    }

                    String trimmedStatus = status.trim().toUpperCase();
                    boolean matchesFilter = "ACTIVE".equals(trimmedStatus)
                            || "WAITING_STAFF".equals(trimmedStatus)
                            || "WITH_STAFF".equals(trimmedStatus);

                    if (!matchesFilter) {
                        log.info("EXCLUDED session {} - status '{}' doesn't match", s.getId(), trimmedStatus);
                        continue;
                    }

                    log.info("INCLUDED session {} - creating DTO...", s.getId());

                    // Use simple DTO creation to avoid any lazy loading issues
                    ChatSessionDTO dto = createSimpleSessionDTO(s);
                    if (dto != null) {
                        result.add(dto);
                        log.info("SUCCESS: Added session {} to result", s.getId());
                    } else {
                        log.warn("DTO was null for session {}", s.getId());
                    }

                } catch (Exception ex) {
                    log.error("Error processing session {}: {}", s.getId(), ex.getMessage(), ex);
                }
            }

            log.info("Returning {} session DTOs to admin", result.size());

        } catch (Exception e) {
            log.error("CRITICAL Error in getActiveSessionsForAdmin: {}", e.getMessage(), e);
        }

        log.info("===== END ADMIN SESSIONS LOADING =====");
        return result;
    }

    /**
     * Create a simple session DTO - avoids lazy loading issues
     */
    private ChatSessionDTO createSimpleSessionDTO(ChatSession session) {
        log.info("createSimpleSessionDTO for session: {}", session.getId());

        // Get user info safely
        Long userId = null;
        String userName = "Khách";
        try {
            if (session.getUser() != null) {
                userId = session.getUser().getId();
                userName = session.getUser().getFullName();
                if (userName == null || userName.isEmpty()) {
                    userName = session.getUser().getUsername();
                }
            } else if (session.getGuestId() != null) {
                String guestId = session.getGuestId();
                userName = "Khách #" + (guestId.length() >= 8 ? guestId.substring(0, 8) : guestId);
            }
        } catch (Exception e) {
            log.warn("Error getting user info for session {}: {}", session.getId(), e.getMessage());
        }

        // Get messages safely
        List<ChatMessageDTO> messageDTOs = new ArrayList<>();
        long unreadCount = 0;
        try {
            List<ChatMessage> messages = messageRepository.findTop50BySessionOrderBySentAtDesc(session);
            if (messages != null && !messages.isEmpty()) {
                Collections.reverse(messages);
                for (ChatMessage m : messages) {
                    try {
                        messageDTOs.add(ChatMessageDTO.builder()
                                .id(m.getId())
                                .sessionId(session.getId())
                                .senderType(m.getSenderType())
                                .senderId(m.getSenderId())
                                .senderName(m.getSenderName())
                                .content(m.getContent())
                                .messageType(m.getMessageType())
                                .metadata(m.getMetadata())
                                .sentAt(m.getSentAt())
                                .isRead(m.getIsRead())
                                .build());

                        // Count unread from users
                        if (!m.getIsRead() && "USER".equals(m.getSenderType())) {
                            unreadCount++;
                        }
                    } catch (Exception e) {
                        log.warn("Error mapping message {}: {}", m.getId(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error getting messages for session {}: {}", session.getId(), e.getMessage());
        }

        // Get staff info safely
        Long staffId = null;
        String staffName = null;
        try {
            if (session.getStaff() != null) {
                staffId = session.getStaff().getId();
                staffName = session.getStaff().getFullName();
            }
        } catch (Exception e) {
            log.warn("Error getting staff info for session {}: {}", session.getId(), e.getMessage());
        }

        // Check online status
        String guestId = session.getGuestId();
        String onlineKey = userId != null ? "user_" + userId : (guestId != null ? "guest_" + guestId : "guest_unknown");
        boolean isUserOnline = onlineUsers.containsKey(onlineKey);

        String title = session.getTitle();
        if (title == null || title.isEmpty()) {
            title = "Cuộc trò chuyện";
        }

        return ChatSessionDTO.builder()
                .id(session.getId())
                .userId(userId)
                .guestId(session.getGuestId())
                .status(session.getStatus() != null ? session.getStatus() : "ACTIVE")
                .title(title)
                .staffId(staffId)
                .staffName(staffName)
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .updatedAt(session.getUpdatedAt())
                .messages(messageDTOs)
                .unreadCount(unreadCount)
                .isUserOnline(isUserOnline)
                .userName(userName)
                .build();
    }

    /**
     * Create a basic session DTO when full mapping fails
     */
    private ChatSessionDTO createBasicSessionDTO(ChatSession session) {
        String guestId = session.getGuestId();
        String userName = session.getUser() != null ? session.getUser().getFullName()
                : "Khách #" + (guestId != null && guestId.length() >= 8 ? guestId.substring(0, 8) : "unknown");

        return ChatSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .guestId(session.getGuestId())
                .status(session.getStatus())
                .title(session.getTitle() != null ? session.getTitle() : "Cuộc trò chuyện")
                .staffId(session.getStaff() != null ? session.getStaff().getId() : null)
                .staffName(session.getStaff() != null ? session.getStaff().getFullName() : null)
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .updatedAt(session.getUpdatedAt())
                .messages(new ArrayList<>())
                .unreadCount(0L)
                .isUserOnline(false)
                .userName(userName)
                .build();
    }

    /**
     * Get sessions waiting for staff
     */
    public List<ChatSessionDTO> getWaitingSessionsForAdmin() {
        try {
            return sessionRepository.findByStatusOrderByUpdatedAtDesc("WAITING_STAFF")
                    .stream()
                    .map(this::mapToSessionDTOWithLastMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting waiting sessions: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Mark messages as read
     */
    @Transactional
    public void markSessionAsRead(Long sessionId, String readerType) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // Mark opposite sender's messages as read
        String targetSenderType = "ADMIN".equals(readerType) ? "USER" : "ADMIN";
        messageRepository.markAsRead(session, targetSenderType);

        log.info("Marked messages as read - session: {}, reader: {}", sessionId, readerType);
    }

    /**
     * Get unread count for user
     */
    public long getUnreadCountForUser(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId).orElse(null);
        if (session == null)
            return 0;

        return messageRepository.countBySessionAndIsReadFalseAndSenderTypeNot(session, "USER");
    }

    /**
     * Get unread count for admin (all sessions)
     */
    public long getTotalUnreadForAdmin() {
        return sessionRepository.findByStatusInOrderByUpdatedAtDesc(
                Arrays.asList("ACTIVE", "WAITING_STAFF", "WITH_STAFF")).stream()
                .mapToLong(session -> messageRepository.countBySessionAndIsReadFalseAndSenderTypeNot(session, "ADMIN"))
                .sum();
    }

    // ==================== Private Helpers ====================

    private ChatSession getOrCreateSession(WebSocketMessageDTO messageDTO, User sender) {
        if (messageDTO.getSessionId() != null) {
            return sessionRepository.findById(messageDTO.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
        }

        // Find existing active session
        Optional<ChatSession> existingSession;
        if (sender != null) {
            existingSession = sessionRepository.findFirstByUserAndStatusInOrderByStartedAtDesc(
                    sender, Arrays.asList("ACTIVE", "WAITING_STAFF", "WITH_STAFF"));
        } else {
            existingSession = sessionRepository.findFirstByGuestIdAndStatusInOrderByStartedAtDesc(
                    messageDTO.getGuestId(), Arrays.asList("ACTIVE", "WAITING_STAFF", "WITH_STAFF"));
        }

        return existingSession.orElseGet(() -> {
            ChatSession newSession = ChatSession.builder()
                    .user(sender)
                    .guestId(sender == null ? messageDTO.getGuestId() : null)
                    .status("ACTIVE")
                    .title("Cuộc trò chuyện mới")
                    .build();
            ChatSession savedSession = sessionRepository.save(newSession);

            // Notify admins about new session
            notifyAdminsNewSession(savedSession);

            return savedSession;
        });
    }

    /**
     * Notify admins about new chat session via WebSocket
     */
    private void notifyAdminsNewSession(ChatSession session) {
        try {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "NEW_SESSION");
            notification.put("sessionId", session.getId());

            // Create a basic session DTO
            ChatSessionDTO sessionDTO = createBasicSessionDTO(session);
            notification.put("session", sessionDTO);

            String guestId = session.getGuestId();
            String userName = session.getUser() != null ? session.getUser().getFullName()
                    : "Khách #" + (guestId != null && guestId.length() >= 8 ? guestId.substring(0, 8) : "unknown");
            notification.put("userName", userName);
            notification.put("timestamp", LocalDateTime.now().toString());

            messagingTemplate.convertAndSend("/topic/admin/notifications", notification);
            log.info("Notified admins about new WebSocket session: {}", session.getId());
        } catch (Exception e) {
            log.error("Error notifying admins about new session: {}", e.getMessage());
        }
    }

    private void broadcastToSession(Long sessionId, ChatMessageDTO message) {
        messagingTemplate.convertAndSend("/topic/chat/" + sessionId, message);
    }

    private void notifyAdminsNewMessage(ChatSession session, ChatMessageDTO message) {
        // Create notification for admin dashboard
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "NEW_MESSAGE");
        notification.put("sessionId", session.getId());
        notification.put("message", message);

        String guestId = session.getGuestId();
        String userName = session.getUser() != null ? session.getUser().getFullName()
                : "Khách #" + (guestId != null && guestId.length() >= 8 ? guestId.substring(0, 8) : "unknown");
        notification.put("userName", userName);
        notification.put("timestamp", LocalDateTime.now().toString());

        messagingTemplate.convertAndSend("/topic/admin/notifications", notification);
    }

    private ChatMessageDTO mapToMessageDTO(ChatMessage message) {
        return ChatMessageDTO.builder()
                .id(message.getId())
                .sessionId(message.getSession().getId())
                .senderType(message.getSenderType())
                .senderId(message.getSenderId())
                .senderName(message.getSenderName())
                .content(message.getContent())
                .messageType(message.getMessageType())
                .metadata(message.getMetadata())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .build();
    }

    private ChatSessionDTO mapToSessionDTOWithLastMessage(ChatSession session) {
        List<ChatMessage> messages = messageRepository.findTop50BySessionOrderBySentAtDesc(session);
        if (messages == null) {
            messages = new ArrayList<>();
        } else {
            Collections.reverse(messages);
        }

        long unreadCount = 0;
        try {
            unreadCount = messageRepository.countBySessionAndIsReadFalseAndSenderTypeNot(session, "ADMIN");
        } catch (Exception e) {
            log.warn("Error counting unread messages for session {}: {}", session.getId(), e.getMessage());
        }

        // Check if user is online
        String guestId = session.getGuestId();
        String onlineKey = session.getUser() != null ? "user_" + session.getUser().getId()
                : (guestId != null ? "guest_" + guestId : "guest_unknown");
        boolean isUserOnline = onlineUsers.containsKey(onlineKey);

        String userName = session.getUser() != null ? session.getUser().getFullName()
                : "Khách #" + (guestId != null && guestId.length() >= 8 ? guestId.substring(0, 8) : "unknown");

        String title = session.getTitle();
        if (title == null || title.isEmpty()) {
            title = "Cuộc trò chuyện";
        }

        return ChatSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .guestId(session.getGuestId())
                .status(session.getStatus() != null ? session.getStatus() : "ACTIVE")
                .title(title)
                .staffId(session.getStaff() != null ? session.getStaff().getId() : null)
                .staffName(session.getStaff() != null ? session.getStaff().getFullName() : null)
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .updatedAt(session.getUpdatedAt())
                .messages(messages.stream().map(this::mapToMessageDTO).collect(Collectors.toList()))
                .unreadCount(unreadCount)
                .isUserOnline(isUserOnline)
                .userName(userName)
                .build();
    }
}
