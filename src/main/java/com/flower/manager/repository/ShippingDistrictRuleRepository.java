package com.flower.manager.repository;

import com.flower.manager.entity.ShippingDistrictRule;
import com.flower.manager.enums.DeliveryType;
import com.flower.manager.enums.ShippingZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho ShippingDistrictRule
 */
@Repository
public interface ShippingDistrictRuleRepository extends JpaRepository<ShippingDistrictRule, Long> {

    /**
     * Tìm rule theo city, district và delivery type
     */
    Optional<ShippingDistrictRule> findFirstByCityAndDistrictAndDeliveryTypeAndActiveTrue(
            String city, String district, DeliveryType deliveryType);

    /**
     * Tìm rule với delivery type mặc định là STANDARD
     */
    default Optional<ShippingDistrictRule> findByDistrict(String city, String district) {
        return findFirstByCityAndDistrictAndDeliveryTypeAndActiveTrue(city, district, DeliveryType.STANDARD);
    }

    /**
     * Lấy tất cả quận/huyện đang active theo thành phố
     * Hỗ trợ cả active = TRUE và active IS NULL (default TRUE)
     */
    @Query("SELECT r FROM ShippingDistrictRule r WHERE r.city = :city AND (r.active = true OR r.active IS NULL) ORDER BY r.zone, r.district")
    List<ShippingDistrictRule> findByCityAndActiveOrNull(@Param("city") String city);

    /**
     * Lấy tất cả quận/huyện đang active theo thành phố (original method)
     */
    List<ShippingDistrictRule> findByCityAndActiveTrueOrderByZoneAscDistrictAsc(String city);

    /**
     * Lấy tất cả quận/huyện theo zone
     */
    List<ShippingDistrictRule> findByCityAndZoneAndActiveTrue(String city, ShippingZone zone);

    /**
     * Kiểm tra quận có được hỗ trợ không (hỗ trợ cả NULL)
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM ShippingDistrictRule r WHERE r.city = :city AND r.district = :district AND (r.active = true OR r.active IS NULL)")
    boolean existsByCityAndDistrictActiveOrNull(@Param("city") String city, @Param("district") String district);

    /**
     * Kiểm tra quận có được hỗ trợ không (original)
     */
    boolean existsByCityAndDistrictAndActiveTrue(String city, String district);

    /**
     * Đếm số quận theo zone
     */
    @Query("SELECT r.zone, COUNT(r) FROM ShippingDistrictRule r WHERE r.city = :city AND r.active = true GROUP BY r.zone")
    List<Object[]> countByZone(@Param("city") String city);

    /**
     * Lấy danh sách tên quận/huyện để hiển thị dropdown (hỗ trợ cả NULL)
     */
    @Query("SELECT DISTINCT r.district FROM ShippingDistrictRule r WHERE r.city = :city AND (r.active = true OR r.active IS NULL) ORDER BY r.district")
    List<String> findDistinctDistrictsByCityOrNull(@Param("city") String city);

    /**
     * Lấy danh sách tên quận/huyện để hiển thị dropdown (original)
     */
    @Query("SELECT DISTINCT r.district FROM ShippingDistrictRule r WHERE r.city = :city AND r.active = true ORDER BY r.district")
    List<String> findDistinctDistrictsByCity(@Param("city") String city);

    /**
     * Tìm rule cho calculate (hỗ trợ cả NULL)
     */
    @Query("SELECT r FROM ShippingDistrictRule r WHERE r.city = :city AND r.district = :district AND r.deliveryType = :deliveryType AND (r.active = true OR r.active IS NULL)")
    Optional<ShippingDistrictRule> findRuleForCalculate(@Param("city") String city, @Param("district") String district,
            @Param("deliveryType") DeliveryType deliveryType);

    // ========== ADMIN METHODS ==========

    /**
     * Phân trang theo city
     */
    Page<ShippingDistrictRule> findByCity(String city, Pageable pageable);

    /**
     * Tìm kiếm theo district (có phân trang)
     */
    Page<ShippingDistrictRule> findByDistrictContainingIgnoreCase(String district, Pageable pageable);

    /**
     * Lấy danh sách cities duy nhất
     */
    @Query("SELECT DISTINCT r.city FROM ShippingDistrictRule r ORDER BY r.city")
    List<String> findDistinctCities();
}
