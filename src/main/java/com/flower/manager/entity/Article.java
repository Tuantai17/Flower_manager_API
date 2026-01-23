package com.flower.manager.entity;

import com.flower.manager.enums.ArticleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity Article - Bài viết/Tin tức
 * 
 * Hỗ trợ:
 * - CRUD cơ bản
 * - Scheduled publishing (đặt lịch đăng bài)
 * - AI generated content tracking
 */
@Entity
@Table(name = "articles", indexes = {
        @Index(name = "idx_articles_slug", columnList = "slug", unique = true),
        @Index(name = "idx_articles_status_published", columnList = "status, published_at"),
        @Index(name = "idx_articles_status_scheduled", columnList = "status, scheduled_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @Column(length = 100)
    private String author;

    @Column(length = 500)
    private String thumbnail;

    // ========== CÁC CỘT MỚI CHO LEVEL 1+2+3 ==========

    /**
     * Trạng thái bài viết: DRAFT, SCHEDULED, PUBLISHED, ARCHIVED
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private ArticleStatus status = ArticleStatus.DRAFT;

    /**
     * Thời gian đặt lịch đăng (dùng khi status = SCHEDULED)
     */
    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt;

    /**
     * Thời gian thực sự được publish
     */
    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    /**
     * Tags (lưu dạng CSV, ví dụ: "hoa hồng,valentine,quà tặng")
     */
    @Column(length = 255)
    private String tags;

    /**
     * Đánh dấu bài viết được tạo bởi AI
     */
    @Column(name = "ai_generated", nullable = false)
    @Builder.Default
    private Boolean aiGenerated = false;

    /**
     * Lưu prompt đã dùng để generate (cho Level 3)
     */
    @Column(name = "ai_prompt", columnDefinition = "TEXT")
    private String aiPrompt;

    // ========== TIMESTAMPS ==========

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ArticleStatus.DRAFT;
        }
        if (aiGenerated == null) {
            aiGenerated = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ========== HELPER METHODS ==========

    /**
     * Kiểm tra bài viết đã publish chưa
     */
    public boolean isPublished() {
        return status == ArticleStatus.PUBLISHED;
    }

    /**
     * Kiểm tra bài viết có thể publish không
     */
    public boolean canPublish() {
        return status == ArticleStatus.DRAFT || status == ArticleStatus.SCHEDULED;
    }

    /**
     * Publish bài viết ngay lập tức
     */
    public void publishNow() {
        this.status = ArticleStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.scheduledAt = null;
    }

    /**
     * Schedule bài viết
     */
    public void schedule(LocalDateTime scheduleTime) {
        this.status = ArticleStatus.SCHEDULED;
        this.scheduledAt = scheduleTime;
    }

    /**
     * Archive bài viết
     */
    public void archive() {
        this.status = ArticleStatus.ARCHIVED;
    }
}
