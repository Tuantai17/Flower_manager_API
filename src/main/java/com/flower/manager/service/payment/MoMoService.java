package com.flower.manager.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Formatter;

/**
 * Service tích hợp thanh toán MoMo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MoMoService {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${momo.partnerCode:MOMOBKUN20180529}")
    private String partnerCode;

    @Value("${momo.accessKey:F8BBA842ECF85}")
    private String accessKey;

    @Value("${momo.secretKey:K951B6PE1waDMi640xX08PD3vg6EkVlz}")
    private String secretKey;

    @Value("${momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String endpoint;

    @Value("${momo.returnUrl:http://localhost:3000/payment-return}")
    private String returnUrl;

    @Value("${momo.notifyUrl:http://localhost:8080/api/payment/momo-callback}")
    private String notifyUrl;

    /**
     * Tạo URL thanh toán MoMo
     */
    public String createPaymentUrl(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException("ORDER_NOT_FOUND", "Đơn hàng không tồn tại"));

        if (order.getIsPaid()) {
            throw new BusinessException("ORDER_ALREADY_PAID", "Đơn hàng đã được thanh toán");
        }

        try {
            long amount = order.getFinalPrice().longValue();
            String orderInfo = "PayOrder" + order.getOrderCode(); // Bỏ dấu cách để tránh lỗi encoding
            String requestId = String.valueOf(System.currentTimeMillis());
            String orderId_str = order.getOrderCode();
            String extraData = "";
            String requestType = "captureWallet";

            // Tạo raw signature string theo đúng thứ tự Alphabet của MoMo (V2)
            // accessKey=&amount=&extraData=&ipnUrl=&orderId=&orderInfo=&partnerCode=&redirectUrl=&requestId=&requestType=
            String rawHash = "accessKey=" + accessKey.trim() +
                    "&amount=" + amount +
                    "&extraData=" + extraData +
                    "&ipnUrl=" + notifyUrl.trim() +
                    "&orderId=" + orderId_str +
                    "&orderInfo=" + orderInfo +
                    "&partnerCode=" + partnerCode.trim() +
                    "&redirectUrl=" + returnUrl.trim() +
                    "&requestId=" + requestId +
                    "&requestType=" + requestType;

            log.info("MoMo Raw Hash for signing: [{}]", rawHash);
            String signature = hmacSHA256(secretKey.trim(), rawHash);

            // Tạo Map cho request body
            java.util.Map<String, Object> bodyMap = new java.util.LinkedHashMap<>();
            bodyMap.put("partnerCode", partnerCode);
            bodyMap.put("requestId", requestId);
            bodyMap.put("amount", amount);
            bodyMap.put("orderId", orderId_str);
            bodyMap.put("orderInfo", orderInfo);
            bodyMap.put("redirectUrl", returnUrl);
            bodyMap.put("ipnUrl", notifyUrl);
            bodyMap.put("lang", "vi");
            bodyMap.put("extraData", extraData);
            bodyMap.put("requestType", requestType);
            bodyMap.put("signature", signature);

            String body = objectMapper.writeValueAsString(bodyMap);
            log.info("MoMo Request Body: {}", body);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);

            JsonNode json = objectMapper.readTree(response.getBody());

            int resultCode = json.get("resultCode").asInt();
            if (resultCode != 0) {
                String message = json.get("message").asText();
                log.error("MoMo payment error: {} - {}", resultCode, message);
                throw new BusinessException("MOMO_ERROR", "Lỗi thanh toán MoMo: " + message);
            }

            String payUrl = json.get("payUrl").asText();
            log.info("Created MoMo payment URL for order: {}", order.getOrderCode());
            return payUrl;

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating MoMo payment: {}", e.getMessage());
            throw new BusinessException("MOMO_ERROR", "Không thể tạo thanh toán MoMo: " + e.getMessage());
        }
    }

    /**
     * Xác thực callback từ MoMo
     */
    public boolean verifyCallback(String rawData, String signature) {
        try {
            String calculatedSignature = hmacSHA256(secretKey, rawData);
            return calculatedSignature.equals(signature);
        } catch (Exception e) {
            log.error("Error verifying MoMo callback: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Tạo HMAC SHA256 signature
     */
    private String hmacSHA256(String key, String data) throws Exception {
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] hash = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
