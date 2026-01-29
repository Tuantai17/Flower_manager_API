package com.flower.manager.service.shipping;

import com.flower.manager.dto.shipping.*;
import com.flower.manager.entity.ShippingDistrictRule;
import com.flower.manager.entity.Voucher;
import com.flower.manager.enums.DeliveryType;
import com.flower.manager.exception.ResourceNotFoundException;
import com.flower.manager.repository.ShippingDistrictRuleRepository;
import com.flower.manager.repository.VoucherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation của ShippingService
 * Tính phí vận chuyển động theo quận/huyện TP.HCM
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingServiceImpl implements ShippingService {

    private final ShippingDistrictRuleRepository ruleRepository;
    private final VoucherRepository voucherRepository;

    // Giờ cao điểm: 11h-13h và 17h-20h
    private static final LocalTime PEAK_HOUR_1_START = LocalTime.of(11, 0);
    private static final LocalTime PEAK_HOUR_1_END = LocalTime.of(13, 0);
    private static final LocalTime PEAK_HOUR_2_START = LocalTime.of(17, 0);
    private static final LocalTime PEAK_HOUR_2_END = LocalTime.of(20, 0);

    @Override
    public ShippingCalculateResponse calculate(ShippingCalculateRequest request) {
        String city = request.getCity() != null ? request.getCity() : "TPHCM";
        DeliveryType deliveryType = request.getDeliveryType() != null
                ? request.getDeliveryType()
                : DeliveryType.STANDARD;

        // 1. Tìm rule theo quận/huyện (hỗ trợ cả active = NULL)
        ShippingDistrictRule rule = ruleRepository
                .findRuleForCalculate(city, request.getDistrict(), deliveryType)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Quận/Huyện không được hỗ trợ: " + request.getDistrict()));

        // 2. Kiểm tra giờ cao điểm và ngày lễ
        boolean isPeakHour = isPeakHour();
        boolean isHoliday = isHoliday();

        // 3. Tính phí vận chuyển
        int originalFee = calculateOriginalFee(rule, isPeakHour, isHoliday);
        boolean isFreeShip = rule.isFreeShip(request.getSubtotal());
        int shippingFee = isFreeShip ? 0 : originalFee;

        // 4. Build response
        return ShippingCalculateResponse.builder()
                .ruleId(rule.getId())
                .zone(rule.getZone())
                .zoneName(rule.getZone().getDisplayName())
                .originalFee(originalFee)
                .shippingFee(shippingFee)
                .currency("VND")
                .isFreeShip(isFreeShip)
                .freeShipThreshold(rule.getFreeShipThreshold())
                .amountToFreeShip(rule.getAmountToFreeShip(request.getSubtotal()))
                .estimatedTime(rule.getEstimatedTime())
                .breakdown(ShippingCalculateResponse.ShippingBreakdown.builder()
                        .baseFee(rule.getBaseFee())
                        .peakFee(rule.getPeakFee() != null ? rule.getPeakFee() : 0)
                        .holidayMultiplier(
                                rule.getHolidayMultiplier() != null ? rule.getHolidayMultiplier().doubleValue() : 1.0)
                        .isPeakHour(isPeakHour)
                        .isHoliday(isHoliday)
                        .build())
                .build();
    }

    @Override
    public CheckoutPreviewResponse previewCheckout(CheckoutPreviewRequest request) {
        List<CheckoutPreviewResponse.AppliedVoucher> appliedVouchers = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // 1. Tính phí ship
        ShippingCalculateRequest shippingRequest = ShippingCalculateRequest.builder()
                .city("TPHCM")
                .district(request.getDistrict())
                .subtotal(request.getSubtotal())
                .deliveryType(request.getDeliveryType())
                .build();
        ShippingCalculateResponse shippingResult = calculate(shippingRequest);

        int subtotal = request.getSubtotal();
        int shippingOriginal = shippingResult.getShippingFee();
        int orderDiscount = 0;
        int shippingDiscount = 0;

        // 2. Áp dụng voucher ORDER
        if (request.getVouchers() != null && request.getVouchers().getOrderVoucherCode() != null) {
            String orderVoucherCode = request.getVouchers().getOrderVoucherCode().trim();
            if (!orderVoucherCode.isEmpty()) {
                try {
                    Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(orderVoucherCode)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Mã voucher không tồn tại: " + orderVoucherCode));

                    // Kiểm tra loại voucher phải là ORDER (hoặc NULL cho backward compatibility)
                    if (voucher.getVoucherType() != null
                            && voucher.getVoucherType() != com.flower.manager.enums.VoucherType.ORDER) {
                        warnings.add("Mã " + orderVoucherCode
                                + " là voucher giảm phí vận chuyển, không áp dụng được cho đơn hàng");
                    } else if (voucher.isValid()) {
                        BigDecimal discount = voucher.calculateDiscount(BigDecimal.valueOf(subtotal));
                        orderDiscount = discount.intValue();

                        appliedVouchers.add(CheckoutPreviewResponse.AppliedVoucher.builder()
                                .code(voucher.getCode())
                                .type("ORDER")
                                .discount(orderDiscount)
                                .description(voucher.getDescription())
                                .build());
                    } else {
                        warnings.add("Voucher " + orderVoucherCode + " đã hết hạn hoặc không còn hiệu lực");
                    }
                } catch (ResourceNotFoundException e) {
                    warnings.add(e.getMessage());
                }
            }
        }

        // 3. Áp dụng voucher SHIPPING
        if (request.getVouchers() != null && request.getVouchers().getShippingVoucherCode() != null) {
            String shippingVoucherCode = request.getVouchers().getShippingVoucherCode().trim();
            if (!shippingVoucherCode.isEmpty()) {
                if (shippingOriginal == 0) {
                    warnings.add("Phí ship đã miễn phí, voucher ship không có tác dụng");
                } else {
                    try {
                        Voucher voucher = voucherRepository.findByCodeAndIsActiveTrue(shippingVoucherCode)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Mã voucher không tồn tại: " + shippingVoucherCode));

                        if (voucher.getVoucherType() != null
                                && voucher.getVoucherType() != com.flower.manager.enums.VoucherType.SHIPPING) {
                            // Nếu voucher không phải loại SHIPPING, cảnh báo người dùng
                            warnings.add("Mã " + shippingVoucherCode
                                    + " là voucher giảm giá đơn hàng, không áp dụng được cho phí vận chuyển");
                        } else if (voucher.isValid()) {
                            // Check minOrderValue với SUBTOTAL (tiền hàng), không phải phí ship
                            BigDecimal minOrder = voucher.getMinOrderValue() != null ? voucher.getMinOrderValue()
                                    : BigDecimal.ZERO;
                            if (BigDecimal.valueOf(subtotal).compareTo(minOrder) < 0) {
                                warnings.add("Đơn hàng chưa đạt " + minOrder.intValue() + "đ để áp dụng mã "
                                        + shippingVoucherCode);
                            } else {
                                // Tính discount dựa trên phí shipping
                                BigDecimal discount;
                                if (Boolean.TRUE.equals(voucher.getIsPercent())) {
                                    discount = BigDecimal.valueOf(shippingOriginal)
                                            .multiply(voucher.getDiscountValue())
                                            .divide(BigDecimal.valueOf(100));
                                    if (voucher.getMaxDiscount() != null
                                            && discount.compareTo(voucher.getMaxDiscount()) > 0) {
                                        discount = voucher.getMaxDiscount();
                                    }
                                } else {
                                    discount = voucher.getDiscountValue();
                                }
                                // Không vượt quá phí ship
                                shippingDiscount = Math.min(discount.intValue(), shippingOriginal);

                                appliedVouchers.add(CheckoutPreviewResponse.AppliedVoucher.builder()
                                        .code(voucher.getCode())
                                        .type("SHIPPING")
                                        .discount(shippingDiscount)
                                        .description(voucher.getDescription())
                                        .build());
                            }
                        } else {
                            warnings.add("Voucher " + shippingVoucherCode + " đã hết hạn hoặc không còn hiệu lực");
                        }
                    } catch (ResourceNotFoundException e) {
                        warnings.add(e.getMessage());
                    }
                }
            }
        }

        // 4. Tính tổng
        int subtotalAfterDiscount = Math.max(0, subtotal - orderDiscount);
        int shippingFinal = Math.max(0, shippingOriginal - shippingDiscount);
        int grandTotal = subtotalAfterDiscount + shippingFinal;

        return CheckoutPreviewResponse.builder()
                .subtotal(subtotal)
                .shippingOriginal(shippingOriginal)
                .orderDiscount(orderDiscount)
                .subtotalAfterDiscount(subtotalAfterDiscount)
                .shippingDiscount(shippingDiscount)
                .shippingFinal(shippingFinal)
                .grandTotal(grandTotal)
                .appliedVouchers(appliedVouchers)
                .warnings(warnings)
                .build();
    }

    @Override
    public List<ShippingDistrictRule> getAvailableDistricts(String city) {
        // Sử dụng query mới hỗ trợ cả active = TRUE và active IS NULL
        return ruleRepository.findByCityAndActiveOrNull(city);
    }

    @Override
    public List<String> getDistrictNames(String city) {
        // Sử dụng query mới hỗ trợ cả active = NULL
        return ruleRepository.findDistinctDistrictsByCityOrNull(city);
    }

    @Override
    public boolean isDistrictSupported(String city, String district) {
        // Sử dụng query mới hỗ trợ cả active = NULL
        return ruleRepository.existsByCityAndDistrictActiveOrNull(city, district);
    }

    /**
     * Tính phí vận chuyển gốc (chưa áp miễn phí)
     */
    private int calculateOriginalFee(ShippingDistrictRule rule, boolean isPeakHour, boolean isHoliday) {
        int fee = rule.getBaseFee();

        // Cộng phí cao điểm
        if (isPeakHour && rule.getPeakFee() != null && rule.getPeakFee() > 0) {
            fee += rule.getPeakFee();
        }

        // Nhân hệ số ngày lễ
        if (isHoliday && rule.getHolidayMultiplier() != null) {
            fee = BigDecimal.valueOf(fee)
                    .multiply(rule.getHolidayMultiplier())
                    .intValue();
        }

        return fee;
    }

    /**
     * Kiểm tra có phải giờ cao điểm không
     */
    private boolean isPeakHour() {
        LocalTime now = LocalTime.now();
        return (now.isAfter(PEAK_HOUR_1_START) && now.isBefore(PEAK_HOUR_1_END))
                || (now.isAfter(PEAK_HOUR_2_START) && now.isBefore(PEAK_HOUR_2_END));
    }

    /**
     * Kiểm tra có phải ngày lễ không (placeholder - có thể mở rộng)
     */
    private boolean isHoliday() {
        // TODO: Implement holiday check (có thể dùng bảng holidays hoặc API)
        return false;
    }
}
