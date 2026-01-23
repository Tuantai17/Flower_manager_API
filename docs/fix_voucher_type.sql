-- Fix voucher type cho các voucher hiện có
-- Chạy trong MySQL Workbench hoặc phpMyAdmin

-- Đặt voucher FREESHIP thành SHIPPING
UPDATE vouchers SET voucher_type = 'SHIPPING' WHERE code LIKE '%FREESHIP%';
UPDATE vouchers SET voucher_type = 'SHIPPING' WHERE code LIKE '%SHIP%';

-- Đặt các voucher khác thành ORDER (nếu chưa có)
UPDATE vouchers SET voucher_type = 'ORDER' WHERE voucher_type IS NULL;

-- Kiểm tra kết quả
SELECT id, code, voucher_type, description FROM vouchers;
