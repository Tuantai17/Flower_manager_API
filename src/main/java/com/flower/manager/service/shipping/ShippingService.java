package com.flower.manager.service.shipping;

import com.flower.manager.dto.shipping.CheckoutPreviewRequest;
import com.flower.manager.dto.shipping.CheckoutPreviewResponse;
import com.flower.manager.dto.shipping.ShippingCalculateRequest;
import com.flower.manager.dto.shipping.ShippingCalculateResponse;
import com.flower.manager.entity.ShippingDistrictRule;

import java.util.List;

/**
 * Service interface cho tính phí vận chuyển
 */
public interface ShippingService {

    /**
     * Tính phí vận chuyển dựa trên quận/huyện và tổng tiền
     */
    ShippingCalculateResponse calculate(ShippingCalculateRequest request);

    /**
     * Preview checkout với voucher
     */
    CheckoutPreviewResponse previewCheckout(CheckoutPreviewRequest request);

    /**
     * Lấy danh sách tất cả quận/huyện đang hỗ trợ
     */
    List<ShippingDistrictRule> getAvailableDistricts(String city);

    /**
     * Lấy danh sách tên quận/huyện cho dropdown
     */
    List<String> getDistrictNames(String city);

    /**
     * Kiểm tra quận có được hỗ trợ không
     */
    boolean isDistrictSupported(String city, String district);
}
