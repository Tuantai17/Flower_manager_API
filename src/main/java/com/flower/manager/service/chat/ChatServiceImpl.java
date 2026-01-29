package com.flower.manager.service.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.manager.dto.chat.*;
import com.flower.manager.entity.*;
import com.flower.manager.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of ChatService
 * Handles chat sessions, messages, and AI integration
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final GeminiService geminiService;
    private final ChatbotResponses chatbotResponses;
    private final ObjectMapper objectMapper;

    private static final List<String> ACTIVE_STATUSES = Arrays.asList("ACTIVE", "WAITING_STAFF", "WITH_STAFF");
    private static final NumberFormat currencyFormat = NumberFormat.getInstance(Locale.of("vi", "VN"));

    // ==================== Session Management ====================

    @Override
    @Transactional
    public ChatSessionDTO createSession(User user, String guestId) {
        ChatSession session = ChatSession.builder()
                .user(user)
                .guestId(user == null ? guestId : null)
                .status("ACTIVE")
                .title("Cuộc trò chuyện mới")
                .build();

        session = sessionRepository.save(session);

        // Add welcome message
        ChatMessage welcomeMessage = ChatMessage.builder()
                .session(session)
                .senderType("BOT")
                .senderName("Trợ lý Flower Shop")
                .content(chatbotResponses.getWelcomeMessage())
                .messageType("TEXT")
                .build();

        messageRepository.save(welcomeMessage);

        log.info("Created new chat session: {} for user: {}", session.getId(),
                user != null ? user.getUsername() : "guest-" + guestId);

        return mapToSessionDTO(session, Collections.singletonList(welcomeMessage));
    }

    @Override
    @Transactional
    public ChatSessionDTO getOrCreateSession(User user, String guestId) {
        Optional<ChatSession> existingSession;

        if (user != null) {
            existingSession = sessionRepository.findFirstByUserAndStatusInOrderByStartedAtDesc(user, ACTIVE_STATUSES);
        } else if (guestId != null && !guestId.isEmpty()) {
            existingSession = sessionRepository.findFirstByGuestIdAndStatusInOrderByStartedAtDesc(guestId,
                    ACTIVE_STATUSES);
        } else {
            // Generate new guest ID
            guestId = UUID.randomUUID().toString();
            existingSession = Optional.empty();
        }

        if (existingSession.isPresent()) {
            ChatSession session = existingSession.get();
            List<ChatMessage> messages = messageRepository.findBySessionOrderBySentAtAsc(session);
            return mapToSessionDTO(session, messages);
        }

        return createSession(user, guestId);
    }

    @Override
    public ChatSessionDTO getSession(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        List<ChatMessage> messages = messageRepository.findBySessionOrderBySentAtAsc(session);
        return mapToSessionDTO(session, messages);
    }

    @Override
    public List<ChatMessageDTO> getMessages(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        return messageRepository.findBySessionOrderBySentAtAsc(session)
                .stream()
                .map(this::mapToMessageDTO)
                .collect(Collectors.toList());
    }

    // ==================== Message Handling ====================

    @Override
    @Transactional
    public ChatResponse sendMessage(SendMessageRequest request, User user) {
        // Get or create session
        ChatSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
        } else {
            ChatSessionDTO sessionDTO = getOrCreateSession(user, request.getGuestId());
            session = sessionRepository.findById(sessionDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Session not found"));
        }

        // Save user message
        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .senderType("USER")
                .senderId(user != null ? user.getId() : null)
                .senderName(user != null ? user.getFullName() : "Khách")
                .content(request.getContent())
                .messageType("TEXT")
                .build();

        userMessage = messageRepository.save(userMessage);

        // Generate bot response
        String botResponseText = generateBotResponse(request.getContent(), session);

        // Detect product suggestions
        List<ChatResponse.ProductSuggestion> productSuggestions = detectAndSuggestProducts(request.getContent());

        // Save bot message
        ChatMessage botMessage = ChatMessage.builder()
                .session(session)
                .senderType("BOT")
                .senderName("Trợ lý Flower Shop")
                .content(botResponseText)
                .messageType(productSuggestions.isEmpty() ? "TEXT" : "PRODUCT")
                .metadata(productSuggestions.isEmpty() ? null : toJson(productSuggestions))
                .build();

        botMessage = messageRepository.save(botMessage);

        // Update session title from first user message
        if (session.getTitle().equals("Cuộc trò chuyện mới")) {
            String title = request.getContent().length() > 50
                    ? request.getContent().substring(0, 47) + "..."
                    : request.getContent();
            session.setTitle(title);
            sessionRepository.save(session);
        }

        return ChatResponse.builder()
                .sessionId(session.getId())
                .userMessage(mapToMessageDTO(userMessage))
                .botMessage(mapToMessageDTO(botMessage))
                .quickReplies(chatbotResponses.getQuickReplies())
                .productSuggestions(productSuggestions)
                .needStaffSupport(false)
                .build();
    }

    /**
     * Generate bot response using Gemini or fallback to pattern matching
     */
    private String generateBotResponse(String userMessage, ChatSession session) {
        // Build context with product/category info - PASS USER MESSAGE for smart
        // filtering
        String context = buildShopContext(userMessage);

        log.info("=== CHATBOT DEBUG ===");
        log.info("User Message: {}", userMessage);
        log.info("Context Length: {} chars", context.length());
        log.info("Context Preview: {}", context.substring(0, Math.min(500, context.length())));

        // Build conversation history
        List<ChatMessage> recentMessages = messageRepository.findRecentMessages(session);
        List<String> history = new ArrayList<>();
        for (ChatMessage msg : recentMessages) {
            String prefix = msg.isFromUser() ? "Khách hàng: " : "Trợ lý: ";
            history.add(prefix + msg.getContent());
        }
        Collections.reverse(history);

        // Try Gemini first
        if (geminiService.isConfigured()) {
            log.info("Calling Gemini API...");
            String geminiResponse = geminiService.generateResponse(userMessage, context, history);
            if (geminiResponse != null && !geminiResponse.isEmpty()) {
                log.info("Gemini Response: {}", geminiResponse.substring(0, Math.min(200, geminiResponse.length())));
                return geminiResponse;
            }
            log.warn("Gemini returned null or empty response, falling back to pattern matching");
        } else {
            log.warn("Gemini not configured, using fallback");
        }

        // Fallback to pattern matching
        String patternResponse = chatbotResponses.getResponse(userMessage);
        if (patternResponse != null) {
            return patternResponse;
        }

        return chatbotResponses.getDefaultResponse();
    }

    /**
     * Build shop context for AI with real product data
     * Now filters products based on user's question for better relevance
     */
    private String buildShopContext(String userMessage) {
        StringBuilder context = new StringBuilder();
        String lowerMessage = userMessage != null ? userMessage.toLowerCase() : "";

        // Shop info
        context.append("Thông tin cửa hàng: FlowerCorner - Cửa hàng hoa tươi chất lượng cao.\n");
        context.append("Hotline: 1900 633 045 | 0865 160 360\n");
        context.append("Địa chỉ: TP.HCM và Hà Nội\n\n");

        // Add categories with descriptions
        context.append("=== DANH MỤC HOA ===\n");
        categoryRepository.findAll().stream()
                .filter(cat -> Boolean.TRUE.equals(cat.getActive()))
                .forEach(cat -> {
                    context.append("• ").append(cat.getName());
                    if (cat.getDescription() != null && !cat.getDescription().isEmpty()) {
                        context.append(": ").append(cat.getDescription());
                    }
                    context.append("\n");
                });
        context.append("\n");

        // Extract keywords from user message for smart filtering
        List<String> keywords = extractProductKeywords(lowerMessage);
        log.info("Extracted keywords from message: {}", keywords);

        // Find matching products based on keywords
        List<Product> matchingProducts = new ArrayList<>();
        List<Product> allActiveProducts = productRepository.findAll().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());

        // If user asks about specific products, find them
        if (!keywords.isEmpty()) {
            for (Product p : allActiveProducts) {
                String productNameLower = p.getName().toLowerCase();
                String categoryNameLower = p.getCategory() != null ? p.getCategory().getName().toLowerCase() : "";

                for (String keyword : keywords) {
                    if (productNameLower.contains(keyword) || categoryNameLower.contains(keyword)) {
                        if (!matchingProducts.contains(p)) {
                            matchingProducts.add(p);
                        }
                        break;
                    }
                }
            }
        }

        // If no matches found, include all products (important for general questions)
        List<Product> productsToInclude;
        if (matchingProducts.isEmpty()) {
            log.info("No matching products found, including ALL active products");
            productsToInclude = allActiveProducts;
        } else {
            log.info("Found {} matching products based on keywords", matchingProducts.size());
            productsToInclude = matchingProducts;
            // Also add some additional products for variety
            for (Product p : allActiveProducts) {
                if (!matchingProducts.contains(p) && productsToInclude.size() < 30) {
                    productsToInclude.add(p);
                }
            }
        }

        // Add products with full details
        context.append("=== DANH SÁCH SẢN PHẨM HOA (Giá chính xác từ database) ===\n");
        context.append("Tổng số sản phẩm: ").append(productsToInclude.size()).append("\n\n");

        for (Product p : productsToInclude) {
            context.append("• ").append(p.getName());
            context.append(" - GIÁ: ").append(formatPrice(p.getCurrentPrice()));
            if (p.isOnSale() && p.getSalePrice() != null) {
                context.append(" (Giảm từ ").append(formatPrice(p.getPrice())).append(")");
            }
            if (p.getStockQuantity() != null && p.getStockQuantity() > 0) {
                context.append(" | Còn hàng");
            } else {
                context.append(" | Hết hàng");
            }
            if (p.getCategory() != null) {
                context.append(" | Danh mục: ").append(p.getCategory().getName());
            }
            context.append("\n");
        }
        context.append("\n");

        // Add policies
        context.append("=== CHÍNH SÁCH CỬA HÀNG ===\n");
        context.append("• Giao hàng nội thành: 2-4 tiếng, phí 15,000-30,000đ\n");
        context.append("• Miễn phí giao hàng đơn từ 500,000đ\n");
        context.append("• Đổi trả trong 2 giờ nếu hoa hư hỏng\n");
        context.append("• Thanh toán: COD, MoMo, chuyển khoản\n\n");

        return context.toString();
    }

    /**
     * Extract product-related keywords from user message
     */
    private List<String> extractProductKeywords(String message) {
        List<String> keywords = new ArrayList<>();

        // Common flower keywords
        String[] flowerKeywords = {
                "hồng", "hướng dương", "lan", "ly", "cúc", "tulip", "baby",
                "lavender", "sen", "đồng tiền", "thược dược", "cẩm tú cầu",
                "sinh nhật", "khai trương", "chia buồn", "cưới", "valentine",
                "chúc mừng", "đám tang", "kỷ niệm", "tình yêu", "8/3", "20/10",
                "bó hoa", "giỏ hoa", "hộp hoa", "lẵng hoa", "kệ hoa"
        };

        for (String keyword : flowerKeywords) {
            if (message.contains(keyword)) {
                keywords.add(keyword);
            }
        }

        return keywords;
    }

    /**
     * Detect product-related keywords and suggest products
     * Enhanced to handle price queries and category-based searches
     * FIXED: Prioritize category matching over generic keywords
     */
    private List<ChatResponse.ProductSuggestion> detectAndSuggestProducts(String message) {
        List<ChatResponse.ProductSuggestion> suggestions = new ArrayList<>();
        String lowerMessage = message.toLowerCase();

        // Check for price-based queries
        BigDecimal maxPrice = extractMaxPrice(lowerMessage);
        BigDecimal minPrice = extractMinPrice(lowerMessage);

        // Category keywords - PRIORITIZED
        Map<String, List<String>> categoryKeywords = new java.util.LinkedHashMap<>();
        categoryKeywords.put("sinh nhật", Arrays.asList("sinh nhật", "birthday", "tặng người yêu", "lãng mạn"));
        categoryKeywords.put("khai trương", Arrays.asList("khai trương", "chúc mừng", "kệ hoa"));
        categoryKeywords.put("chia buồn", Arrays.asList("chia buồn", "tang", "phúng điếu"));
        categoryKeywords.put("cây cảnh", Arrays.asList("chậu", "cây", "mini", "cây cảnh"));
        categoryKeywords.put("hoa hồng", Arrays.asList("hồng", "rose", "hoa hồng"));

        // Find which category user is asking about
        String matchedCategory = null;
        for (Map.Entry<String, List<String>> entry : categoryKeywords.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (lowerMessage.contains(keyword)) {
                    matchedCategory = entry.getKey();
                    break;
                }
            }
            if (matchedCategory != null)
                break;
        }

        // Generic flower keywords
        List<String> genericKeywords = Arrays.asList(
                "hồng", "hướng dương", "lan", "ly", "cúc", "tulip", "baby", "hoa",
                "bó", "giỏ", "hộp", "lẵng", "valentine", "ngày lễ");

        boolean hasProductKeyword = genericKeywords.stream().anyMatch(lowerMessage::contains);
        boolean hasPriceQuery = maxPrice != null || minPrice != null;
        boolean hasCategoryQuery = matchedCategory != null;

        if (hasCategoryQuery || hasProductKeyword || hasPriceQuery) {
            final String finalMatchedCategory = matchedCategory;

            // Find matching products - PRIORITIZE CATEGORY MATCH
            List<Product> matchingProducts = productRepository.findAll().stream()
                    .filter(Product::isActive)
                    .filter(p -> {
                        // Price filter
                        if (maxPrice != null && p.getCurrentPrice().compareTo(maxPrice) > 0) {
                            return false;
                        }
                        if (minPrice != null && p.getCurrentPrice().compareTo(minPrice) < 0) {
                            return false;
                        }

                        String productName = p.getName().toLowerCase();
                        String categoryName = p.getCategory() != null
                                ? p.getCategory().getName().toLowerCase()
                                : "";

                        // If user asked for specific category, ONLY show products from that category
                        if (finalMatchedCategory != null) {
                            return categoryName.contains(finalMatchedCategory) ||
                                    categoryKeywords.get(finalMatchedCategory).stream()
                                            .anyMatch(k -> productName.contains(k) || categoryName.contains(k));
                        }

                        // Otherwise use generic keyword matching
                        if (hasProductKeyword) {
                            return genericKeywords.stream()
                                    .anyMatch(k -> productName.contains(k) || categoryName.contains(k));
                        }

                        return true;
                    })
                    .sorted((a, b) -> a.getCurrentPrice().compareTo(b.getCurrentPrice()))
                    .limit(4)
                    .collect(Collectors.toList());

            for (Product p : matchingProducts) {
                suggestions.add(ChatResponse.ProductSuggestion.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .slug(p.getSlug())
                        .thumbnail(p.getThumbnail())
                        .price(formatPrice(p.getPrice()))
                        .salePrice(p.getSalePrice() != null ? formatPrice(p.getSalePrice()) : null)
                        .build());
            }

            log.info("Product suggestions for '{}': {} products found (category={}, maxPrice={}, minPrice={})",
                    message, suggestions.size(), matchedCategory, maxPrice, minPrice);
        }

        return suggestions;
    }

    /**
     * Extract max price from message like "dưới 200000" or "tối đa 500k"
     */
    private BigDecimal extractMaxPrice(String message) {
        // Pattern: dưới/under + số | tối đa + số | < số | số trở xuống
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?:dưới|under|tối đa|\\<)\\s*(\\d+(?:[.,]\\d+)?)(k|nghìn|ngàn)?|" +
                        "(\\d+(?:[.,]\\d+)?)(k|nghìn|ngàn)?\\s*(?:trở xuống|đổ lại)");
        java.util.regex.Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String numStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
            String unit = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);

            if (numStr != null) {
                numStr = numStr.replace(",", ".");
                BigDecimal value = new BigDecimal(numStr);
                if (unit != null && unit.matches("k|nghìn|ngàn")) {
                    value = value.multiply(new BigDecimal(1000));
                }
                return value;
            }
        }

        // Check for "giá rẻ" = under 300k
        if (message.contains("giá rẻ") || message.contains("rẻ")) {
            return new BigDecimal("300000");
        }

        return null;
    }

    /**
     * Extract min price from message like "từ 200000" or "trên 500k"
     */
    private BigDecimal extractMinPrice(String message) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(
                "(?:từ|trên|over|tối thiểu|\\>)\\s*(\\d+(?:[.,]\\d+)?)(k|nghìn|ngàn)?|" +
                        "(\\d+(?:[.,]\\d+)?)(k|nghìn|ngàn)?\\s*(?:trở lên)");
        java.util.regex.Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            String numStr = matcher.group(1) != null ? matcher.group(1) : matcher.group(3);
            String unit = matcher.group(2) != null ? matcher.group(2) : matcher.group(4);

            if (numStr != null) {
                numStr = numStr.replace(",", ".");
                BigDecimal value = new BigDecimal(numStr);
                if (unit != null && unit.matches("k|nghìn|ngàn")) {
                    value = value.multiply(new BigDecimal(1000));
                }
                return value;
            }
        }

        return null;
    }

    // ==================== Staff Support ====================

    @Override
    @Transactional
    public ChatSessionDTO requestStaffSupport(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setStatus("WAITING_STAFF");
        session = sessionRepository.save(session);

        // Add system message
        ChatMessage systemMessage = ChatMessage.builder()
                .session(session)
                .senderType("SYSTEM")
                .content(chatbotResponses.getStaffRequestMessage())
                .messageType("TEXT")
                .build();

        messageRepository.save(systemMessage);

        log.info("Staff support requested for session: {}", sessionId);

        List<ChatMessage> messages = messageRepository.findBySessionOrderBySentAtAsc(session);
        return mapToSessionDTO(session, messages);
    }

    @Override
    @Transactional
    public void closeSession(Long sessionId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.close();
        sessionRepository.save(session);

        log.info("Session closed: {}", sessionId);
    }

    @Override
    public List<ChatSessionDTO> getUserChatHistory(User user) {
        return sessionRepository.findByUserOrderByStartedAtDesc(user)
                .stream()
                .map(s -> mapToSessionDTO(s, null))
                .collect(Collectors.toList());
    }

    // ==================== Admin Methods ====================

    @Override
    public List<ChatSessionDTO> getSessionsWaitingForStaff() {
        return sessionRepository.findByStatusOrderByUpdatedAtDesc("WAITING_STAFF")
                .stream()
                .map(s -> {
                    List<ChatMessage> messages = messageRepository.findTop50BySessionOrderBySentAtDesc(s);
                    Collections.reverse(messages);
                    return mapToSessionDTO(s, messages);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatSessionDTO assignStaffToSession(Long sessionId, User staff) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setStaff(staff);
        session.setStatus("WITH_STAFF");
        session = sessionRepository.save(session);

        // Add system message
        ChatMessage systemMessage = ChatMessage.builder()
                .session(session)
                .senderType("SYSTEM")
                .content("Nhân viên " + staff.getFullName() + " đã tham gia hỗ trợ bạn.")
                .messageType("TEXT")
                .build();

        messageRepository.save(systemMessage);

        log.info("Staff {} assigned to session {}", staff.getUsername(), sessionId);

        List<ChatMessage> messages = messageRepository.findBySessionOrderBySentAtAsc(session);
        return mapToSessionDTO(session, messages);
    }

    @Override
    @Transactional
    public ChatMessageDTO sendStaffMessage(Long sessionId, String content, User staff) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        ChatMessage message = ChatMessage.builder()
                .session(session)
                .senderType("STAFF")
                .senderId(staff.getId())
                .senderName(staff.getFullName())
                .content(content)
                .messageType("TEXT")
                .build();

        message = messageRepository.save(message);
        return mapToMessageDTO(message);
    }

    // ==================== Mapping Helpers ====================

    private ChatSessionDTO mapToSessionDTO(ChatSession session, List<ChatMessage> messages) {
        Long unreadCount = 0L;
        if (messages != null && !messages.isEmpty()) {
            unreadCount = messages.stream()
                    .filter(m -> !m.getIsRead() && !"USER".equals(m.getSenderType()))
                    .count();
        }

        return ChatSessionDTO.builder()
                .id(session.getId())
                .userId(session.getUser() != null ? session.getUser().getId() : null)
                .guestId(session.getGuestId())
                .status(session.getStatus())
                .title(session.getTitle())
                .staffId(session.getStaff() != null ? session.getStaff().getId() : null)
                .staffName(session.getStaff() != null ? session.getStaff().getFullName() : null)
                .startedAt(session.getStartedAt())
                .endedAt(session.getEndedAt())
                .updatedAt(session.getUpdatedAt())
                .messages(messages != null ? messages.stream().map(this::mapToMessageDTO).collect(Collectors.toList())
                        : null)
                .unreadCount(unreadCount)
                .build();
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

    private String formatPrice(BigDecimal price) {
        if (price == null)
            return "0đ";
        return currencyFormat.format(price) + "đ";
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }
}
