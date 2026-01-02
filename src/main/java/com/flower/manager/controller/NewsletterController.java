package com.flower.manager.controller;

import com.flower.manager.dto.ApiResponse;
import com.flower.manager.dto.newsletter.NewsletterSubscribeRequest;
import com.flower.manager.dto.newsletter.NewsletterSubscribeResponse;
import com.flower.manager.service.newsletter.NewsletterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller cho Newsletter (Đăng ký nhận tin khuyến mãi)
 * 
 * Endpoint: /api/public/newsletter/**
 * Tất cả endpoints đều public, không cần đăng nhập
 */
@RestController
@RequestMapping("/api/public/newsletter")
@RequiredArgsConstructor
@Slf4j
public class NewsletterController {

    private final NewsletterService newsletterService;

    /**
     * Đăng ký nhận tin khuyến mãi
     * POST /api/public/newsletter/subscribe
     * 
     * Request body: { "email": "user@example.com" }
     * Response: Thông tin voucher welcome được tạo
     */
    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<NewsletterSubscribeResponse>> subscribe(
            @Valid @RequestBody NewsletterSubscribeRequest request) {

        log.info("Newsletter subscription request for email: {}", request.getEmail());

        NewsletterSubscribeResponse response = newsletterService.subscribe(request.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created(response,
                        "Đăng ký thành công! Voucher giảm giá đã được gửi cho bạn."));
    }

    /**
     * Kiểm tra email đã đăng ký chưa
     * GET /api/public/newsletter/check?email=xxx
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestParam String email) {
        boolean isSubscribed = newsletterService.isEmailSubscribed(email);
        return ResponseEntity.ok(ApiResponse.success(isSubscribed));
    }

    /**
     * Hủy đăng ký nhận tin
     * POST /api/public/newsletter/unsubscribe
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<Void>> unsubscribe(
            @Valid @RequestBody NewsletterSubscribeRequest request) {

        newsletterService.unsubscribe(request.getEmail());
        return ResponseEntity.ok(ApiResponse.success(null, "Đã hủy đăng ký nhận tin thành công."));
    }
}
