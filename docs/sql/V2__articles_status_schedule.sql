-- =====================================================
-- A1) CẬP NHẬT DATABASE CHO ARTICLES
-- Mục tiêu: Hỗ trợ DRAFT/SCHEDULED/PUBLISHED + AI
-- Timezone: Asia/Ho_Chi_Minh
-- =====================================================

-- 1. Thêm các cột mới vào table articles
ALTER TABLE articles 
ADD COLUMN status ENUM('DRAFT', 'SCHEDULED', 'PUBLISHED', 'ARCHIVED') NOT NULL DEFAULT 'DRAFT' AFTER thumbnail,
ADD COLUMN scheduled_at DATETIME NULL AFTER status,
ADD COLUMN published_at DATETIME NULL AFTER scheduled_at,
ADD COLUMN tags VARCHAR(255) NULL AFTER published_at,
ADD COLUMN ai_generated TINYINT(1) NOT NULL DEFAULT 0 AFTER tags,
ADD COLUMN ai_prompt TEXT NULL AFTER ai_generated;

-- 2. Tạo indexes quan trọng
-- Index cho slug (unique để không trùng)
CREATE UNIQUE INDEX idx_articles_slug ON articles(slug);

-- Index cho query public (status = PUBLISHED, sort by published_at)
CREATE INDEX idx_articles_status_published ON articles(status, published_at DESC);

-- Index cho scheduled job (status = SCHEDULED, scheduled_at <= now)
CREATE INDEX idx_articles_status_scheduled ON articles(status, scheduled_at);

-- Index cho tìm kiếm theo tags
CREATE INDEX idx_articles_tags ON articles(tags);

-- 3. Cập nhật các bài viết cũ (nếu có) thành PUBLISHED
UPDATE articles 
SET status = 'PUBLISHED', 
    published_at = created_at 
WHERE status = 'DRAFT' AND content IS NOT NULL AND content != '';

-- =====================================================
-- VERIFY: Kiểm tra cấu trúc table
-- =====================================================
-- SHOW COLUMNS FROM articles;
-- SELECT id, title, status, scheduled_at, published_at, ai_generated FROM articles;
