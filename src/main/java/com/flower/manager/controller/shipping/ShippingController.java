package com.flower.manager.controller.shipping;

import com.flower.manager.dto.shipping.*;
import com.flower.manager.entity.ShippingDistrictRule;
import com.flower.manager.service.shipping.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý API liên quan đến phí vận chuyển
 */
@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Shipping", description = "API tính phí vận chuyển")
public class ShippingController {

    private final ShippingService shippingService;

    /**
     * Tính phí vận chuyển theo quận/huyện và tổng tiền
     */
    @PostMapping("/calculate")
    @Operation(summary = "Tính phí vận chuyển", description = "Tính phí ship dựa trên quận/huyện và tổng tiền đơn hàng")
    public ResponseEntity<Map<String, Object>> calculateShipping(
            @Valid @RequestBody ShippingCalculateRequest request) {

        log.info("Calculate shipping for district: {}, subtotal: {}",
                request.getDistrict(), request.getSubtotal());

        ShippingCalculateResponse result = shippingService.calculate(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách quận/huyện được hỗ trợ
     */
    @GetMapping("/districts")
    @Operation(summary = "Lấy danh sách quận/huyện", description = "Lấy tất cả quận/huyện đang hỗ trợ giao hàng")
    public ResponseEntity<Map<String, Object>> getDistricts(
            @RequestParam(defaultValue = "TPHCM") String city) {

        List<ShippingDistrictRule> districts = shippingService.getAvailableDistricts(city);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", districts);
        response.put("total", districts.size());

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy danh sách tên quận/huyện cho dropdown
     */
    @GetMapping("/districts/names")
    @Operation(summary = "Lấy tên quận/huyện", description = "Lấy danh sách tên quận/huyện cho dropdown")
    public ResponseEntity<Map<String, Object>> getDistrictNames(
            @RequestParam(defaultValue = "TPHCM") String city) {

        List<String> names = shippingService.getDistrictNames(city);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", names);

        return ResponseEntity.ok(response);
    }

    /**
     * Kiểm tra quận/huyện có được hỗ trợ không
     */
    @GetMapping("/check-support")
    @Operation(summary = "Kiểm tra hỗ trợ", description = "Kiểm tra quận/huyện có được hỗ trợ giao hàng không")
    public ResponseEntity<Map<String, Object>> checkSupport(
            @RequestParam String district,
            @RequestParam(defaultValue = "TPHCM") String city) {

        boolean supported = shippingService.isDistrictSupported(city, district);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("supported", supported);
        response.put("district", district);
        response.put("city", city);

        return ResponseEntity.ok(response);
    }
}
