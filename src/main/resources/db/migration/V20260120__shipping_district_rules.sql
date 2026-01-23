-- ========================================
-- SHIPPING DISTRICT RULES - TP.HCM
-- ========================================
-- Bảng lưu trữ quy tắc tính phí vận chuyển theo quận/huyện
-- Chạy file này trong MySQL Workbench hoặc phpMyAdmin

-- 1. Tạo bảng shipping_district_rules
CREATE TABLE IF NOT EXISTS shipping_district_rules (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    city VARCHAR(50) NOT NULL DEFAULT 'TPHCM' COMMENT 'Thành phố',
    district VARCHAR(100) NOT NULL COMMENT 'Quận/Huyện',
    zone ENUM('INNER', 'OUTER') NOT NULL COMMENT 'Khu vực: INNER=Nội thành, OUTER=Ngoại thành',
    delivery_type ENUM('STANDARD', 'RUSH') NOT NULL DEFAULT 'STANDARD' COMMENT 'Loại giao hàng',
    base_fee INT NOT NULL COMMENT 'Phí cơ bản (VND)',
    free_ship_threshold INT NOT NULL COMMENT 'Miễn phí ship từ (VND)',
    estimated_time VARCHAR(30) NOT NULL COMMENT 'Thời gian giao dự kiến',
    peak_fee INT DEFAULT 0 COMMENT 'Phí cao điểm (thêm vào base_fee)',
    holiday_multiplier DECIMAL(3,2) DEFAULT 1.00 COMMENT 'Hệ số nhân ngày lễ',
    active BOOLEAN DEFAULT TRUE COMMENT 'Đang hoạt động',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_district_delivery (city, district, delivery_type),
    INDEX idx_active_city (active, city),
    INDEX idx_zone (zone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Bảng quy tắc phí vận chuyển theo quận/huyện TP.HCM';

-- 2. Xóa dữ liệu cũ (nếu có)
DELETE FROM shipping_district_rules WHERE city = 'TPHCM';

-- 3. Insert dữ liệu NỘI THÀNH TP.HCM (12 quận)
-- Fee: 25.000đ | Free từ: 500.000đ | Thời gian: 2-4 giờ
INSERT INTO shipping_district_rules 
(city, district, zone, delivery_type, base_fee, free_ship_threshold, estimated_time) VALUES
('TPHCM', 'Quận 1', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 3', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 4', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 5', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 6', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 7', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 8', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 10', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận 11', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận Gò Vấp', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận Bình Thạnh', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận Phú Nhuận', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ'),
('TPHCM', 'Quận Tân Bình', 'INNER', 'STANDARD', 25000, 500000, '2-4 giờ');

-- 4. Insert dữ liệu NGOẠI THÀNH TP.HCM (9 quận/huyện)
-- Free từ: 700.000đ | Fee và thời gian khác nhau
INSERT INTO shipping_district_rules 
(city, district, zone, delivery_type, base_fee, free_ship_threshold, estimated_time) VALUES
('TPHCM', 'Quận 12', 'OUTER', 'STANDARD', 35000, 700000, '4-5 giờ'),
('TPHCM', 'TP. Thủ Đức', 'OUTER', 'STANDARD', 35000, 700000, '4-5 giờ'),
('TPHCM', 'Quận Bình Tân', 'OUTER', 'STANDARD', 35000, 700000, '4-5 giờ'),
('TPHCM', 'Quận Tân Phú', 'OUTER', 'STANDARD', 30000, 700000, '3-4 giờ'),
('TPHCM', 'Huyện Hóc Môn', 'OUTER', 'STANDARD', 40000, 700000, '5-6 giờ'),
('TPHCM', 'Huyện Củ Chi', 'OUTER', 'STANDARD', 45000, 700000, '5-6 giờ'),
('TPHCM', 'Huyện Bình Chánh', 'OUTER', 'STANDARD', 40000, 700000, '5-6 giờ'),
('TPHCM', 'Huyện Nhà Bè', 'OUTER', 'STANDARD', 40000, 700000, '5-6 giờ'),
('TPHCM', 'Huyện Cần Giờ', 'OUTER', 'STANDARD', 60000, 700000, '1 ngày');

-- 5. Mở rộng bảng vouchers - thêm cột voucher_type
-- Chạy lệnh này nếu cột chưa tồn tại
ALTER TABLE vouchers 
ADD COLUMN IF NOT EXISTS voucher_type ENUM('ORDER', 'SHIPPING') NOT NULL DEFAULT 'ORDER' 
COMMENT 'Loại voucher: ORDER=giảm tiền hàng, SHIPPING=giảm phí ship'
AFTER description;

-- 6. Thêm cột applicable_zone (optional - giới hạn voucher theo khu vực)
ALTER TABLE vouchers 
ADD COLUMN IF NOT EXISTS applicable_zone ENUM('INNER', 'OUTER', 'ALL') DEFAULT 'ALL'
COMMENT 'Áp dụng cho khu vực: INNER=Nội thành, OUTER=Ngoại thành, ALL=Tất cả'
AFTER voucher_type;

-- 7. Kiểm tra dữ liệu
SELECT 
    zone,
    COUNT(*) as total_districts,
    MIN(base_fee) as min_fee,
    MAX(base_fee) as max_fee,
    MIN(free_ship_threshold) as free_from
FROM shipping_district_rules 
WHERE city = 'TPHCM' AND active = TRUE
GROUP BY zone;

-- 8. Xem chi tiết tất cả quận/huyện
SELECT district, zone, base_fee, free_ship_threshold, estimated_time
FROM shipping_district_rules 
WHERE city = 'TPHCM' AND active = TRUE
ORDER BY zone, district;
