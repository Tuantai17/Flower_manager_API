package com.flower.manager.controller.admin;

import com.flower.manager.dto.shipping.ShippingDistrictRuleDTO;
import com.flower.manager.entity.ShippingDistrictRule;
import com.flower.manager.enums.DeliveryType;
import com.flower.manager.enums.ShippingZone;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.ShippingDistrictRuleRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin Controller cho quản lý Shipping District Rules
 */
@RestController
@RequestMapping("/api/admin/shipping-rules")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminShippingRuleController {

    private final ShippingDistrictRuleRepository repository;

    /**
     * Lấy danh sách rules (có phân trang)
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String zone,
            @RequestParam(required = false) String keyword) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("city", "district"));

        Page<ShippingDistrictRule> rulesPage;

        if (keyword != null && !keyword.isBlank()) {
            rulesPage = repository.findByDistrictContainingIgnoreCase(keyword.trim(), pageable);
        } else if (city != null && !city.isBlank()) {
            rulesPage = repository.findByCity(city.trim(), pageable);
        } else {
            rulesPage = repository.findAll(pageable);
        }

        List<ShippingDistrictRuleDTO> dtos = rulesPage.getContent().stream()
                .map(this::mapToDTO)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("currentPage", rulesPage.getNumber());
        response.put("totalItems", rulesPage.getTotalElements());
        response.put("totalPages", rulesPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    /**
     * Lấy chi tiết 1 rule
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShippingDistrictRuleDTO> getRule(@PathVariable Long id) {
        ShippingDistrictRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShippingDistrictRule", "id", id));
        return ResponseEntity.ok(mapToDTO(rule));
    }

    /**
     * Tạo rule mới
     */
    @PostMapping
    public ResponseEntity<ShippingDistrictRuleDTO> createRule(
            @Valid @RequestBody ShippingDistrictRuleDTO dto) {

        log.info("Creating shipping rule for district: {}", dto.getDistrict());

        ShippingDistrictRule rule = ShippingDistrictRule.builder()
                .city(dto.getCity())
                .district(dto.getDistrict())
                .deliveryType(dto.getDeliveryType() != null ? dto.getDeliveryType() : DeliveryType.STANDARD)
                .baseFee(dto.getBaseFee())
                .peakFee(dto.getPeakFee())
                .freeShipThreshold(dto.getFreeShipThreshold())
                .estimatedTime(dto.getEstimatedTime())
                .holidayMultiplier(dto.getHolidayMultiplier())
                .zone(dto.getZone() != null ? dto.getZone() : ShippingZone.INNER)
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();

        ShippingDistrictRule saved = repository.save(rule);
        log.info("Created shipping rule with id: {}", saved.getId());

        return ResponseEntity.ok(mapToDTO(saved));
    }

    /**
     * Cập nhật rule
     */
    @PutMapping("/{id}")
    public ResponseEntity<ShippingDistrictRuleDTO> updateRule(
            @PathVariable Long id,
            @Valid @RequestBody ShippingDistrictRuleDTO dto) {

        ShippingDistrictRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShippingDistrictRule", "id", id));

        log.info("Updating shipping rule id: {}", id);

        rule.setCity(dto.getCity());
        rule.setDistrict(dto.getDistrict());
        rule.setDeliveryType(dto.getDeliveryType());
        rule.setBaseFee(dto.getBaseFee());
        rule.setPeakFee(dto.getPeakFee());
        rule.setFreeShipThreshold(dto.getFreeShipThreshold());
        rule.setEstimatedTime(dto.getEstimatedTime());
        rule.setHolidayMultiplier(dto.getHolidayMultiplier());
        rule.setZone(dto.getZone());
        rule.setActive(dto.getActive());

        ShippingDistrictRule saved = repository.save(rule);
        log.info("Updated shipping rule id: {}", saved.getId());

        return ResponseEntity.ok(mapToDTO(saved));
    }

    /**
     * Xóa rule
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRule(@PathVariable Long id) {
        ShippingDistrictRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShippingDistrictRule", "id", id));

        log.info("Deleting shipping rule id: {} - district: {}", id, rule.getDistrict());
        repository.delete(rule);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đã xóa quy tắc vận chuyển: " + rule.getDistrict());
        return ResponseEntity.ok(response);
    }

    /**
     * Toggle active status
     */
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ShippingDistrictRuleDTO> toggleActive(@PathVariable Long id) {
        ShippingDistrictRule rule = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShippingDistrictRule", "id", id));

        rule.setActive(!Boolean.TRUE.equals(rule.getActive()));
        ShippingDistrictRule saved = repository.save(rule);

        log.info("Toggled shipping rule id: {} - active: {}", id, saved.getActive());
        return ResponseEntity.ok(mapToDTO(saved));
    }

    /**
     * Lấy danh sách cities (cho dropdown)
     */
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        List<String> cities = repository.findDistinctCities();
        return ResponseEntity.ok(cities);
    }

    /**
     * Lấy danh sách zones (cho dropdown)
     */
    @GetMapping("/zones")
    public ResponseEntity<ShippingZone[]> getZones() {
        return ResponseEntity.ok(ShippingZone.values());
    }

    /**
     * Lấy danh sách delivery types (cho dropdown)
     */
    @GetMapping("/delivery-types")
    public ResponseEntity<DeliveryType[]> getDeliveryTypes() {
        return ResponseEntity.ok(DeliveryType.values());
    }

    // ========== HELPER ==========

    private ShippingDistrictRuleDTO mapToDTO(ShippingDistrictRule rule) {
        return ShippingDistrictRuleDTO.builder()
                .id(rule.getId())
                .city(rule.getCity())
                .district(rule.getDistrict())
                .deliveryType(rule.getDeliveryType())
                .baseFee(rule.getBaseFee())
                .peakFee(rule.getPeakFee())
                .freeShipThreshold(rule.getFreeShipThreshold())
                .estimatedTime(rule.getEstimatedTime())
                .holidayMultiplier(rule.getHolidayMultiplier())
                .zone(rule.getZone())
                .active(rule.getActive())
                .createdAt(rule.getCreatedAt())
                .updatedAt(rule.getUpdatedAt())
                .build();
    }
}
