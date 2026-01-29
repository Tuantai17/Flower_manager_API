package com.flower.manager.service.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Service to interact with Google Gemini API
 * Uses the free Gemini API with API key authentication
 */
@Service
@Slf4j
public class GeminiService {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent";

    @Value("${gemini.api-key:AIzaSyCoS0MZ-yRFaXaXgMbPL4acIptL2U31pt4}")
    private String apiKey;

    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GeminiService() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Check if Gemini API is configured
     */
    public boolean isConfigured() {
        return apiKey != null && !apiKey.isEmpty();
    }

    /**
     * Generate response from Gemini AI
     * 
     * @param userMessage         User's message
     * @param context             Additional context (product info, FAQ, etc.)
     * @param conversationHistory Previous messages for context
     * @return AI generated response
     */
    public String generateResponse(String userMessage, String context, List<String> conversationHistory) {
        if (!isConfigured()) {
            log.warn("Gemini API key not configured, using fallback response");
            return null;
        }

        try {
            log.info("=== GEMINI API CALL ===");
            log.info("User Message: {}", userMessage);
            log.info("Context length: {} chars", context != null ? context.length() : 0);

            String requestBody = buildRequestBody(userMessage, context, conversationHistory);

            Request request = new Request.Builder()
                    .url(GEMINI_API_URL + "?key=" + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    String parsedResponse = parseGeminiResponse(responseBody);
                    log.info("Gemini Response: {}",
                            parsedResponse != null
                                    ? parsedResponse.substring(0, Math.min(200, parsedResponse.length())) + "..."
                                    : "null");
                    return parsedResponse;
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No body";
                    log.error("Gemini API error: {} - {} - Body: {}", response.code(), response.message(), errorBody);
                    return null;
                }
            }
        } catch (IOException e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Build the request body for Gemini API
     */
    private String buildRequestBody(String userMessage, String context, List<String> conversationHistory) {
        StringBuilder prompt = new StringBuilder();

        // System context - CRITICAL: Force using ONLY real product data
        prompt.append("[SYSTEM] Bạn là trợ lý ảo AI của cửa hàng hoa FlowerCorner/Flower Shop.\n\n");
        prompt.append("=== QUY TẮC TUYỆT ĐỐI BẮT BUỘC ===\n");
        prompt.append("1. CHỈ ĐƯỢC trả lời sản phẩm ĐÚNG với yêu cầu của khách hàng.\n");
        prompt.append(
                "2. Khi khách hỏi 'dưới X đồng', CHỈ liệt kê sản phẩm có giá < X. KHÔNG gợi ý sản phẩm có giá cao hơn.\n");
        prompt.append("3. Khi khách hỏi về loại hoa cụ thể, CHỈ trả lời về loại đó, KHÔNG lan man sang loại khác.\n");
        prompt.append("4. TRẢ LỜI NGẮN GỌN, đúng trọng tâm. Không viết dài dòng.\n");
        prompt.append("5. Liệt kê sản phẩm dạng danh sách ngắn: Tên - Giá.\n");
        prompt.append("6. TUYỆT ĐỐI KHÔNG bịa đặt tên sản phẩm hoặc giá tiền không có trong danh sách.\n");
        prompt.append("7. Nếu không có sản phẩm phù hợp, chỉ nói 'Xin lỗi, hiện không có sản phẩm phù hợp'.\n");
        prompt.append("8. Dùng tiếng Việt thân thiện, có thể dùng emoji.\n\n");

        // Add REAL product data FIRST - this is the source of truth
        if (context != null && !context.isEmpty()) {
            prompt.append("=== DỮ LIỆU THỰC TỪ DATABASE (NGUỒN DUY NHẤT) ===\n");
            prompt.append(context);
            prompt.append("\n=== KẾT THÚC DỮ LIỆU THỰC ===\n\n");
        }

        // Add conversation history for context
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            prompt.append("Lịch sử hội thoại gần đây:\n");
            for (String msg : conversationHistory) {
                prompt.append(msg).append("\n");
            }
            prompt.append("\n");
        }

        // Add current user message with strong instruction
        prompt.append("[KHÁCH HÀNG HỎI]: ").append(userMessage).append("\n\n");
        prompt.append(
                "[HƯỚNG DẪN]: Trả lời NGẮN GỌN, CHỈ liệt kê sản phẩm ĐÚNG với yêu cầu. KHÔNG gợi ý sản phẩm ngoài tiêu chí khách yêu cầu:");

        try {
            // Build JSON request
            String json = String.format("""
                    {
                        "contents": [{
                            "parts": [{
                                "text": "%s"
                            }]
                        }],
                        "generationConfig": {
                            "temperature": 0.7,
                            "maxOutputTokens": 1024,
                            "topP": 0.9,
                            "topK": 40
                        },
                        "safetySettings": [
                            {"category": "HARM_CATEGORY_HARASSMENT", "threshold": "BLOCK_MEDIUM_AND_ABOVE"},
                            {"category": "HARM_CATEGORY_HATE_SPEECH", "threshold": "BLOCK_MEDIUM_AND_ABOVE"},
                            {"category": "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold": "BLOCK_MEDIUM_AND_ABOVE"},
                            {"category": "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold": "BLOCK_MEDIUM_AND_ABOVE"}
                        ]
                    }
                    """, escapeJson(prompt.toString()));

            return json;
        } catch (Exception e) {
            log.error("Error building request body: {}", e.getMessage());
            return "{}";
        }
    }

    /**
     * Parse Gemini API response
     */
    private String parseGeminiResponse(String responseBody) {
        try {
            // Log full response for debugging
            log.info("Gemini Raw Response: {}", responseBody.substring(0, Math.min(1000, responseBody.length())));

            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode candidates = root.path("candidates");

            if (candidates.isArray() && !candidates.isEmpty()) {
                JsonNode content = candidates.get(0).path("content");
                JsonNode parts = content.path("parts");

                if (parts.isArray() && !parts.isEmpty()) {
                    String text = parts.get(0).path("text").asText();
                    log.info("Parsed text from Gemini: {}", text.substring(0, Math.min(200, text.length())));
                    return text;
                } else {
                    log.warn("No parts found in Gemini response content");
                }
            } else {
                log.warn("No candidates found in Gemini response");
            }

            // Check for error
            JsonNode error = root.path("error");
            if (!error.isMissingNode()) {
                log.error("Gemini API error in response: {} - {}", error.path("code").asText(),
                        error.path("message").asText());
            }

            return null;
        } catch (Exception e) {
            log.error("Error parsing Gemini response: {} - Raw: {}", e.getMessage(),
                    responseBody.substring(0, Math.min(500, responseBody.length())));
            return null;
        }
    }

    /**
     * Escape special characters for JSON string
     */
    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    /**
     * Build conversation history from messages
     */
    public List<String> buildConversationHistory(List<String> userMessages, List<String> botResponses) {
        List<String> history = new ArrayList<>();
        int size = Math.min(userMessages.size(), botResponses.size());

        // Keep last 5 exchanges for context
        int start = Math.max(0, size - 5);
        for (int i = start; i < size; i++) {
            history.add("Khách hàng: " + userMessages.get(i));
            history.add("Trợ lý: " + botResponses.get(i));
        }

        return history;
    }
}
