package com.flower.manager.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ========================================
 * Geocode Controller - Proxy cho Photon Geocoding API
 * ========================================
 * 
 * Endpoint: GET /api/geocode/search?q=...
 * 
 * Features:
 * - Proxy backend để tránh CORS từ client
 * - Simple in-memory cache với TTL 10 phút
 * - Rate limiting implicit qua cache
 * - Response chuẩn hóa cho FE
 * 
 * Provider: Photon (FREE - OpenStreetMap based)
 * Upgrade path: GOOGLE, MAPBOX bằng cách đổi implementation
 */
@RestController
@RequestMapping("/api/geocode")
public class GeocodeController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Cache đơn giản in-memory (thread-safe)
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final long TTL_MS = 10 * 60 * 1000; // 10 minutes
    private static final int MIN_QUERY_LENGTH = 3;
    private static final int MAX_RESULTS = 6;

    /**
     * Search địa chỉ với Photon API
     * 
     * @param q Query string (minimum 3 characters)
     * @return Map với key "items" chứa list các gợi ý địa chỉ
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(@RequestParam String q) {
        String query = (q == null) ? "" : q.trim();

        // Validate minimum query length
        if (query.length() < MIN_QUERY_LENGTH) {
            return ResponseEntity.ok(Map.of("items", List.of()));
        }

        String key = query.toLowerCase();
        long now = System.currentTimeMillis();

        // Check cache
        CacheEntry cached = cache.get(key);
        if (cached != null && (now - cached.createdAt) < TTL_MS) {
            return ResponseEntity.ok(Map.of("items", cached.items));
        }

        try {
            // Build Photon API URL
            String url = "https://photon.komoot.io/api/?q="
                    + UriUtils.encode(query, StandardCharsets.UTF_8)
                    + "&limit=" + MAX_RESULTS
                    + "&lang=vi";

            // Call Photon API
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = restTemplate.getForObject(url, Map.class);

            List<Map<String, Object>> items = parsePhotonResponse(resp);

            // Update cache
            cache.put(key, new CacheEntry(items, now));

            // Cleanup old cache entries periodically
            cleanupCache(now);

            return ResponseEntity.ok(Map.of("items", items));

        } catch (Exception e) {
            // Log error nhưng vẫn trả về empty để FE không crash
            System.err.println("Geocode API error: " + e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "items", List.of(),
                    "error", "Không thể tìm kiếm địa chỉ. Vui lòng thử lại."));
        }
    }

    /**
     * Reverse Geocoding - Chuyển đổi tọa độ GPS thành địa chỉ
     * Sử dụng Nominatim API (OpenStreetMap official)
     * 
     * @param lat Latitude
     * @param lng Longitude
     * @return Map với thông tin địa chỉ
     */
    @GetMapping("/reverse")
    public ResponseEntity<Map<String, Object>> reverse(
            @RequestParam Double lat,
            @RequestParam Double lng) {

        // Validate coordinates
        if (lat == null || lng == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Thiếu thông tin tọa độ (lat/lng)"));
        }

        // Validate range
        if (lat < -90 || lat > 90 || lng < -180 || lng > 180) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Tọa độ không hợp lệ"));
        }

        // Round to 6 decimals for cache key
        String cacheKey = "reverse_" + String.format("%.6f", lat) + "_" + String.format("%.6f", lng);
        long now = System.currentTimeMillis();

        // Check cache
        CacheEntry cached = cache.get(cacheKey);
        if (cached != null && (now - cached.createdAt) < TTL_MS && !cached.items.isEmpty()) {
            Map<String, Object> cachedItem = cached.items.get(0);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "address", cachedItem.get("label"),
                    "lat", lat,
                    "lng", lng,
                    "provider", "NOMINATIM"));
        }

        try {
            // Build Nominatim Reverse Geocoding URL
            // Nominatim là official OpenStreetMap API - hỗ trợ tiếng Việt tốt
            String url = "https://nominatim.openstreetmap.org/reverse"
                    + "?lat=" + lat
                    + "&lon=" + lng
                    + "&format=json"
                    + "&addressdetails=1"
                    + "&accept-language=vi";

            // Add User-Agent header (required by Nominatim)
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("User-Agent", "FlowerCorner/1.0 (flower delivery app)");
            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            // Call Nominatim API
            @SuppressWarnings("rawtypes")
            org.springframework.http.ResponseEntity<Map> response = restTemplate.exchange(url,
                    org.springframework.http.HttpMethod.GET, entity, Map.class);

            @SuppressWarnings("unchecked")
            Map<String, Object> resp = response.getBody();

            if (resp == null || resp.containsKey("error")) {
                return ResponseEntity.ok(Map.of(
                        "success", false,
                        "error", "Không tìm thấy địa chỉ cho vị trí này",
                        "lat", lat,
                        "lng", lng));
            }

            // Parse Nominatim response
            String displayName = (String) resp.get("display_name");

            // Try to build a shorter, cleaner address
            @SuppressWarnings("unchecked")
            Map<String, Object> addressDetails = (Map<String, Object>) resp.get("address");
            String shortAddress = buildShortAddress(addressDetails, displayName);

            // Cache the result
            List<Map<String, Object>> items = new ArrayList<>();
            Map<String, Object> item = new HashMap<>();
            item.put("label", shortAddress);
            item.put("fullAddress", displayName);
            items.add(item);
            cache.put(cacheKey, new CacheEntry(items, now));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "address", shortAddress,
                    "fullAddress", displayName,
                    "lat", lat,
                    "lng", lng,
                    "provider", "NOMINATIM"));

        } catch (Exception e) {
            System.err.println("Reverse Geocode API error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "error", "Không thể xác định địa chỉ. Vui lòng thử lại.",
                    "lat", lat,
                    "lng", lng));
        }
    }

    /**
     * Build short address from Nominatim address details
     * VD: "Nguyễn Huệ, Quận 1, Hồ Chí Minh"
     */
    private String buildShortAddress(Map<String, Object> address, String fallback) {
        if (address == null) {
            return fallback;
        }

        List<String> parts = new ArrayList<>();

        // House number + Road/Street
        String houseNumber = getStringProp(address, "house_number");
        String road = getStringProp(address, "road");
        if (!road.isEmpty()) {
            if (!houseNumber.isEmpty()) {
                parts.add(houseNumber + " " + road);
            } else {
                parts.add(road);
            }
        }

        // Suburb/Neighbourhood
        String suburb = getStringProp(address, "suburb");
        String neighbourhood = getStringProp(address, "neighbourhood");
        if (!suburb.isEmpty()) {
            parts.add(suburb);
        } else if (!neighbourhood.isEmpty()) {
            parts.add(neighbourhood);
        }

        // District (Quận/Huyện)
        String district = getStringProp(address, "city_district");
        if (district.isEmpty()) {
            district = getStringProp(address, "district");
        }
        if (district.isEmpty()) {
            district = getStringProp(address, "county");
        }
        if (!district.isEmpty()) {
            parts.add(district);
        }

        // City/Province
        String city = getStringProp(address, "city");
        if (city.isEmpty()) {
            city = getStringProp(address, "town");
        }
        if (city.isEmpty()) {
            city = getStringProp(address, "state");
        }
        if (!city.isEmpty()) {
            parts.add(city);
        }

        // If we have parts, join them
        if (!parts.isEmpty()) {
            return String.join(", ", parts);
        }

        // Fallback to display_name but truncate if too long
        if (fallback != null && fallback.length() > 100) {
            // Get first 3 parts of the address
            String[] fallbackParts = fallback.split(",");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < Math.min(3, fallbackParts.length); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append(fallbackParts[i].trim());
            }
            return sb.toString();
        }

        return fallback != null ? fallback : "Vị trí không xác định";
    }

    /**
     * Parse response từ Photon API sang format chuẩn cho FE
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> parsePhotonResponse(Map<String, Object> resp) {
        List<Map<String, Object>> items = new ArrayList<>();

        if (resp == null) {
            return items;
        }

        List<Map<String, Object>> features = (List<Map<String, Object>>) resp.get("features");

        if (features == null) {
            return items;
        }

        for (Map<String, Object> feature : features) {
            try {
                Map<String, Object> props = (Map<String, Object>) feature.get("properties");
                Map<String, Object> geom = (Map<String, Object>) feature.get("geometry");
                List<Number> coords = (List<Number>) geom.get("coordinates");

                if (coords == null || coords.size() < 2) {
                    continue;
                }

                double lng = coords.get(0).doubleValue();
                double lat = coords.get(1).doubleValue();

                // Build label từ properties
                String label = buildAddressLabel(props);

                // Thêm vào kết quả với format chuẩn
                Map<String, Object> item = new HashMap<>();
                item.put("label", label);
                item.put("lat", lat);
                item.put("lng", lng);
                item.put("provider", "PHOTON");
                item.put("placeId", null); // Photon không có placeId

                items.add(item);

            } catch (Exception e) {
                // Skip invalid features
                continue;
            }
        }

        return items;
    }

    /**
     * Build human-readable address label từ Photon properties
     */
    private String buildAddressLabel(Map<String, Object> props) {
        // Ưu tiên: house_number + street → name → city → country
        String houseNumber = getStringProp(props, "housenumber");
        String street = getStringProp(props, "street");
        String name = getStringProp(props, "name");
        String district = getStringProp(props, "district");
        String city = getStringProp(props, "city");
        String state = getStringProp(props, "state");
        String country = getStringProp(props, "country");

        // Build address parts
        List<String> parts = new ArrayList<>();

        // Số nhà + đường
        if (!houseNumber.isEmpty() && !street.isEmpty()) {
            parts.add(houseNumber + " " + street);
        } else if (!street.isEmpty()) {
            parts.add(street);
        } else if (!name.isEmpty()) {
            parts.add(name);
        }

        // Quận
        if (!district.isEmpty()) {
            parts.add(district);
        }

        // Thành phố
        if (!city.isEmpty()) {
            parts.add(city);
        } else if (!state.isEmpty()) {
            parts.add(state);
        }

        // Quốc gia
        if (!country.isEmpty()) {
            parts.add(country);
        }

        return String.join(", ", parts);
    }

    /**
     * Safely get string property from map
     */
    private String getStringProp(Map<String, Object> props, String key) {
        Object value = props.get(key);
        if (value == null) {
            return "";
        }
        String str = String.valueOf(value);
        return "null".equals(str) ? "" : str.trim();
    }

    /**
     * Cleanup old cache entries to prevent memory leak
     */
    private void cleanupCache(long now) {
        // Only cleanup periodically (every 100 requests)
        if (cache.size() > 100) {
            cache.entrySet().removeIf(entry -> (now - entry.getValue().createdAt) > TTL_MS * 2);
        }
    }

    /**
     * Cache entry class
     */
    static class CacheEntry {
        final List<Map<String, Object>> items;
        final long createdAt;

        CacheEntry(List<Map<String, Object>> items, long createdAt) {
            this.items = items;
            this.createdAt = createdAt;
        }
    }
}
