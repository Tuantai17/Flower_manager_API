package com.flower.manager.controller;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.entity.Article;
import com.flower.manager.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/public/articles")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Article>>> getAllArticles() {
        return ResponseEntity.ok(ApiResponse.success(articleService.getAllArticles()));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ApiResponse<Article>> getArticle(@PathVariable String slug) {
        return ResponseEntity.ok(ApiResponse.success(articleService.getArticleBySlug(slug)));
    }

    // Endpoint for n8n or admin to create articles
    @PostMapping
    public ResponseEntity<ApiResponse<Article>> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(ApiResponse.success(articleService.createArticle(article)));
    }
}
