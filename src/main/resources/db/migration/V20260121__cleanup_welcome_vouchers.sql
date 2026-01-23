-- ============================================
-- Migration: Dọn dẹp voucher WELCOME cũ
-- Chỉ giữ lại voucher WELCOME30 (dùng chung)
-- ============================================

-- 1. Xóa các saved_vouchers liên quan đến voucher WELCOME-XXXX cũ
DELETE FROM saved_vouchers 
WHERE voucher_id IN (
    SELECT id FROM vouchers 
    WHERE code LIKE 'WELCOME-%' 
    AND code != 'WELCOME30'
);

-- 2. Xóa các voucher WELCOME-XXXX cũ (giữ lại WELCOME30)
DELETE FROM vouchers 
WHERE code LIKE 'WELCOME-%' 
AND code != 'WELCOME30';

-- 3. Cập nhật newsletter_subscribers để dùng WELCOME30
UPDATE newsletter_subscribers 
SET voucher_code = 'WELCOME30' 
WHERE voucher_code LIKE 'WELCOME-%';

-- 4. Đảm bảo voucher WELCOME30 tồn tại và active
-- Nếu chưa có, backend sẽ tự tạo khi có người subscribe
