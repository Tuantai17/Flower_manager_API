package com.flower.manager.config;

import com.flower.manager.entity.ShippingDistrictRule;
import com.flower.manager.enums.DeliveryType;
import com.flower.manager.enums.ShippingZone;
import com.flower.manager.repository.ShippingDistrictRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Data Initializer for Shipping District Rules
 * Seeds 21 districts of TPHCM on application startup if table is empty
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(100) // Run after other initializers
public class ShippingDistrictDataInitializer implements CommandLineRunner {

    private final ShippingDistrictRuleRepository repository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only seed if table is empty
        if (repository.count() > 0) {
            log.info("Shipping district rules already exist, skipping seeding.");
            return;
        }

        log.info("Seeding 21 shipping district rules for TPHCM...");

        // Inner city districts (12 districts) - Fee: 25,000 VND, Free from 500,000 VND
        List<ShippingDistrictRule> innerDistricts = List.of(
                createRule("Quận 1", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận 3", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận 4", ShippingZone.INNER, 25000, 500000, "2-4 giờ"),
                createRule("Quận 5", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận 6", ShippingZone.INNER, 25000, 500000, "2-4 giờ"),
                createRule("Quận 7", ShippingZone.INNER, 25000, 500000, "2-4 giờ"),
                createRule("Quận 8", ShippingZone.INNER, 25000, 500000, "3-4 giờ"),
                createRule("Quận 10", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận 11", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận Phú Nhuận", ShippingZone.INNER, 25000, 500000, "2-3 giờ"),
                createRule("Quận Bình Thạnh", ShippingZone.INNER, 25000, 500000, "2-4 giờ"),
                createRule("Quận Tân Bình", ShippingZone.INNER, 25000, 500000, "2-4 giờ"),
                createRule("Quận Gò Vấp", ShippingZone.INNER, 25000, 500000, "2-4 giờ"));

        // Outer city districts (9 districts) - Free from 700,000 VND
        List<ShippingDistrictRule> outerDistricts = List.of(
                createRule("Quận 12", ShippingZone.OUTER, 35000, 700000, "4-5 giờ"),
                createRule("TP. Thủ Đức", ShippingZone.OUTER, 35000, 700000, "4-5 giờ"),
                createRule("Quận Bình Tân", ShippingZone.OUTER, 35000, 700000, "4-5 giờ"),
                createRule("Quận Tân Phú", ShippingZone.OUTER, 30000, 700000, "3-4 giờ"),
                createRule("Huyện Hóc Môn", ShippingZone.OUTER, 40000, 700000, "5-6 giờ"),
                createRule("Huyện Củ Chi", ShippingZone.OUTER, 45000, 700000, "5-6 giờ"),
                createRule("Huyện Bình Chánh", ShippingZone.OUTER, 40000, 700000, "5-6 giờ"),
                createRule("Huyện Nhà Bè", ShippingZone.OUTER, 40000, 700000, "5-6 giờ"),
                createRule("Huyện Cần Giờ", ShippingZone.OUTER, 60000, 700000, "1 ngày"));

        repository.saveAll(innerDistricts);
        repository.saveAll(outerDistricts);

        log.info("Successfully seeded {} shipping district rules for TPHCM",
                innerDistricts.size() + outerDistricts.size());
    }

    private ShippingDistrictRule createRule(String district, ShippingZone zone,
            int baseFee, int freeShipThreshold, String estimatedTime) {
        return ShippingDistrictRule.builder()
                .city("TPHCM")
                .district(district)
                .zone(zone)
                .deliveryType(DeliveryType.STANDARD)
                .baseFee(baseFee)
                .freeShipThreshold(freeShipThreshold)
                .estimatedTime(estimatedTime)
                .peakFee(0)
                .holidayMultiplier(BigDecimal.ONE)
                .active(true)
                .build();
    }
}
