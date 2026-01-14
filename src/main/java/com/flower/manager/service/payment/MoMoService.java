package com.flower.manager.service.payment;

import com.flower.manager.dto.payment.MomoPaymentResponse;
import com.flower.manager.entity.Order;
import com.flower.manager.enums.ErrorCode;
import com.flower.manager.exception.BusinessException;
import com.flower.manager.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Service tích hợp thanh toán MoMo
 * Sử dụng MoMo API v2
 * 
 * Log convention:
 * - [MOMO:CREATE] - Tạo payment request
 * - [MOMO:CALLBACK] - Xử lý callback (IPN/Return)
 * - [MOMO:VERIFY] - Xác thực chữ ký
 * - [MOMO:ERROR] - Lỗi xảy ra
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
        log.info("[MOMO:CREATE] Starting payment creation for orderId={}, type={}", orderId, momoType);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.error("[MOMO:ERROR] Order not found: orderId={}", orderId);
                    return new BusinessException(ErrorCode.ORDER_NOT_FOUND);
                });

        if (order.getIsPaid()) {
            log.warn("[MOMO:ERROR] Order already paid: orderCode={}", order.getOrderCode());
            throw new BusinessException(ErrorCode.ORDER_ALREADY_PAID);
        }

        log.info("[MOMO:CREATE] Order found: orderCode={}, amount={}",
                order.getOrderCode(), order.getFinalPrice());

        return createPayment(
                order.getFinalPrice().longValue(),
                order.getOrderCode(),
                "Thanh toán đơn hàng " + order.getOrderCode(),
                momoType);
    }

    /**
     * Tạo yêu cầu thanh toán MoMo
     * 
     * momoType options:
     * - "auto" hoặc null: Hiển thị trang chọn phương thức (QR + ATM/Visa)
     * - "wallet": Chỉ QR/Ví MoMo
     * - "card": Chỉ thẻ ATM/Visa/Master
     */
    public MomoPaymentResponse createPayment(Long amount, String orderId, String orderInfo, String momoType) {
        String requestId = String.valueOf(System.currentTimeMillis());

        log.info("[MOMO:CREATE] Preparing request: orderId={}, amount={}, requestId={}, momoType={}",
                orderId, amount, requestId, momoType);

        try {
            String extraData = "";

            // Xác định requestType dựa trên momoType
            // payWithMethod: Hiển thị TRANG CHỌN phương thức (QR + ATM/Visa)
            // captureWallet: Chỉ QR/Ví MoMo
            // payWithATM: Chỉ thẻ ATM/Visa/Master
            String requestType;

            if ("wallet".equalsIgnoreCase(momoType)) {
                requestType = "captureWallet";
                log.info("[MOMO:CREATE] Mode: Wallet only (QR)");
            } else if ("card".equalsIgnoreCase(momoType)) {
                requestType = "payWithATM";
                log.info("[MOMO:CREATE] Mode: Card only (ATM/Visa/Master)");
            } else {
                // Mặc định: Hiển thị trang chọn phương thức thanh toán
                requestType = "payWithMethod";
                log.info("[MOMO:CREATE] Mode: Payment selection page (all methods)");
            }

            // Tạo raw signature theo thứ tự alphabet (MoMo v2 requirement)
            // QUAN TRỌNG: KHÔNG có payType trong signature khi dùng payWithMethod
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

            log.debug("[MOMO:CREATE] Raw hash for signature: {}", rawHash);
            String signature = hmacSHA256(secretKey, rawHash);
            log.debug("[MOMO:CREATE] Generated signature: {}...", signature.substring(0, 20));

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
            // KHÔNG set payType để MoMo hiển thị trang chọn phương thức

            log.info("[MOMO:CREATE] Sending request to MoMo API: endpoint={}, requestType={}", endpoint, requestType);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

            long startTime = System.currentTimeMillis();
            ResponseEntity<MomoPaymentResponse> response = restTemplate.postForEntity(
                    endpoint, entity, MomoPaymentResponse.class);
            long duration = System.currentTimeMillis() - startTime;

            MomoPaymentResponse momoResponse = response.getBody();

            if (momoResponse != null) {
                log.info("[MOMO:CREATE] Response received in {}ms: resultCode={}, message={}",
                        duration, momoResponse.getResultCode(), momoResponse.getMessage());

                if (momoResponse.getResultCode() != 0) {
                    log.error("[MOMO:ERROR] Payment creation failed: orderId={}, resultCode={}, message={}",
                            orderId, momoResponse.getResultCode(), momoResponse.getMessage());
                    throw new BusinessException(ErrorCode.PAYMENT_MOMO_ERROR,
                            "Lỗi thanh toán MoMo: " + momoResponse.getMessage());
                }

                log.info("[MOMO:CREATE] Payment URL created successfully: orderId={}, payUrl={}...",
                        orderId,
                        momoResponse.getPayUrl() != null
                                ? momoResponse.getPayUrl().substring(0, Math.min(50, momoResponse.getPayUrl().length()))
                                : "null");
            }

            return momoResponse;

        } catch (BusinessException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("[MOMO:ERROR] Network error calling MoMo API: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_MOMO_ERROR,
                    "Không thể kết nối đến MoMo: " + e.getMessage());
        } catch (Exception e) {
            log.error("[MOMO:ERROR] Unexpected error creating payment: orderId={}, error={}",
                    orderId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_MOMO_ERROR,
                    "Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xác thực chữ ký callback từ MoMo (dùng cho IPN - Map<String, Object>)
     */
    public boolean verifyIpnSignature(Map<String, Object> data) {
        log.info("[MOMO:VERIFY] Starting IPN signature verification for orderId={}",
                data.get("orderId"));

        Map<String, String> stringData = new HashMap<>();
        data.forEach((k, v) -> stringData.put(k, v != null ? v.toString() : ""));

        boolean isValid = verifySignatureParams(stringData);

        if (isValid) {
            log.info("[MOMO:VERIFY] IPN signature verified successfully: orderId={}",
                    data.get("orderId"));
        } else {
            log.error("[MOMO:VERIFY] IPN signature verification FAILED: orderId={}",
                    data.get("orderId"));
        }

        return isValid;
    }

    /**
     * Xác thực chữ ký từ params (dùng cho cả IPN và redirect)
     */
    private boolean verifySignatureParams(Map<String, String> data) {
        try {
            String receivedSignature = data.get("signature");
            if (receivedSignature == null) {
                log.warn("[MOMO:VERIFY] No signature found in callback data");
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

            log.debug("[MOMO:VERIFY] Raw hash for verification: {}", rawHash);

            String calculatedSignature = hmacSHA256(secretKey, rawHash);
            boolean isValid = calculatedSignature.equals(receivedSignature);

            log.debug("[MOMO:VERIFY] Calculated: {}..., Received: {}..., Match: {}",
                    calculatedSignature.substring(0, 20),
                    receivedSignature.substring(0, Math.min(20, receivedSignature.length())),
                    isValid);

            return isValid;

        } catch (Exception e) {
            log.error("[MOMO:ERROR] Error verifying signature: {}", e.getMessage(), e);
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
