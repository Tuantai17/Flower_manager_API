# ✅ Checklist Triển Khai Phí Vận Chuyển Động

## 📊 1. Database

### Files đã tạo:

- [x] `src/main/resources/db/migration/V20260120__shipping_district_rules.sql`

### Chạy SQL:

```bash
# Trong MySQL Workbench hoặc terminal
mysql -u root -p flower_db < V20260120__shipping_district_rules.sql
```

### Kiểm tra:

```sql
SELECT * FROM shipping_district_rules WHERE city = 'TPHCM';
-- Phải có 22 quận/huyện (13 nội thành + 9 ngoại thành)
```

---

## 🗄️ 2. Backend Spring Boot

### Enums đã tạo:

- [x] `enums/ShippingZone.java` - INNER, OUTER
- [x] `enums/DeliveryType.java` - STANDARD, RUSH
- [x] `enums/VoucherType.java` - ORDER, SHIPPING

### Entity:

- [x] `entity/ShippingDistrictRule.java`

### Repository:

- [x] `repository/ShippingDistrictRuleRepository.java`
- [x] Cập nhật `repository/VoucherRepository.java` (thêm findByCodeAndIsActiveTrue)

### DTOs:

- [x] `dto/shipping/ShippingCalculateRequest.java`
- [x] `dto/shipping/ShippingCalculateResponse.java`
- [x] `dto/shipping/CheckoutPreviewRequest.java`
- [x] `dto/shipping/CheckoutPreviewResponse.java`

### Service:

- [x] `service/shipping/ShippingService.java` (interface)
- [x] `service/shipping/ShippingServiceImpl.java` (implementation)

### Controller:

- [x] `controller/shipping/ShippingController.java`
- [x] `controller/shipping/CheckoutPreviewController.java`

### Test API (Postman/curl):

```bash
# 1. Tính phí ship
curl -X POST http://localhost:8080/api/shipping/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "city": "TPHCM",
    "district": "Quận 1",
    "subtotal": 450000,
    "deliveryType": "STANDARD"
  }'

# Expected: shippingFee = 25000 (vì 450K < 500K)

# 2. Tính với đơn miễn phí
curl -X POST http://localhost:8080/api/shipping/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "district": "Quận 1",
    "subtotal": 550000
  }'

# Expected: shippingFee = 0, isFreeShip = true

# 3. Preview checkout với voucher
curl -X POST http://localhost:8080/api/checkout/preview \
  -H "Content-Type: application/json" \
  -d '{
    "district": "Quận 1",
    "subtotal": 500000,
    "vouchers": {
      "orderVoucherCode": "GIAM10",
      "shippingVoucherCode": "FREESHIP"
    }
  }'

# 4. Lấy danh sách quận
curl http://localhost:8080/api/shipping/districts
```

---

## ⚛️ 3. Frontend React

### Files đã tạo:

- [x] `api/shippingApi.js` - API service
- [x] `hooks/useCheckoutShipping.js` - Custom hook

### Cách sử dụng trong CheckoutPage:

```jsx
import useCheckoutShipping from "../../hooks/useCheckoutShipping";

const CheckoutPage = () => {
  const { cartTotal } = useApp();
  const [district, setDistrict] = useState("");

  // Sử dụng hook
  const {
    shipping,
    vouchers,
    setOrderVoucher,
    setShippingVoucher,
    preview,
    loading,
    error,
  } = useCheckoutShipping({
    subtotal: cartTotal,
    district: district,
    deliveryType: "STANDARD",
  });

  return (
    <div>
      {/* Phí ship */}
      <p>
        Phí vận chuyển:{" "}
        {shipping.isFreeShip ? "Miễn phí" : formatPrice(shipping.shippingFee)}
      </p>
      <p>Thời gian giao: {shipping.estimatedTime}</p>

      {/* Voucher ORDER */}
      <input
        value={vouchers.orderVoucherCode}
        onChange={(e) => setOrderVoucher(e.target.value)}
        placeholder="Mã giảm giá đơn hàng"
      />

      {/* Voucher SHIPPING */}
      <input
        value={vouchers.shippingVoucherCode}
        onChange={(e) => setShippingVoucher(e.target.value)}
        placeholder="Mã giảm phí ship"
      />

      {/* Tổng tiền */}
      <p>Tổng: {formatPrice(preview.grandTotal)}</p>
    </div>
  );
};
```

---

## 📋 Checklist Test

- [ ] Chọn quận nội thành → phí ship 25K, miễn phí từ 500K
- [ ] Chọn quận ngoại thành → phí ship khác nhau, miễn phí từ 700K
- [ ] Đơn >= ngưỡng miễn phí → phí ship = 0
- [ ] Áp voucher ORDER → giảm tiền hàng
- [ ] Áp voucher SHIPPING → giảm phí ship
- [ ] Áp cả 2 voucher cùng lúc → cả 2 đều áp dụng
- [ ] Ship đã free + voucher ship → warning

---

## 🎉 Hoàn thành!

Tất cả files đã được tạo. Các bước tiếp theo:

1. **Chạy SQL** để tạo bảng và seed data
2. **Restart Backend** để load Entity mới
3. **Test API** bằng Postman
4. **Cập nhật CheckoutPage** sử dụng hook mới
