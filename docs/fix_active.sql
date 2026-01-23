-- Fix: Cập nhật active = TRUE cho tất cả records
UPDATE shipping_district_rules SET active = TRUE WHERE active IS NULL;

-- Verify
SELECT COUNT(*) as total, SUM(CASE WHEN active = TRUE THEN 1 ELSE 0 END) as active_count 
FROM shipping_district_rules WHERE city = 'TPHCM';
