package com.flower.manager.dto.chat;

import lombok.*;

import java.util.List;

/**
 * Response DTO after sending a message
 * Contains user message and bot response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    /**
     * Session ID
     */
    private Long sessionId;

    /**
     * User's sent message
     */
    private ChatMessageDTO userMessage;

    /**
     * Bot's response message
     */
    private ChatMessageDTO botMessage;

    /**
     * Quick reply suggestions
     */
    private List<String> quickReplies;

    /**
     * Product recommendations (if any)
     */
    private List<ProductSuggestion> productSuggestions;

    /**
     * Whether staff support is needed
     */
    private Boolean needStaffSupport;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSuggestion {
        private Long id;
        private String name;
        private String slug;
        private String thumbnail;
        private String price;
        private String salePrice;
    }
}
