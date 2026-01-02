package com.flower.manager.controller;

import com.flower.manager.dto.chat.*;
import com.flower.manager.entity.User;
import com.flower.manager.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Chat functionality
 * Handles both user chat and admin chat management
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    // ==================== User Chat Endpoints ====================

    /**
     * Create or get existing chat session
     * 
     * @param guestId Optional guest ID for non-authenticated users
     * @param user    Authenticated user (auto-injected if logged in)
     * @return Chat session with welcome message
     */
    @PostMapping("/session")
    public ResponseEntity<ChatSessionDTO> createOrGetSession(
            @RequestParam(required = false) String guestId,
            @AuthenticationPrincipal User user) {

        log.info("Create/Get session request - User: {}, GuestId: {}",
                user != null ? user.getUsername() : "null", guestId);

        ChatSessionDTO session = chatService.getOrCreateSession(user, guestId);
        return ResponseEntity.ok(session);
    }

    /**
     * Send a message and get bot response
     * 
     * @param request Message request
     * @param user    Authenticated user (auto-injected)
     * @return Bot response with suggestions
     */
    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(
            @Valid @RequestBody SendMessageRequest request,
            @AuthenticationPrincipal User user) {

        log.info("Message received - Session: {}, User: {}",
                request.getSessionId(), user != null ? user.getUsername() : "guest");

        ChatResponse response = chatService.sendMessage(request, user);
        return ResponseEntity.ok(response);
    }

    /**
     * Get session by ID with all messages
     * 
     * @param sessionId Session ID
     * @return Session with messages
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<ChatSessionDTO> getSession(@PathVariable Long sessionId) {
        ChatSessionDTO session = chatService.getSession(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Get messages of a session
     * 
     * @param sessionId Session ID
     * @return List of messages
     */
    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable Long sessionId) {
        List<ChatMessageDTO> messages = chatService.getMessages(sessionId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Request staff support
     * 
     * @param sessionId Session ID
     * @return Updated session
     */
    @PostMapping("/session/{sessionId}/request-staff")
    public ResponseEntity<ChatSessionDTO> requestStaffSupport(@PathVariable Long sessionId) {
        log.info("Staff support requested for session: {}", sessionId);
        ChatSessionDTO session = chatService.requestStaffSupport(sessionId);
        return ResponseEntity.ok(session);
    }

    /**
     * Close a chat session
     * 
     * @param sessionId Session ID
     * @return Success message
     */
    @PostMapping("/session/{sessionId}/close")
    public ResponseEntity<Map<String, String>> closeSession(@PathVariable Long sessionId) {
        chatService.closeSession(sessionId);
        return ResponseEntity.ok(Map.of("message", "Session closed successfully"));
    }

    /**
     * Get user's chat history
     * 
     * @param user Authenticated user
     * @return List of past sessions
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatSessionDTO>> getChatHistory(@AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.ok(List.of());
        }
        List<ChatSessionDTO> history = chatService.getUserChatHistory(user);
        return ResponseEntity.ok(history);
    }

    // ==================== Admin Chat Endpoints ====================

    /**
     * Get all sessions waiting for staff (Admin)
     * 
     * @return List of waiting sessions
     */
    @GetMapping("/admin/waiting")
    public ResponseEntity<List<ChatSessionDTO>> getWaitingSessions() {
        List<ChatSessionDTO> sessions = chatService.getSessionsWaitingForStaff();
        return ResponseEntity.ok(sessions);
    }

    /**
     * Assign staff to a session (Admin)
     * 
     * @param sessionId Session ID
     * @param staff     Staff user (auto-injected)
     * @return Updated session
     */
    @PostMapping("/admin/session/{sessionId}/assign")
    public ResponseEntity<ChatSessionDTO> assignStaff(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal User staff) {

        log.info("Staff {} assigning to session {}", staff.getUsername(), sessionId);
        ChatSessionDTO session = chatService.assignStaffToSession(sessionId, staff);
        return ResponseEntity.ok(session);
    }

    /**
     * Staff sends a message (Admin)
     * 
     * @param sessionId Session ID
     * @param request   Message content
     * @param staff     Staff user (auto-injected)
     * @return Sent message
     */
    @PostMapping("/admin/session/{sessionId}/message")
    public ResponseEntity<ChatMessageDTO> sendStaffMessage(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> request,
            @AuthenticationPrincipal User staff) {

        String content = request.get("content");
        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        ChatMessageDTO message = chatService.sendStaffMessage(sessionId, content, staff);
        return ResponseEntity.ok(message);
    }
}
