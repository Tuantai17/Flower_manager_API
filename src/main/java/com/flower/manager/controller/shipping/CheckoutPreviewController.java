package com.flower.manager.controller.shipping;

import com.flower.manager.dto.shipping.CheckoutPreviewRequest;
import com.flower.manager.dto.shipping.CheckoutPreviewResponse;
import com.flower.manager.service.shipping.ShippingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller xử lý API preview checkout (tính tổng tiền với voucher)
 */
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Checkout Preview", description = "API preview checkout với voucher")
public class CheckoutPreviewController {

    private final ShippingService shippingService;

    /**
     * Preview checkout - tính tổng tiền với voucher ORDER và SHIPPING
     */
    @PostMapping("/preview")
    @Operation(summary = "Preview checkout", description = "Tính tổng tiền thực với voucher giảm giá và voucher ship")
    public ResponseEntity<Map<String, Object>> previewCheckout(
            @Valid @RequestBody CheckoutPreviewRequest request) {

        log.info("Preview checkout for district: {}, subtotal: {}, vouchers: {}",
                request.getDistrict(),
                request.getSubtotal(),
                request.getVouchers());

        CheckoutPreviewResponse result = shippingService.previewCheckout(request);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);

        return ResponseEntity.ok(response);
    }
}
