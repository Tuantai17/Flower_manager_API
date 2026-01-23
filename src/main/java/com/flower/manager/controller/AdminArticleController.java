package com.flower.manager.controller;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.article.*;
import com.flower.manager.enums.ArticleStatus;
import com.flower.manager.service.article.ArticleAIService;
import com.flower.manager.service.article.ArticleScraperService;
import com.flower.manager.service.article.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * A5) ADMIN API - Quản lý bài viết
 * 
 * Base URL: /api/admin/articles
 * 
 * Yêu cầu: ROLE_ADMIN
 * 
 * Endpoints:
 * - GET /api/admin/articles - List tất cả bài viết
 * - GET /api/admin/articles/{id} - Chi tiết bài viết
 * - POST /api/admin/articles - Tạo bài viết mới (DRAFT)
 * - PUT /api/admin/articles/{id} - Cập nhật bài viết
 * - DELETE /api/admin/articles/{id} - Xóa bài viết
 * - PATCH /api/admin/articles/{id}/status - Thay đổi trạng thái
 * - POST /api/admin/articles/ai-generate - Tạo bằng AI
 * - POST /api/admin/articles/import - Import từ URL bên ngoài
 */
@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminArticleController {

    private final ArticleService articleService;
    private final ArticleAIService articleAIService;
    private final ArticleScraperService articleScraperService;

    /**
     * GET /api/admin/articles
     * 
     * Lay danh sach tat ca bai viet
     * 
     * Query params:
     * - page: So trang (default: 0)
     * - size: So bai moi trang (default: 10)
     * - status: Filter theo status (DRAFT, SCHEDULED, PUBLISHED, ARCHIVED)
     * - q: Tu khoa tim kiem theo title
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleResponse>>> getArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) String q) {
        Page<ArticleResponse> articles = articleService.getAdminArticles(page, size, status, q);
        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * GET /api/admin/articles/{id}
     * 
     * Lay chi tiet bai viet theo ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticle(
            @PathVariable Long id) {
        ArticleResponse article = articleService.getAdminArticleById(id);
        return ResponseEntity.ok(ApiResponse.success(article));
    }

    /**
     * POST /api/admin/articles
     * 
     * Tao bai viet moi (mac dinh status = DRAFT)
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleResponse>> createArticle(
            @Valid @RequestBody ArticleCreateRequest request) {
        ArticleResponse article = articleService.createArticle(request);
        return ResponseEntity.ok(ApiResponse.success(article, "Tao bai viet thanh cong"));
    }

    /**
     * PUT /api/admin/articles/{id}
     * 
     * Cap nhat bai viet
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateArticle(
            @PathVariable Long id,
            @Valid @RequestBody ArticleUpdateRequest request) {
        ArticleResponse article = articleService.updateArticle(id, request);
        return ResponseEntity.ok(ApiResponse.success(article, "Cap nhat bai viet thanh cong"));
    }

    /**
     * DELETE /api/admin/articles/{id}
     * 
     * Xoa bai viet (hard delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteArticle(
            @PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null, "Xoa bai viet thanh cong"));
    }

    /**
     * PATCH /api/admin/articles/{id}/status
     * 
     * Thay doi trang thai bai viet
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ArticleResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ArticleStatusRequest request) {
        ArticleResponse article = articleService.updateArticleStatus(id, request);
        return ResponseEntity.ok(ApiResponse.success(article, "Cap nhat trang thai thanh cong"));
    }

    // ========== B3) AI GENERATE ENDPOINT ==========

    /**
     * POST /api/admin/articles/ai-generate
     * 
     * Tao bai viet bang AI Gemini
     * Bai viet se duoc luu voi status = DRAFT, ai_generated = true
     * 
     * Request body:
     * {
     * "topic": "Y nghia hoa hong do ngay Valentine",
     * "tone": "am ap, tu van",
     * "keywords": ["hoa hong", "valentine", "qua tang"],
     * "length": "vua",
     * "callToAction": true
     * }
     * 
     * Response: ArticleAIGenerateResponse
     */
    @PostMapping("/ai-generate")
    public ResponseEntity<ApiResponse<ArticleAIGenerateResponse>> generateArticleWithAI(
            @Valid @RequestBody ArticleAIGenerateRequest request) {
        ArticleAIGenerateResponse response = articleAIService.generateArticle(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tao bai viet bang AI thanh cong"));
    }

    // ========== B4) IMPORT FROM URL ENDPOINT ==========

    /**
     * POST /api/admin/articles/import
     * 
     * Import bai viet tu website khac (VnExpress, Kenh14, Dantri, TuoiTre, ...)
     * Bai viet se duoc luu voi status = DRAFT de admin xem lai truoc khi dang
     * 
     * Request body:
     * {
     * "url": "https://vnexpress.net/bai-viet-abc-123.html",
     * "defaultAuthor": "FlowerCorner Team",
     * "defaultTags": "hoa, tin tuc",
     * "customThumbnail": null
     * }
     * 
     * Response: ArticleResponse (bai viet da luu)
     */
    @PostMapping("/import")
    public ResponseEntity<ApiResponse<ArticleResponse>> importFromUrl(
            @Valid @RequestBody ArticleImportRequest request) {
        ArticleResponse response = articleScraperService.importFromUrl(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Import bai viet thanh cong"));
    }
}
