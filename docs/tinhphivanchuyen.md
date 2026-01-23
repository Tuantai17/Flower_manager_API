Bạn là **Senior Fullstack Architect (Spring Boot 3 + React)**.
Tôi đang làm **Shop Hoa chỉ vận hành tại TP.HCM**. Hãy viết cho tôi **kế hoạch triển khai hoàn chỉnh, chi tiết, copy-paste được** để xây dựng tính năng **Phí vận chuyển tính động (không hardcoded)**, kèm **voucher vận chuyển + voucher giảm giá dùng song song**.

Yêu cầu đầu ra bắt buộc gồm:

1. **Sơ đồ Flow Backend ↔ Frontend** (dạng mermaid sequence) cho Checkout:

- User chọn Quận/Huyện + deliveryType (STANDARD/RUSH) → FE gọi `POST /api/shipping/calculate` → BE trả shippingFee + estimatedTime + freeShipThreshold.
- User nhập voucher ORDER/SHIPPING → FE gọi `POST /api/checkout/preview` → BE validate voucher + tính tổng tiền realtime.
- User đặt hàng → BE lưu Order + snapshot phí (shippingOriginal, shippingFinal, discounts, vouchers).

2. **Thiết kế Database chuẩn (TP.HCM only)**:

- Tạo bảng `shipping_district_rules` (1 quận/huyện = 1 rule) gồm các cột:
  `id, city, district, zone, delivery_type, base_fee, free_ship_threshold, estimated_time, peak_fee, holiday_multiplier, active, created_at, updated_at`.
- Viết **SQL tạo bảng** + **SQL seed dữ liệu** đúng như bảng phí TP.HCM:
  - Nội thành: fee 25k, free từ 500k, thời gian 2-4h (liệt kê các quận nội thành theo mẫu của tôi).
  - Ngoại thành: Quận 12 35k (4-5h), Thủ Đức 35k (4-5h), Bình Tân 35k (4-5h), Tân Phú 30k (3-4h), Hóc Môn 40k (5-6h), Củ Chi 45k (5-6h), Bình Chánh 40k (5-6h), Nhà Bè 40k (5-6h), Cần Giờ 60k (1 ngày), free từ 700k.

- Giải thích rõ **cách chọn rule** theo (city + district + deliveryType + active).

3. **API chuẩn REST**:

- `POST /api/shipping/calculate`
  - Request JSON: `{ city:"TPHCM", district:"Quận 1", subtotal:450000, deliveryType:"STANDARD", requestedTime?:... }`
  - Response JSON: `{ shippingFee, originalFee, freeShipThreshold, isFreeShip, estimatedTime, zone, ruleId, breakdown }`
  - Status codes: 200/400/422.

- `POST /api/checkout/preview`
  - Request JSON: `{ subtotal, district, deliveryType, vouchers:{ orderVoucherCode, shippingVoucherCode } }`
  - Response JSON: `{ shippingOriginal, shippingFee, orderDiscount, shippingDiscount, shippingFinal, grandTotal, warnings }`

4. **Rule tính phí động (distance/time/voucher)**:

- Vì chỉ TP.HCM, distance có thể map bằng district/zone. Nếu có distanceKm thì nêu cách mở rộng.
- Công thức tính:
  - Tính `originalFee = base_fee + peak_fee (nếu giờ cao điểm)`, nhân `holiday_multiplier` nếu ngày lễ.
  - Nếu `subtotal >= free_ship_threshold` → `shippingFee = 0` (miễn phí).
  - Voucher SHIPPING chỉ giảm trên `shippingFee` và không âm: `shippingFinal = max(0, shippingFee - shippingDiscount)`.
  - Voucher ORDER giảm trên subtotal: `grandTotal = subtotal - orderDiscount + shippingFinal`.

5. **Mapping voucher vận chuyển + voucher giảm giá**:

- Voucher có `type`: ORDER hoặc SHIPPING.
- Điều kiện validate: hết hạn, minOrderAmount, perUserLimit, usageLimit, zone/district (optional).
- Thứ tự xử lý chuẩn: tính ship → áp ORDER → áp SHIPPING → ra grandTotal.
- Nêu các edge cases: ship đã free thì voucher ship không có tác dụng; voucher ship không vượt shipFee; 2 voucher dùng đồng thời.

6. **Backend triển khai Spring Boot (không cần full code, nhưng phải có skeleton rõ)**:

- Entities/DTO cần có.
- Repository query ví dụ.
- Service pseudocode: `calculateShipping()` và `previewCheckout()`.
- Controller endpoints.
- Lưu snapshot vào Order khi tạo đơn.

7. **Frontend React triển khai (pseudocode rõ)**:

- State cần có: subtotal, district, deliveryType, shippingFee/originalFee, vouchers, grandTotal.
- Khi district/deliveryType/subtotal đổi: gọi `/shipping/calculate`.
- Khi voucher đổi: gọi `/checkout/preview`.
- UI hiển thị: phí ship, miễn phí từ, thời gian giao, tổng thanh toán.

8. Output phải viết bằng **tiếng Việt**, trình bày theo mục, rõ ràng, có block code JSON/SQL/mermaid.
   Mục tiêu: Tôi đọc prompt này là có thể đem đi triển khai đầy đủ ngay.
