package com.flower.manager.controller;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.article.ArticleListItemResponse;
import com.flower.manager.dto.article.ArticleResponse;
import com.flower.manager.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A4) PUBLIC API - Tin tức công khai
 * 
 * Base URL: /api/news
 * 
 * Không yêu cầu authentication
 * Chỉ trả về bài viết có status = PUBLISHED
 */
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final ArticleService articleService;

    /**
     * GET /api/news
     * 
     * Lấy danh sách bài viết đã publish
     * 
     * Query params:
     * - page: Số trang (default: 0)
     * - size: Số bài mỗi trang (default: 10)
     * - q: Từ khóa tìm kiếm theo title (optional)
     * - tag: Filter theo tag (optional)
     * 
     * Response: Pageable<ArticleListItemResponse>
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ArticleListItemResponse>>> getPublicArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tag) {
        Page<ArticleListItemResponse> articles = articleService.getPublicArticles(page, size, q, tag);
        return ResponseEntity.ok(ApiResponse.success(articles));
    }

    /**
     * GET /api/news/{slug}
     * 
     * Lấy chi tiết bài viết theo slug
     * 
     * Chỉ trả về nếu status = PUBLISHED
     * 404 nếu không tìm thấy hoặc chưa publish
     * 
     * Response: ArticleResponse (full content)
     */
    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<ArticleResponse>> getArticleBySlug(
            @PathVariable String slug) {
        ArticleResponse article = articleService.getPublicArticleBySlug(slug);
        return ResponseEntity.ok(ApiResponse.success(article));
    }
}
