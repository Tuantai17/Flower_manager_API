package com.flower.manager.service.article;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flower.manager.config.GeminiConfig;
import com.flower.manager.dto.article.ArticleAIGenerateRequest;
import com.flower.manager.dto.article.ArticleAIGenerateResponse;
import com.flower.manager.dto.article.ArticleResponse;
import com.flower.manager.entity.Article;
import com.flower.manager.enums.ArticleStatus;
import com.flower.manager.repository.ArticleRepository;
import com.flower.manager.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * B2 + B3) Service tích hợp Gemini AI để generate bài viết
 * 
 * Flow:
 * 1. Nhận request với topic, tone, keywords
 * 2. Xây dựng prompt chuẩn cho shop hoa
 * 3. Gọi Gemini API
 * 4. Parse response
 * 5. Lưu vào DB với status = DRAFT, ai_generated = true
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleAIService {

    private final GeminiConfig geminiConfig;
    private final ArticleRepository articleRepository;
    private final ObjectMapper objectMapper;

    /**
     * Generate bài viết bằng AI và lưu DRAFT
     */
    @Transactional
    public ArticleAIGenerateResponse generateArticle(ArticleAIGenerateRequest request) {
        log.info("🤖 Generating article with AI: topic={}", request.getTopic());

        // 1. Xây dựng prompt
        String prompt = buildPrompt(request);
        log.debug("Prompt: {}", prompt);

        // 2. Gọi Gemini API
        String aiResponse = callGeminiAPI(prompt);
        log.debug("AI Response: {}", aiResponse);

        // 3. Parse response
        ArticleAIGenerateResponse response = parseAIResponse(aiResponse, request);

        // 4. Lưu vào DB với status = DRAFT
        Article article = Article.builder()
                .title(response.getTitle())
                .slug(generateUniqueSlug(response.getTitle()))
                .summary(response.getSummary())
                .content(response.getContent())
                .tags(response.getTagsSuggestion() != null
                        ? String.join(",", response.getTagsSuggestion())
                        : null)
                .author(request.getAuthor() != null ? request.getAuthor() : "AI Bot")
                .status(ArticleStatus.DRAFT)
                .aiGenerated(true)
                .aiPrompt(prompt)
                .build();

        Article saved = articleRepository.save(article);
        response.setSavedArticleId(saved.getId());
        response.setSlugSuggestion(saved.getSlug());

        log.info("✅ AI article generated and saved: id={}, title={}", saved.getId(), saved.getTitle());
        return response;
    }

    /**
     * B3) Xây dựng prompt chuẩn cho shop hoa
     */
    private String buildPrompt(ArticleAIGenerateRequest request) {
        // Xác định độ dài yêu cầu
        String lengthGuide = switch (request.getLength()) {
            case "ngắn" -> "200-300 từ";
            case "dài" -> "800-1000 từ";
            default -> "400-600 từ";
        };

        // Keywords
        String keywords = request.getKeywords() != null && !request.getKeywords().isEmpty()
                ? String.join(", ", request.getKeywords())
                : "hoa tươi, quà tặng";

        // Tone
        String tone = request.getTone() != null && !request.getTone().isBlank()
                ? request.getTone()
                : "ấm áp, tư vấn chuyên nghiệp";

        // CTA
        String ctaGuide = Boolean.TRUE.equals(request.getCallToAction())
                ? "Thêm phần kêu gọi hành động (CTA) cuối bài, khuyến khích khách hàng liên hệ hoặc mua hàng."
                : "";

        return """
                Bạn là content writer chuyên nghiệp cho cửa hàng hoa tươi FlowerCorner.

                Viết 1 bài blog với các yêu cầu sau:

                📌 CHỦ ĐỀ: %s

                📌 YÊU CẦU:
                - Độ dài: %s
                - Giọng văn: %s
                - Từ khóa SEO: %s
                - Ngôn ngữ: Tiếng Việt, tự nhiên, dễ đọc
                - Format: HTML với các thẻ h2, h3, p, ul/li
                - Không bịa thông tin nhạy cảm, không copy nguồn
                %s

                📌 CẤU TRÚC BÀI VIẾT:
                1. Tiêu đề hấp dẫn (1 dòng)
                2. Tóm tắt ngắn (2-3 câu)
                3. Nội dung chính (có heading h2/h3, bullet points)
                4. Kết luận

                📌 OUTPUT FORMAT (JSON):
                {
                    "title": "Tiêu đề bài viết",
                    "summary": "Tóm tắt 2-3 câu",
                    "content": "<h2>...</h2><p>...</p>...",
                    "tags": ["tag1", "tag2", "tag3"],
                    "thumbnailPrompt": "Mô tả hình ảnh phù hợp"
                }

                CHỈ TRẢ VỀ JSON, KHÔNG CÓ TEXT KHÁC.
                """.formatted(
                request.getTopic(),
                lengthGuide,
                tone,
                keywords,
                ctaGuide);
    }

    /**
     * B2) Gọi Gemini API
     */
    private String callGeminiAPI(String prompt) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            String url = String.format(
                    "%s/models/%s:generateContent?key=%s",
                    geminiConfig.getBaseUrl(),
                    geminiConfig.getModel(),
                    geminiConfig.getApiKey());

            log.info("🌐 Calling Gemini API: model={}", geminiConfig.getModel());

            // Request body theo Gemini API format
            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "parts", List.of(
                                            Map.of("text", prompt)))),
                    "generationConfig", Map.of(
                            "temperature", geminiConfig.getTemperature(),
                            "maxOutputTokens", geminiConfig.getMaxTokens(),
                            "topP", 0.95,
                            "topK", 40));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parse Gemini response
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode candidates = root.path("candidates");
                if (candidates.isArray() && candidates.size() > 0) {
                    JsonNode content = candidates.get(0).path("content").path("parts").get(0).path("text");
                    String result = content.asText();
                    log.info("✅ Gemini API returned {} characters", result.length());
                    return result;
                }

                // Check if there's an error in response
                if (root.has("error")) {
                    String errorMsg = root.path("error").path("message").asText("Unknown error");
                    log.error("❌ Gemini API error: {}", errorMsg);
                    throw new RuntimeException("Lỗi từ Gemini AI: " + errorMsg);
                }
            }

            log.error("❌ Gemini API returned non-200 status: {}", response.getStatusCode());
            throw new RuntimeException("Gemini API trả về lỗi: HTTP " + response.getStatusCode());

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("❌ HTTP Client Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());

            // Parse error message
            try {
                JsonNode errorJson = objectMapper.readTree(e.getResponseBodyAsString());
                String errorMessage = errorJson.path("error").path("message").asText("Unknown error");

                if (errorMessage.contains("API key")) {
                    throw new RuntimeException("API key không hợp lệ. Vui lòng kiểm tra cấu hình Gemini API key.");
                } else if (errorMessage.contains("quota") || errorMessage.contains("RESOURCE_EXHAUSTED")) {
                    throw new RuntimeException("Đã vượt quá quota API. Vui lòng kiểm tra billing hoặc thử lại sau.");
                } else {
                    throw new RuntimeException("Lỗi từ Gemini AI: " + errorMessage);
                }
            } catch (Exception parseEx) {
                throw new RuntimeException("Lỗi gọi API Gemini (HTTP " + e.getStatusCode() + "): " + e.getMessage());
            }

        } catch (Exception e) {
            log.error("❌ Error calling Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi gọi AI: " + e.getMessage(), e);
        }
    }

    /**
     * Parse AI response JSON
     */
    private ArticleAIGenerateResponse parseAIResponse(String aiResponse, ArticleAIGenerateRequest request) {
        try {
            // Clean response (remove markdown code blocks if present)
            String cleanedResponse = aiResponse
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            JsonNode json = objectMapper.readTree(cleanedResponse);

            // Parse tags
            List<String> tags = null;
            if (json.has("tags") && json.get("tags").isArray()) {
                tags = Arrays.asList(
                        objectMapper.treeToValue(json.get("tags"), String[].class));
            }

            return ArticleAIGenerateResponse.builder()
                    .title(json.path("title").asText("Bài viết mới"))
                    .summary(json.path("summary").asText(""))
                    .content(json.path("content").asText(""))
                    .tagsSuggestion(tags)
                    .thumbnailPrompt(json.path("thumbnailPrompt").asText(null))
                    .build();

        } catch (Exception e) {
            log.error("❌ Error parsing AI response: {}", e.getMessage());
            // Fallback: dùng response nguyên bản làm content
            return ArticleAIGenerateResponse.builder()
                    .title("Bài viết về: " + request.getTopic())
                    .summary("Bài viết được tạo bởi AI")
                    .content("<p>" + aiResponse + "</p>")
                    .tagsSuggestion(request.getKeywords())
                    .build();
        }
    }

    /**
     * Tạo slug unique
     */
    private String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.toSlug(title);

        if (!articleRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        int suffix = 1;
        String newSlug;
        do {
            newSlug = baseSlug + "-" + suffix;
            suffix++;
        } while (articleRepository.existsBySlug(newSlug));

        return newSlug;
    }
}
