package com.flower.manager.controller;

import com.flower.manager.dto.chat.*;
import com.flower.manager.entity.User;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.security.JwtUtils;
import com.flower.manager.service.chat.LiveChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket Controller for Live Chat
 * Handles real-time messaging via STOMP
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class WebSocketChatController {

    private final LiveChatService liveChatService;
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * Handle incoming chat messages
     * Client sends to: /app/chat.send
     * Broadcasts to: /topic/chat/{sessionId}
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload WebSocketMessageDTO message,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            User sender = extractUserFromSession(headerAccessor);

            log.info("WebSocket message received - sender: {}, session: {}, type: {}",
                    message.getSenderType(), message.getSessionId(), message.getMessageType());

            // Process and broadcast the message
            liveChatService.sendLiveMessage(message, sender);

        } catch (Exception e) {
            log.error("Error processing WebSocket message: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle admin messages
     * Client sends to: /app/chat.admin.send
     */
    @MessageMapping("/chat.admin.send")
    public void sendAdminMessage(@Payload WebSocketMessageDTO message,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            User admin = extractUserFromSession(headerAccessor);

            if (admin == null || !isAdmin(admin)) {
                log.warn("Unauthorized admin message attempt");
                return;
            }

            log.info("Admin message - from: {}, to session: {}",
                    admin.getUsername(), message.getSessionId());

            liveChatService.sendAdminMessage(
                    message.getSessionId(),
                    message.getContent(),
                    admin);

        } catch (Exception e) {
            log.error("Error processing admin message: {}", e.getMessage(), e);
        }
    }

    /**
     * Handle typing indicator
     * Client sends to: /app/chat.typing
     * Broadcasts to: /topic/chat/{sessionId}/typing
     */
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload TypingIndicatorDTO indicator) {
        log.debug("Typing indicator - session: {}, typing: {}",
                indicator.getSessionId(), indicator.isTyping());

        liveChatService.sendTypingIndicator(indicator);
    }

    /**
     * Handle user connection (online status)
     * Client sends to: /app/chat.connect
     */
    @MessageMapping("/chat.connect")
    @SendToUser("/queue/connected")
    public Map<String, Object> handleConnect(@Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            User user = extractUserFromSession(headerAccessor);

            Long sessionId = payload.get("sessionId") != null ? Long.valueOf(payload.get("sessionId").toString())
                    : null;
            String guestId = (String) payload.get("guestId");

            if (user != null) {
                liveChatService.userConnected(
                        user.getId(),
                        null,
                        user.getFullName(),
                        sessionId);
            } else if (guestId != null) {
                liveChatService.userConnected(
                        null,
                        guestId,
                        "Khách",
                        sessionId);
            }

            // Return connection confirmation
            return Map.of(
                    "status", "connected",
                    "adminOnline", liveChatService.isAnyAdminOnline(),
                    "timestamp", LocalDateTime.now().toString());

        } catch (Exception e) {
            log.error("Error handling connect: {}", e.getMessage(), e);
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    /**
     * Handle admin connection
     * Client sends to: /app/chat.admin.connect
     */
    @MessageMapping("/chat.admin.connect")
    @SendToUser("/queue/admin/connected")
    public Map<String, Object> handleAdminConnect(SimpMessageHeaderAccessor headerAccessor) {
        try {
            User admin = extractUserFromSession(headerAccessor);

            if (admin == null || !isAdmin(admin)) {
                return Map.of("status", "error", "message", "Unauthorized");
            }

            liveChatService.adminConnected(admin);

            return Map.of(
                    "status", "connected",
                    "adminId", admin.getId(),
                    "onlineUsers", liveChatService.getOnlineUsers(),
                    "unreadCount", liveChatService.getTotalUnreadForAdmin(),
                    "timestamp", LocalDateTime.now().toString());

        } catch (Exception e) {
            log.error("Error handling admin connect: {}", e.getMessage(), e);
            return Map.of("status", "error", "message", e.getMessage());
        }
    }

    /**
     * Handle marking messages as read
     * Client sends to: /app/chat.read
     */
    @MessageMapping("/chat.read")
    public void handleMarkAsRead(@Payload Map<String, Object> payload,
            SimpMessageHeaderAccessor headerAccessor) {
        try {
            Long sessionId = Long.valueOf(payload.get("sessionId").toString());
            String readerType = (String) payload.get("readerType");

            liveChatService.markSessionAsRead(sessionId, readerType);

        } catch (Exception e) {
            log.error("Error marking as read: {}", e.getMessage(), e);
        }
    }

    // ==================== Helper Methods ====================

    private User extractUserFromSession(SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Try to get JWT token from session attributes
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                String token = (String) sessionAttributes.get("token");
                if (token != null && jwtUtils.validateJwtToken(token)) {
                    String username = jwtUtils.getUsernameFromJwtToken(token);
                    return userRepository.findByUsername(username).orElse(null);
                }

                // Or get user from principal
                Long userId = (Long) sessionAttributes.get("userId");
                if (userId != null) {
                    return userRepository.findById(userId).orElse(null);
                }
            }

            // Try Principal
            Principal principal = headerAccessor.getUser();
            if (principal != null) {
                return userRepository.findByUsername(principal.getName()).orElse(null);
            }

        } catch (Exception e) {
            log.debug("Could not extract user from session: {}", e.getMessage());
        }
        return null;
    }

    private boolean isAdmin(User user) {
        return user.getRole() != null &&
                (user.getRole().name().equals("ADMIN") || user.getRole().name().equals("STAFF"));
    }
}
