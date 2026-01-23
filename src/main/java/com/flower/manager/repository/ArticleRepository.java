package com.flower.manager.repository;

import com.flower.manager.entity.Article;
import com.flower.manager.enums.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho Article
 * Hỗ trợ:
 * - Public query: chỉ lấy bài PUBLISHED
 * - Admin query: lấy tất cả status
 * - Scheduled job: lấy bài cần publish
 */
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    // ========== PUBLIC QUERIES (chỉ lấy bài PUBLISHED) ==========

    /**
     * Tìm bài viết theo slug (chỉ PUBLISHED)
     */
    Optional<Article> findBySlugAndStatus(String slug, ArticleStatus status);

    /**
     * Lấy danh sách bài viết PUBLISHED, phân trang, sắp xếp theo publishedAt DESC
     */
    Page<Article> findByStatusOrderByPublishedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * Tìm kiếm bài viết theo title (chỉ PUBLISHED)
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status " +
            "AND LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY a.publishedAt DESC")
    Page<Article> searchPublished(@Param("status") ArticleStatus status,
            @Param("q") String q,
            Pageable pageable);

    /**
     * Tìm kiếm bài viết theo tag (chỉ PUBLISHED)
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status " +
            "AND LOWER(a.tags) LIKE LOWER(CONCAT('%', :tag, '%')) " +
            "ORDER BY a.publishedAt DESC")
    Page<Article> findByTagAndStatus(@Param("status") ArticleStatus status,
            @Param("tag") String tag,
            Pageable pageable);

    // ========== ADMIN QUERIES (tất cả status) ==========

    /**
     * Tìm bài viết theo slug (admin - mọi status)
     */
    Optional<Article> findBySlug(String slug);

    /**
     * Kiểm tra slug đã tồn tại chưa
     */
    boolean existsBySlug(String slug);

    /**
     * Đếm số bài viết có slug bắt đầu bằng baseSlug
     * Dùng để tạo slug unique: base-slug, base-slug-1, base-slug-2...
     */
    @Query("SELECT COUNT(a) FROM Article a WHERE a.slug LIKE CONCAT(:baseSlug, '%')")
    long countBySlugStartingWith(@Param("baseSlug") String baseSlug);

    /**
     * Lấy danh sách bài viết theo status (admin)
     */
    Page<Article> findByStatusOrderByCreatedAtDesc(ArticleStatus status, Pageable pageable);

    /**
     * Tìm kiếm bài viết theo title (admin - mọi status)
     */
    @Query("SELECT a FROM Article a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY a.createdAt DESC")
    Page<Article> searchAll(@Param("q") String q, Pageable pageable);

    /**
     * Tìm kiếm bài viết theo title và status (admin)
     */
    @Query("SELECT a FROM Article a WHERE a.status = :status " +
            "AND LOWER(a.title) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "ORDER BY a.createdAt DESC")
    Page<Article> searchByStatus(@Param("status") ArticleStatus status,
            @Param("q") String q,
            Pageable pageable);

    // ========== SCHEDULED JOB QUERIES ==========

    /**
     * Tìm các bài viết SCHEDULED có scheduled_at <= now
     * Dùng cho job tự động publish
     */
    @Query("SELECT a FROM Article a WHERE a.status = 'SCHEDULED' " +
            "AND a.scheduledAt <= :now")
    List<Article> findScheduledArticlesToPublish(@Param("now") LocalDateTime now);

    /**
     * Cập nhật hàng loạt bài viết từ SCHEDULED sang PUBLISHED
     * Sử dụng Modifying query để tối ưu performance
     */
    @Modifying
    @Query("UPDATE Article a SET a.status = 'PUBLISHED', " +
            "a.publishedAt = :now, a.scheduledAt = NULL " +
            "WHERE a.status = 'SCHEDULED' AND a.scheduledAt <= :now")
    int publishScheduledArticles(@Param("now") LocalDateTime now);

    // ========== STATISTICS ==========

    /**
     * Đếm số bài viết theo status
     */
    long countByStatus(ArticleStatus status);

    /**
     * Đếm số bài viết AI generated
     */
    long countByAiGeneratedTrue();
}
