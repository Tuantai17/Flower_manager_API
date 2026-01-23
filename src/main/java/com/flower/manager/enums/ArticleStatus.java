package com.flower.manager.enums;

/**
 * Trạng thái bài viết
 * 
 * DRAFT: Bản nháp, chưa công khai
 * SCHEDULED: Đã lên lịch đăng, chờ job publish
 * PUBLISHED: Đã công khai
 * ARCHIVED: Đã lưu trữ, không hiện public
 */
public enum ArticleStatus {
    DRAFT,
    SCHEDULED,
    PUBLISHED,
    ARCHIVED
}
