package com.flower.manager.controller;

import com.flower.manager.dto.chat.*;
import com.flower.manager.entity.ChatMessage;
import com.flower.manager.entity.ChatSession;
import com.flower.manager.entity.User;
import com.flower.manager.repository.ChatMessageRepository;
import com.flower.manager.repository.ChatSessionRepository;
import com.flower.manager.repository.UserRepository;
import com.flower.manager.service.chat.LiveChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Live Chat Admin API
 * Provides endpoints for admin chat management
 */
@RestController
@RequestMapping("/api/livechat")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LiveChatController {

    private final LiveChatService liveChatService;
    private final UserRepository userRepository;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    // ==================== Public Test Endpoint ====================

    /**
     * Public test endpoint (no auth required)
     * GET /api/livechat/public/test
     */
    @GetMapping("/public/test")
    public ResponseEntity<Map<String, Object>> publicTest() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ChatSession> allSessions = sessionRepository.findAll();
            result.put("totalSessions", allSessions.size());

            // Get session details
            List<Map<String, Object>> sessionList = new ArrayList<>();
            for (ChatSession s : allSessions) {
                Map<String, Object> sessionInfo = new HashMap<>();
                sessionInfo.put("id", s.getId());
                sessionInfo.put("status", s.getStatus());
                sessionInfo.put("guestId", s.getGuestId());
                sessionInfo.put("userId", s.getUser() != null ? s.getUser().getId() : null);
                sessionInfo.put("title", s.getTitle());

                // Get message count for this session
                List<ChatMessage> messages = messageRepository.findBySessionOrderBySentAtAsc(s);
                sessionInfo.put("messageCount", messages != null ? messages.size() : 0);
                if (messages != null && !messages.isEmpty()) {
                    sessionInfo.put("lastMessage", messages.get(messages.size() - 1).getContent());
                }

                sessionList.add(sessionInfo);
            }
            result.put("sessions", sessionList);
            result.put("success", true);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("Public test error: {}", e.getMessage(), e);
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Test Gemini API directly
     * GET /api/livechat/public/test-gemini?message=xin chào
     */
    @GetMapping("/public/test-gemini")
    public ResponseEntity<Map<String, Object>> testGemini(
            @RequestParam(defaultValue = "tôi có 200000 thì mua được hoa gì?") String message) {
        Map<String, Object> result = new HashMap<>();

        try {
            result.put("message", message);
            result.put("geminiConfigured", liveChatService.isGeminiConfigured());

            // Build context
            String context = liveChatService.buildContextForTest();
            result.put("contextLength", context != null ? context.length() : 0);
            result.put("contextPreview",
                    context != null ? context.substring(0, Math.min(1000, context.length())) : null);

            // Call Gemini
            String response = liveChatService.testGeminiResponse(message, context);
            result.put("geminiResponse", response);
            result.put("success", response != null && !response.isEmpty());

        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
            log.error("Gemini test error: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok(result);
    }

    // ==================== Admin Endpoints ====================

    /**
     * Get all active chat sessions for admin
     * GET /api/livechat/admin/sessions
     */
    @GetMapping("/admin/sessions")
    public ResponseEntity<List<ChatSessionDTO>> getActiveSessions() {
        log.info("Admin fetching active chat sessions");
        List<ChatSessionDTO> sessions = liveChatService.getActiveSessionsForAdmin();
        log.info("Returning {} sessions to admin", sessions.size());
        return ResponseEntity.ok(sessions);
    }

    /**
     * Debug endpoint - get ALL sessions count (including CLOSED)
     * GET /api/livechat/debug/sessions
     */
    @GetMapping("/debug/sessions")
    public ResponseEntity<Map<String, Object>> debugSessions() {
        Map<String, Object> debug = new HashMap<>();
        try {
            List<String> activeStatuses = Arrays.asList("ACTIVE", "WAITING_STAFF", "WITH_STAFF");

            // Test 1: FindAll
            List<ChatSession> allSessions = sessionRepository.findAll();
            debug.put("findAll_count", allSessions.size());

            // Test 2: Native query
            List<ChatSession> nativeSessions = sessionRepository.findActiveSessionsNative(activeStatuses);
            debug.put("nativeQuery_count", nativeSessions.size());

            // Test 3: Derived query
            List<ChatSession> derivedSessions = sessionRepository.findByStatusInOrderByUpdatedAtDesc(activeStatuses);
            debug.put("derivedQuery_count", derivedSessions.size());

            // Build detailed session info
            List<Map<String, Object>> sessionInfoList = allSessions.stream()
                    .map(s -> {
                        Map<String, Object> info = new HashMap<>();
                        info.put("id", s.getId());
                        info.put("status", "'" + s.getStatus() + "'"); // Wrap in quotes to see whitespace
                        info.put("statusLength", s.getStatus() != null ? s.getStatus().length() : 0);
                        info.put("statusBytes",
                                s.getStatus() != null ? Arrays.toString(s.getStatus().getBytes()) : "null");
                        info.put("matchesFilter", s.getStatus() != null && activeStatuses.contains(s.getStatus()));
                        info.put("guestId", s.getGuestId());
                        info.put("userId", s.getUser() != null ? s.getUser().getId() : null);
                        return info;
                    })
                    .collect(Collectors.toList());
            debug.put("allSessions", sessionInfoList);

            // Also get via service
            List<ChatSessionDTO> serviceSessions = liveChatService.getActiveSessionsForAdmin();
            debug.put("service_count", serviceSessions.size());

            debug.put("status", "ok");
        } catch (Exception e) {
            log.error("Debug sessions error: {}", e.getMessage(), e);
            debug.put("status", "error");
            debug.put("error", e.getMessage());
        }
        return ResponseEntity.ok(debug);
    }

    /**
     * Direct test endpoint - returns sessions directly from repository
     * Bypasses service for debugging
     * GET /api/livechat/test/sessions
     */
    @GetMapping("/test/sessions")
    public ResponseEntity<List<Map<String, Object>>> testSessions() {
        log.info("TEST: Directly fetching sessions from repository");
        List<ChatSession> allSessions = sessionRepository.findAll();
        log.info("TEST: Found {} total sessions in DB", allSessions.size());

        List<Map<String, Object>> result = new ArrayList<>();

        for (ChatSession s : allSessions) {
            String status = s.getStatus();
            if (status != null) {
                String trimmedStatus = status.trim().toUpperCase();
                if ("ACTIVE".equals(trimmedStatus) || "WAITING_STAFF".equals(trimmedStatus)
                        || "WITH_STAFF".equals(trimmedStatus)) {
                    Map<String, Object> sessionInfo = new HashMap<>();
                    sessionInfo.put("id", s.getId());
                    sessionInfo.put("status", s.getStatus());
                    sessionInfo.put("guestId", s.getGuestId());
                    sessionInfo.put("userId", s.getUser() != null ? s.getUser().getId() : null);
                    sessionInfo.put("title", s.getTitle());
                    sessionInfo.put("updatedAt", s.getUpdatedAt());
                    sessionInfo.put("startedAt", s.getStartedAt());
                    result.add(sessionInfo);
                    log.info("TEST: Added session {} to result", s.getId());
                }
            }
        }

        log.info("TEST: Returning {} active sessions", result.size());
        return ResponseEntity.ok(result);
    }

    /**
     * Get sessions waiting for staff
     * GET /api/livechat/admin/sessions/waiting
     */
    @GetMapping("/admin/sessions/waiting")
    public ResponseEntity<List<ChatSessionDTO>> getWaitingSessions() {
        log.info("Admin fetching waiting sessions");
        List<ChatSessionDTO> sessions = liveChatService.getWaitingSessionsForAdmin();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Get session details by ID for admin
     * GET /api/livechat/admin/session/{sessionId}
     */
    @GetMapping("/admin/session/{sessionId}")
    public ResponseEntity<ChatSessionDTO> getSessionDetails(@PathVariable Long sessionId) {
        log.info("Admin fetching session details for: {}", sessionId);
        try {
            ChatSessionDTO session = liveChatService.getSessionDetails(sessionId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            log.error("Error getting session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get messages for a session
     * GET /api/livechat/admin/session/{sessionId}/messages
     */
    @GetMapping("/admin/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getSessionMessages(@PathVariable Long sessionId) {
        log.info("Admin fetching messages for session: {}", sessionId);
        try {
            List<ChatMessageDTO> messages = liveChatService.getMessagesForSession(sessionId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting messages for session {}: {}", sessionId, e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get online status info
     * GET /api/livechat/admin/status
     */
    @GetMapping("/admin/status")
    public ResponseEntity<Map<String, Object>> getAdminStatus(
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, Object> status = new HashMap<>();
        status.put("onlineUsers", liveChatService.getOnlineUsers());
        status.put("onlineAdmins", liveChatService.getOnlineAdmins());
        status.put("unreadCount", liveChatService.getTotalUnreadForAdmin());

        return ResponseEntity.ok(status);
    }

    /**
     * Send message as admin (REST fallback)
     * POST /api/livechat/admin/sessions/{sessionId}/message
     */
    @PostMapping("/admin/sessions/{sessionId}/message")
    public ResponseEntity<ChatMessageDTO> sendAdminMessage(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String content = body.get("content");
        ChatMessageDTO message = liveChatService.sendAdminMessage(sessionId, content, admin);

        return ResponseEntity.ok(message);
    }

    /**
     * Mark session as read
     * POST /api/livechat/sessions/{sessionId}/read
     */
    @PostMapping("/sessions/{sessionId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body) {

        String readerType = body.getOrDefault("readerType", "USER");
        liveChatService.markSessionAsRead(sessionId, readerType);

        return ResponseEntity.ok().build();
    }

    /**
     * Toggle chat mode between AI Bot and Manual Staff
     * POST /api/livechat/admin/sessions/{sessionId}/toggle-mode
     */
    @PostMapping("/admin/sessions/{sessionId}/toggle-mode")
    public ResponseEntity<Map<String, Object>> toggleChatMode(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        String mode = body.getOrDefault("mode", "STAFF"); // STAFF or BOT
        User admin = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        String newStatus = liveChatService.toggleChatMode(sessionId, mode, admin);

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("status", newStatus);
        response.put("mode", "ACTIVE".equals(newStatus) ? "BOT" : "STAFF");

        return ResponseEntity.ok(response);
    }

    // ==================== User Endpoints ====================

    /**
     * Check if admin is online
     * GET /api/livechat/admin-online
     */
    @GetMapping("/admin-online")
    public ResponseEntity<Map<String, Object>> isAdminOnline() {
        Map<String, Object> response = new HashMap<>();
        response.put("adminOnline", liveChatService.isAnyAdminOnline());
        response.put("onlineAdmins", liveChatService.getOnlineAdmins());
        return ResponseEntity.ok(response);
    }

    /**
     * Get unread count for user
     * GET /api/livechat/sessions/{sessionId}/unread
     */
    @GetMapping("/sessions/{sessionId}/unread")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long sessionId) {
        long unread = liveChatService.getUnreadCountForUser(sessionId);
        return ResponseEntity.ok(Map.of("unreadCount", unread));
    }
}
