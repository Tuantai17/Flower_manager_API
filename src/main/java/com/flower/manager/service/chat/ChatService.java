package com.flower.manager.service.chat;

import com.flower.manager.dto.chat.ChatMessageDTO;
import com.flower.manager.dto.chat.ChatResponse;
import com.flower.manager.dto.chat.ChatSessionDTO;
import com.flower.manager.dto.chat.SendMessageRequest;
import com.flower.manager.entity.User;

import java.util.List;

/**
 * Chat Service Interface
 */
public interface ChatService {

    /**
     * Create a new chat session
     * 
     * @param user    Authenticated user (nullable for guest)
     * @param guestId Guest ID for non-authenticated users
     * @return Created session
     */
    ChatSessionDTO createSession(User user, String guestId);

    /**
     * Get or create active session for user/guest
     * 
     * @param user    Authenticated user (nullable for guest)
     * @param guestId Guest ID for non-authenticated users
     * @return Active session
     */
    ChatSessionDTO getOrCreateSession(User user, String guestId);

    /**
     * Send a message and get bot response
     * 
     * @param request Message request
     * @param user    Authenticated user (nullable for guest)
     * @return Chat response with bot reply
     */
    ChatResponse sendMessage(SendMessageRequest request, User user);

    /**
     * Get chat session by ID
     * 
     * @param sessionId Session ID
     * @return Session with messages
     */
    ChatSessionDTO getSession(Long sessionId);

    /**
     * Get message history for a session
     * 
     * @param sessionId Session ID
     * @return List of messages
     */
    List<ChatMessageDTO> getMessages(Long sessionId);

    /**
     * Request staff support
     * 
     * @param sessionId Session ID
     * @return Updated session
     */
    ChatSessionDTO requestStaffSupport(Long sessionId);

    /**
     * Close a chat session
     * 
     * @param sessionId Session ID
     */
    void closeSession(Long sessionId);

    /**
     * Get user's chat history
     * 
     * @param user User
     * @return List of sessions
     */
    List<ChatSessionDTO> getUserChatHistory(User user);

    // ===== Admin methods (Phase 2) =====

    /**
     * Get all sessions waiting for staff
     * 
     * @return List of sessions
     */
    List<ChatSessionDTO> getSessionsWaitingForStaff();

    /**
     * Assign staff to a session
     * 
     * @param sessionId Session ID
     * @param staff     Staff user
     * @return Updated session
     */
    ChatSessionDTO assignStaffToSession(Long sessionId, User staff);

    /**
     * Staff sends a message
     * 
     * @param sessionId Session ID
     * @param content   Message content
     * @param staff     Staff user
     * @return Chat response
     */
    ChatMessageDTO sendStaffMessage(Long sessionId, String content, User staff);
}
