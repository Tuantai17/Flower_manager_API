package com.flower.manager.service.article;

import com.flower.manager.dto.article.*;
import com.flower.manager.entity.Article;
import com.flower.manager.enums.ArticleStatus;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.ArticleRepository;
import com.flower.manager.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service xử lý nghiệp vụ Article
 * 
 * Level 1+2:
 * - CRUD bài viết
 * - Schedule publishing
 * - Tự động publish theo lịch
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ArticleService {

    private final ArticleRepository articleRepository;

    // ========== PUBLIC API (A4) ==========

    /**
     * Lấy danh sách bài viết public (status = PUBLISHED)
     * 
     * @param page Số trang (0-indexed)
     * @param size Số bài mỗi trang
     * @param q    Từ khóa tìm kiếm (optional)
     * @param tag  Tag filter (optional)
     * @return Page<ArticleListItemResponse>
     */
    public Page<ArticleListItemResponse> getPublicArticles(int page, int size, String q, String tag) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles;

        if (q != null && !q.isBlank()) {
            // Tìm kiếm theo title
            articles = articleRepository.searchPublished(ArticleStatus.PUBLISHED, q.trim(), pageable);
        } else if (tag != null && !tag.isBlank()) {
            // Filter theo tag
            articles = articleRepository.findByTagAndStatus(ArticleStatus.PUBLISHED, tag.trim(), pageable);
        } else {
            // Lấy tất cả bài PUBLISHED
            articles = articleRepository.findByStatusOrderByPublishedAtDesc(ArticleStatus.PUBLISHED, pageable);
        }

        return articles.map(ArticleListItemResponse::fromEntity);
    }

    /**
     * Lấy chi tiết bài viết public theo slug
     * Chỉ trả về nếu status = PUBLISHED
     */
    public ArticleResponse getPublicArticleBySlug(String slug) {
        Article article = articleRepository.findBySlugAndStatus(slug, ArticleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại hoặc chưa được đăng"));
        return ArticleResponse.fromEntity(article);
    }

    // ========== ADMIN API (A5) ==========

    /**
     * Lấy danh sách tất cả bài viết (admin)
     * 
     * @param page   Số trang
     * @param size   Số bài mỗi trang
     * @param status Filter theo status (optional)
     * @param q      Từ khóa tìm kiếm (optional)
     */
    public Page<ArticleResponse> getAdminArticles(int page, int size, ArticleStatus status, String q) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Article> articles;

        if (status != null && q != null && !q.isBlank()) {
            // Filter cả status và search
            articles = articleRepository.searchByStatus(status, q.trim(), pageable);
        } else if (status != null) {
            // Chỉ filter status
            articles = articleRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
        } else if (q != null && !q.isBlank()) {
            // Chỉ search
            articles = articleRepository.searchAll(q.trim(), pageable);
        } else {
            // Lấy tất cả
            articles = articleRepository.findAll(pageable);
        }

        return articles.map(ArticleResponse::fromEntity);
    }

    /**
     * Lấy chi tiết bài viết theo ID (admin)
     */
    public ArticleResponse getAdminArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại: " + id));
        return ArticleResponse.fromEntity(article);
    }

    /**
     * Tạo bài viết mới (status = DRAFT)
     */
    @Transactional
    public ArticleResponse createArticle(ArticleCreateRequest request) {
        // Tạo slug từ title
        String slug = generateUniqueSlug(request.getTitle());

        Article article = Article.builder()
                .title(request.getTitle())
                .slug(slug)
                .summary(request.getSummary())
                .content(request.getContent())
                .thumbnail(request.getThumbnail())
                .tags(request.getTags())
                .author(request.getAuthor())
                .status(ArticleStatus.DRAFT)
                .aiGenerated(false)
                .build();

        Article saved = articleRepository.save(article);
        log.info("Created article: id={}, slug={}", saved.getId(), saved.getSlug());
        return ArticleResponse.fromEntity(saved);
    }

    /**
     * Cập nhật bài viết
     */
    @Transactional
    public ArticleResponse updateArticle(Long id, ArticleUpdateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại: " + id));

        // Cập nhật các trường nếu có
        if (request.getTitle() != null) {
            article.setTitle(request.getTitle());
            // Cập nhật slug nếu được yêu cầu
            if (Boolean.TRUE.equals(request.getUpdateSlug())) {
                article.setSlug(generateUniqueSlug(request.getTitle()));
            }
        }
        if (request.getSummary() != null) {
            article.setSummary(request.getSummary());
        }
        if (request.getContent() != null) {
            article.setContent(request.getContent());
        }
        if (request.getThumbnail() != null) {
            article.setThumbnail(request.getThumbnail());
        }
        if (request.getTags() != null) {
            article.setTags(request.getTags());
        }
        if (request.getAuthor() != null) {
            article.setAuthor(request.getAuthor());
        }

        Article saved = articleRepository.save(article);
        log.info("Updated article: id={}", saved.getId());
        return ArticleResponse.fromEntity(saved);
    }

    /**
     * Xóa bài viết (hard delete)
     */
    @Transactional
    public void deleteArticle(Long id) {
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Bài viết không tồn tại: " + id);
        }
        articleRepository.deleteById(id);
        log.info("Deleted article: id={}", id);
    }

    /**
     * Thay đổi trạng thái bài viết
     * 
     * Rules:
     * - DRAFT -> SCHEDULED: scheduledAt bắt buộc và > now
     * - DRAFT -> PUBLISHED: publish ngay
     * - SCHEDULED -> PUBLISHED: publish ngay (bỏ schedule)
     * - PUBLISHED -> ARCHIVED: archive
     */
    @Transactional
    public ArticleResponse updateArticleStatus(Long id, ArticleStatusRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bài viết không tồn tại: " + id));

        ArticleStatus newStatus = request.getStatus();
        LocalDateTime now = LocalDateTime.now();

        switch (newStatus) {
            case SCHEDULED:
                // Validate scheduledAt
                if (request.getScheduledAt() == null) {
                    throw new IllegalArgumentException("Thời gian đặt lịch không được để trống");
                }
                if (request.getScheduledAt().isBefore(now)) {
                    throw new IllegalArgumentException("Thời gian đặt lịch phải lớn hơn thời điểm hiện tại");
                }
                article.setStatus(ArticleStatus.SCHEDULED);
                article.setScheduledAt(request.getScheduledAt());
                log.info("Scheduled article: id={}, scheduledAt={}", id, request.getScheduledAt());
                break;

            case PUBLISHED:
                article.setStatus(ArticleStatus.PUBLISHED);
                article.setPublishedAt(now);
                article.setScheduledAt(null); // Xóa schedule nếu có
                log.info("Published article: id={}", id);
                break;

            case ARCHIVED:
                article.setStatus(ArticleStatus.ARCHIVED);
                log.info("Archived article: id={}", id);
                break;

            case DRAFT:
                article.setStatus(ArticleStatus.DRAFT);
                article.setScheduledAt(null);
                log.info("Reverted article to draft: id={}", id);
                break;
        }

        Article saved = articleRepository.save(article);
        return ArticleResponse.fromEntity(saved);
    }

    // ========== SCHEDULED JOB (A7) ==========

    /**
     * Publish các bài viết đã đến lịch
     * Gọi bởi Scheduled Job mỗi phút
     * 
     * @return Số bài viết đã publish
     */
    @Transactional
    public int publishScheduledArticles() {
        LocalDateTime now = LocalDateTime.now();
        int count = articleRepository.publishScheduledArticles(now);
        if (count > 0) {
            log.info("Published {} scheduled articles at {}", count, now);
        }
        return count;
    }

    // ========== HELPER METHODS ==========

    /**
     * Tạo slug unique từ title
     * Nếu slug đã tồn tại, thêm suffix -1, -2, ...
     */
    private String generateUniqueSlug(String title) {
        String baseSlug = SlugUtils.toSlug(title);

        if (!articleRepository.existsBySlug(baseSlug)) {
            return baseSlug;
        }

        // Tìm suffix phù hợp
        int suffix = 1;
        String newSlug;
        do {
            newSlug = baseSlug + "-" + suffix;
            suffix++;
        } while (articleRepository.existsBySlug(newSlug));

        return newSlug;
    }

    // ========== LEGACY METHODS (backward compatible) ==========

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article getArticleBySlug(String slug) {
        return articleRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Article not found"));
    }

    public Article createArticle(Article article) {
        if (article.getSlug() == null || article.getSlug().isEmpty()) {
            article.setSlug(generateUniqueSlug(article.getTitle()));
        }
        if (article.getStatus() == null) {
            article.setStatus(ArticleStatus.DRAFT);
        }
        if (article.getAiGenerated() == null) {
            article.setAiGenerated(false);
        }
        return articleRepository.save(article);
    }
}
