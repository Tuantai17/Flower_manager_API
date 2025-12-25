package com.flower.manager.service.payment;

import com.flower.manager.dto.payment.MomoPaymentResponse;
import com.flower.manager.entity.Order;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service tích hợp thanh toán MoMo
 * Sử dụng MoMo API v2
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoMoService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${momo.partnerCode}")
    private String partnerCode;

    @Value("${momo.accessKey}")
    private String accessKey;

    @Value("${momo.secretKey}")
    private String secretKey;

    @Value("${momo.endpoint}")
    private String endpoint;

    @Value("${momo.returnUrl}")
    private String returnUrl;

    @Value("${momo.notifyUrl}")
    private String notifyUrl;

    /**
     * Tạo yêu cầu thanh toán MoMo từ Order ID
     */
    public MomoPaymentResponse createPaymentFromOrder(Long orderId, String momoType) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Đơn hàng không tồn tại"));

        if (order.getIsPaid()) {
            throw new BusinessException("ORDER_ALREADY_PAID", "Đơn hàng đã được thanh toán");
        }

        return createPayment(
                order.getFinalPrice().longValue(),
                order.getOrderCode(),
                "Thanh toán đơn hàng " + order.getOrderCode(),
                momoType);
    }

    /**
     * Tạo yêu cầu thanh toán MoMo
     */
    public MomoPaymentResponse createPayment(Long amount, String orderId, String orderInfo, String momoType) {
        try {
            String requestId = String.valueOf(System.currentTimeMillis());
            String extraData = "";

            // 🔥 Xử lý requestType theo momoType
            String requestType = "captureWallet"; // Mặc định là Ví (QR)
            if ("CARD".equalsIgnoreCase(momoType)) {
                requestType = "payWithATM"; // Thẻ ATM / Visa / Master
            }

            // Tạo raw signature theo thứ tự alphabet (MoMo v2 requirement)
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + notifyUrl +
                    "&orderId=" + orderId +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode +
                    "&redirectUrl=" + returnUrl +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            log.debug("MoMo Raw Hash: {}", rawHash);
            String signature = hmacSHA256(secretKey, rawHash);

            // Tạo payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("partnerCode", partnerCode);
            payload.put("partnerName", "Flower Shop");
            payload.put("storeId", partnerCode);
            payload.put("requestId", requestId);
            payload.put("amount", amount);
            payload.put("orderId", orderId);
            payload.put("orderInfo", orderInfo);
            payload.put("redirectUrl", returnUrl);
            payload.put("ipnUrl", notifyUrl);
            payload.put("lang", "vi");
            payload.put("extraData", extraData);
            payload.put("requestType", requestType);
            payload.put("signature", signature);

            log.info("Creating MoMo payment for order: {}, amount: {}", orderId, amount);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<MomoPaymentResponse> response = restTemplate.postForEntity(
                    endpoint, entity, MomoPaymentResponse.class);

            MomoPaymentResponse momoResponse = response.getBody();

            if (momoResponse != null && momoResponse.getResultCode() != 0) {
                log.error("MoMo payment error: {} - {}", momoResponse.getResultCode(), momoResponse.getMessage());
                throw new BusinessException("MOMO_ERROR", "Lỗi thanh toán MoMo: " + momoResponse.getMessage());
            }

            log.info("MoMo payment URL created successfully for order: {}", orderId);
            return momoResponse;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating MoMo payment: {}", e.getMessage(), e);
            throw new BusinessException("MOMO_ERROR", "Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xác thực chữ ký callback từ MoMo (dùng cho IPN - Map<String, Object>)
     */
    public boolean verifyIpnSignature(Map<String, Object> data) {
        Map<String, String> stringData = new HashMap<>();
        data.forEach((k, v) -> stringData.put(k, v != null ? v.toString() : ""));
        return verifySignatureParams(stringData);
    }

    private boolean verifySignatureParams(Map<String, String> data) {
        try {
            String receivedSignature = (String) data.get("signature");
            if (receivedSignature == null) {
                return false;
            }

            // Tạo raw data để verify (theo thứ tự alphabet)
            String rawHash = "accessKey=" + accessKey +
                    "&amount=" + data.get("amount") +
                    "&extraData=" + data.getOrDefault("extraData", "") +
                    "&message=" + data.get("message") +
                    "&orderId=" + data.get("orderId") +
                    "&orderInfo=" + data.get("orderInfo") +
                    "&orderType=" + data.getOrDefault("orderType", "") +
                    "&partnerCode=" + data.get("partnerCode") +
                    "&payType=" + data.getOrDefault("payType", "") +
                    "&requestId=" + data.get("requestId") +
                    "&responseTime=" + data.get("responseTime") +
                    "&resultCode=" + data.get("resultCode") +
                    "&transId=" + data.get("transId");

            String calculatedSignature = hmacSHA256(secretKey, rawHash);
            return calculatedSignature.equals(receivedSignature);

        } catch (Exception e) {
            log.error("Error verifying MoMo signature: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Tạo HMAC SHA256 signature
     */
    private String hmacSHA256(String key, String data) throws Exception {
        Mac hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmac.init(secretKeySpec);
        byte[] hash = hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
