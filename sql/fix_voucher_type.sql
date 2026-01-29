-- Script sửa voucher_type cho các voucher FREESHIP
-- Các voucher có code chứa "FREESHIP" hoặc liên quan đến phí ship
-- cần được đổi từ ORDER sang SHIPPING

-- Cập nhật voucher FREESHIP thành loại SHIPPING
UPDATE vouchers 
SET voucher_type = 'SHIPPING' 
WHERE code LIKE 'FREESHIP%' 
   OR description LIKE '%vận chuyển%' 
   OR description LIKE '%phí ship%'
   OR description LIKE '%Miễn phí vận chuyển%';

-- Kiểm tra kết quả
SELECT id, code, description, voucher_type 
FROM vouchers 
ORDER BY id;
