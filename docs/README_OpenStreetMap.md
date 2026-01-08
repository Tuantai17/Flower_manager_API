# ✅ Tích hợp Map + Autocomplete Địa Chỉ - Flower Shop

## Tổng quan

Tích hợp hoàn chỉnh OpenStreetMap + Photon Geocoding API cho trang Checkout:

- ✅ **Autocomplete** gợi ý địa chỉ khi gõ (debounce 400ms)
- ✅ **Map** hiển thị vị trí + marker theo địa chỉ được chọn
- ✅ **Backend proxy** + cache để ổn định, tránh bị limit từ client
- ✅ **Lưu DB** `shippingAddress + lat + lng + geoProvider + placeId`
- ✅ **Upgrade Path** FREE → PAID (Google/Mapbox) mà không cần rewrite

---

## 📁 Cấu trúc Files

### Backend (Spring Boot)

```
flower-manager/
└── src/main/java/com/flower/manager/
    ├── controller/
    │   └── GeocodeController.java      # Proxy endpoint /api/geocode/search
    ├── entity/
    │   └── Order.java                  # Đã thêm lat, lng, geoProvider, placeId
    ├── dto/order/
    │   ├── OrderDTO.java               # Đã thêm lat, lng, geoProvider, placeId
    │   └── CheckoutRequest.java        # Đã thêm lat, lng, geoProvider, placeId
    └── service/order/
        └── OrderServiceImpl.java       # Đã cập nhật để save/map geocoding fields
```

### Frontend (React)

```
flower-shop-frontend/
└── src/
    ├── index.js                        # Import leaflet CSS + iconFix
    ├── api/
    │   └── geocodeApi.js               # API service cho geocode search
    ├── utils/
    │   └── leafletIconFix.js           # Fix lỗi marker icon
    ├── components/common/
    │   └── AddressPicker.js            # Component Autocomplete + Map
    ├── pages/user/
    │   └── CheckoutPage.js             # Đã tích hợp AddressPicker
    ├── hooks/
    │   └── useCheckout.js              # Hook quản lý checkout state
    └── services/
        └── orderService.js             # Đã thêm lat, lng fields
```

---

## 🔧 Cách sử dụng

### 1. Chạy Backend

```bash
cd flower-manager
mvn spring-boot:run
```

Backend chạy tại: `http://localhost:8080`

### 2. Chạy Frontend

```bash
cd flower-shop-frontend
npm install  # Đã bao gồm leaflet, react-leaflet
npm start
```

Frontend chạy tại: `http://localhost:3000`

### 3. Test Geocode API

Mở browser:

```
http://localhost:8080/api/geocode/search?q=nguyen%20hue%20quan%201
```

Kết quả mong đợi:

```json
{
  "items": [
    {
      "label": "Nguyễn Huệ, Quận 1, Hồ Chí Minh, Việt Nam",
      "lat": 10.774,
      "lng": 106.704,
      "provider": "PHOTON",
      "placeId": null
    }
  ]
}
```

---

## 📋 Flow hoạt động

```
┌─────────────────────────────────────────────────────────────────┐
│  1. User gõ địa chỉ trong AddressPicker                         │
│     ↓ (debounce 400ms)                                          │
│  2. FE gọi: GET /api/geocode/search?q=...                       │
│     ↓                                                           │
│  3. Backend (GeocodeController) check cache                     │
│     ↓ (cache miss)                                              │
│  4. Backend gọi Photon API: https://photon.komoot.io/api/?q=... │
│     ↓                                                           │
│  5. Backend parse response, cache, trả về cho FE                │
│     ↓                                                           │
│  6. FE hiển thị dropdown gợi ý                                  │
│     ↓ (user click)                                              │
│  7. FE set addressLine, lat, lng vào form state                 │
│     ↓                                                           │
│  8. Map fly đến vị trí, hiển thị marker                         │
│     ↓ (user submit)                                             │
│  9. FE gọi: POST /api/orders/checkout                           │
│     Body: { ..., lat, lng, geoProvider, placeId }               │
│     ↓                                                           │
│ 10. Backend lưu vào DB: orders.lat, orders.lng, ...             │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🗄️ Database Schema

Bảng `orders` đã được thêm các cột:

| Column       | Type         | Description                          |
| ------------ | ------------ | ------------------------------------ |
| lat          | DOUBLE       | Latitude từ geocoding                |
| lng          | DOUBLE       | Longitude từ geocoding               |
| geo_provider | VARCHAR(20)  | Provider: PHOTON, GOOGLE, MAPBOX     |
| place_id     | VARCHAR(120) | Place ID (cho Google/Mapbox upgrade) |

**Migration SQL:**

```sql
ALTER TABLE orders ADD COLUMN lat DOUBLE NULL;
ALTER TABLE orders ADD COLUMN lng DOUBLE NULL;
ALTER TABLE orders ADD COLUMN geo_provider VARCHAR(20) NULL;
ALTER TABLE orders ADD COLUMN place_id VARCHAR(120) NULL;
```

---

## 🎨 Component AddressPicker

### Props

| Prop     | Type     | Default      | Description                    |
| -------- | -------- | ------------ | ------------------------------ |
| value    | object   | {}           | { addressLine, lat, lng, ... } |
| onChange | function | required     | Callback khi chọn địa chỉ      |
| error    | string   | null         | Error message                  |
| label    | string   | "Địa chỉ..." | Label của input                |
| required | boolean  | false        | Bắt buộc hay không             |
| showMap  | boolean  | true         | Hiện map hay không             |

### Cách sử dụng

```jsx
import AddressPicker from "../components/common/AddressPicker";

const [address, setAddress] = useState({
  addressLine: "",
  lat: null,
  lng: null,
  provider: null,
  placeId: null,
});

<AddressPicker
  value={address}
  onChange={setAddress}
  error={errors.address}
  label="Địa chỉ giao hàng"
  showMap={true}
/>;
```

---

## 🔄 Upgrade Path: FREE → PAID

### Hiện tại: Photon (FREE)

- Provider: Photon (komoot.io)
- Limit: Không có limit cứng, nhưng nên cache
- Độ chính xác: Tốt cho địa chỉ phổ biến ở Việt Nam

### Upgrade sang Google Places API

1. Tạo file `GoogleGeocodeProvider.java`:

```java
public class GoogleGeocodeProvider implements GeocodeProvider {
    @Value("${google.places.api-key}")
    private String apiKey;

    public List<GeoItem> search(String q) {
        // Gọi Google Places Autocomplete API
        // Parse response
        // Return với provider = "GOOGLE"
    }
}
```

2. Cập nhật `GeocodeController` để switch provider theo ENV:

```java
@Value("${geocode.provider:PHOTON}")
private String provider;

// Switch: if (provider.equals("GOOGLE")) ...
```

3. FE **không cần thay đổi gì** - vẫn gọi `/api/geocode/search`

---

## 🐛 Troubleshooting

### 1. Marker không hiện trên map

- ✅ Đã import `leaflet/dist/leaflet.css` trong `index.js`
- ✅ Đã import `./utils/leafletIconFix` trong `index.js`

### 2. CORS error khi gọi geocode

- ✅ GeocodeController đã có trong `/api/**` path
- ✅ CORS đã được config trong SecurityConfig

### 3. Không ra gợi ý địa chỉ

- Query phải >= 3 ký tự
- Test endpoint: `http://localhost:8080/api/geocode/search?q=test`
- Check log backend xem có lỗi gì không

### 4. Map không load

- Check console browser có lỗi JS không
- Đảm bảo đã install: `npm install leaflet react-leaflet`

---

## 📝 Checklist Test

- [ ] Backend chạy: `http://localhost:8080`
- [ ] Frontend chạy: `http://localhost:3000`
- [ ] Test API geocode: `http://localhost:8080/api/geocode/search?q=nguyen`
- [ ] Vào `/checkout` → gõ địa chỉ → hiện gợi ý
- [ ] Click gợi ý → map fly đến vị trí, marker hiện
- [ ] Submit order → kiểm tra DB có lat, lng
- [ ] Xem order detail → có hiển thị tọa độ

---

## 📚 Tham khảo

- [Photon API Documentation](https://github.com/komoot/photon)
- [React-Leaflet Documentation](https://react-leaflet.js.org/)
- [OpenStreetMap Tiles](https://wiki.openstreetmap.org/wiki/Tiles)

---

**Author:** Generated by Antigravity AI  
**Date:** 2026-01-08  
**Version:** 1.0.0
