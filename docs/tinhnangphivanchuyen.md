Bạn là **Senior Fullstack Architect (Spring Boot 3.x + Java 21 + MySQL + React)**.
Dự án của tôi là **Shop Hoa chỉ vận hành tại TP.HCM**. Tôi muốn triển khai tính năng **Phí vận chuyển tính động (không hardcoded)** và hỗ trợ áp dụng đồng thời **2 loại voucher**:

- Voucher giảm giá đơn hàng (**ORDER**)
- Voucher giảm phí vận chuyển (**SHIPPING**)

Hãy viết tài liệu triển khai **HOÀN CHỈNH – RÕ RÀNG – CHI TIẾT – COPY/PASTE ĐƯỢC** gồm đầy đủ 3 phần: **DATABASE → BACKEND → FRONTEND**, theo đúng yêu cầu sau:

========================
A) DATABASE (MySQL)
===================

1. Tạo bảng `shipping_district_rules` (TP.HCM only) với các cột:

- `id, city, district, zone, delivery_type, base_fee, free_ship_threshold, estimated_time, peak_fee, holiday_multiplier, active, created_at, updated_at`
- unique key `(city, district, delivery_type)`

2. Viết **SQL CREATE TABLE** hoàn chỉnh.
3. Viết **SQL SEED DATA** đúng bảng phí TP.HCM giống trang chính sách vận chuyển của tôi:

- Nội thành TP.HCM: `fee=25.000`, `free_from=500.000`, thời gian `2-3 giờ` hoặc `2-4 giờ`
  Danh sách quận nội thành: Quận 1, Quận 3, Quận 5, Quận 10, Quận 11, Quận Phú Nhuận, Quận Bình Thạnh, Quận Tân Bình, Quận Gò Vấp, Quận 4, Quận 7, Quận 8.
- Ngoại thành TP.HCM: `free_from=700.000`
  Quận 12: 35k (4-5h), TP. Thủ Đức: 35k (4-5h), Quận Bình Tân: 35k (4-5h), Quận Tân Phú: 30k (3-4h), Huyện Hóc Môn: 40k (5-6h), Huyện Củ Chi: 45k (5-6h), Huyện Bình Chánh: 40k (5-6h), Huyện Nhà Bè: 40k (5-6h), Huyện Cần Giờ: 60k (1 ngày).

4. Mở rộng bảng `vouchers` hiện có (các cột đang có: code, description, discount_value, start_date, end_date, is_active, is_percent, max_discount, min_order_value, usage_count, usage_limit):

- Thêm cột `voucher_type ENUM('ORDER','SHIPPING') DEFAULT 'ORDER'`
- (Optional) thêm cột `applicable_zone ENUM('INNER_HCM','OUTER_HCM','ALL') DEFAULT 'ALL'`

5. Viết SQL ALTER TABLE hoàn chỉnh + câu SELECT kiểm tra dữ liệu.

========================
B) BACKEND (Spring Boot)
========================

1. Vẽ **Flow Checkout Backend ↔ Frontend** bằng mermaid sequence diagram:

- User chọn Quận/Huyện + deliveryType → FE gọi `POST /api/shipping/calculate` → BE trả ship fee + threshold + time
- User nhập voucher ORDER/SHIPPING → FE gọi `POST /api/checkout/preview` → BE validate voucher và tính tổng tiền realtime
- User bấm đặt hàng → BE lưu order + snapshot phí và voucher đã áp

2. Thiết kế API chuẩn REST:

- `POST /api/shipping/calculate`
  Request JSON: `{ city:"TPHCM", district:"Quận 1", subtotal:450000, deliveryType:"STANDARD", requestedTime?: "ISO" }`
  Response JSON: `{ shippingFee, originalFee, currency:"VND", isFreeShip, freeShipThreshold, estimatedTime, zone, ruleId, breakdown }`
  Status codes: 200/400/422
- `POST /api/checkout/preview`
  Request JSON: `{ district, deliveryType, subtotal, vouchers:{ orderVoucherCode, shippingVoucherCode } }`
  Response JSON: `{ subtotal, shippingOriginal, shippingFee, orderDiscount, shippingDiscount, shippingFinal, grandTotal, warnings }`

3. Viết skeleton code **Entity/Repository/Service/Controller** (đúng cấu trúc, có package rõ):

- Entity `ShippingDistrictRule` + enum Zone/DeliveryType
- Repository query `findFirstByCityAndDistrictAndDeliveryTypeAndActiveTrue(...)`
- Service `ShippingService.calculate(...)` tính phí theo rule + free ship + breakdown
- Controller `ShippingController` map endpoint `/api/shipping/calculate`
- Service `CheckoutPreviewService.preview(...)` gọi ship + validate voucher ORDER/SHIPPING + tính `grandTotal`
- Controller `CheckoutController` endpoint `/api/checkout/preview`

4. Viết rõ **công thức tính**:

- `originalFee = (base_fee + peak_fee + rush_fee) * holiday_multiplier`
- Nếu `subtotal >= free_ship_threshold` → `shippingFee = 0` else `shippingFee = originalFee`
- Voucher ORDER giảm trên subtotal
- Voucher SHIPPING giảm trên shippingFee và không âm:
  `shippingDiscount = min(shippingFee, discountValue...)`
  `shippingFinal = max(0, shippingFee - shippingDiscount)`
- `grandTotal = subtotal - orderDiscount + shippingFinal`

5. Viết module validate voucher dựa trên schema voucher hiện có:

- check `is_active`, `start_date/end_date`, `usage_limit/usage_count`, `min_order_value`
- nếu `is_percent=1` thì discount = subtotal \* discount_value% và áp `max_discount`
- nếu `is_percent=0` thì discount = discount_value (cố định)
- phân biệt theo `voucher_type`:
  - ORDER: tính trên subtotal
  - SHIPPING: tính trên shippingFee (và optional check applicable_zone)

- Edge cases:
  - ship đã free thì voucher ship không tác dụng (warnings)
  - voucher ship không vượt shippingFee
  - cho phép dùng đồng thời 2 voucher (1 ORDER + 1 SHIPPING)

========================
C) FRONTEND (React)
===================

1. Tạo hook `useCheckoutShipping`:

- State: subtotal, district, deliveryType, shippingFee/originalFee/freeShipThreshold/estimatedTime, orderVoucherCode, shippingVoucherCode, preview(grandTotal, discounts), loading/error
- Khi district/deliveryType/subtotal đổi → call `/api/shipping/calculate`
- Khi voucher đổi → call `/api/checkout/preview`

2. Cập nhật CheckoutPage:

- Dropdown chọn quận/huyện TP.HCM
- Hiển thị phí ship, miễn phí từ, thời gian dự kiến
- 2 ô nhập voucher: ORDER & SHIPPING
- Tổng thanh toán realtime + hiển thị warnings

3. Cho ví dụ code React (hook + cách dùng trong CheckoutPage) với axios hoặc apiService.

========================
D) OUTPUT FORMAT
================

- Viết **tiếng Việt**
- Trình bày theo mục A/B/C rõ ràng
- Có block code riêng cho: SQL, mermaid, JSON examples, Java skeleton, React hook
- Có checklist triển khai + checklist test (curl / Postman)
- Mục tiêu: tôi copy prompt này là có thể triển khai trọn bộ từ DB → API → UI

Hãy trả về đầy đủ nội dung theo đúng yêu cầu trên, không bỏ sót phần nào.
