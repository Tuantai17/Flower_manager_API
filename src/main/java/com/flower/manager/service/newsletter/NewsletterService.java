package com.flower.manager.service.newsletter;

import com.flower.manager.dto.newsletter.NewsletterSubscribeResponse;

/**
 * Service interface cho Newsletter
 */
public interface NewsletterService {

    /**
     * Đăng ký nhận tin và tạo voucher welcome
     * 
     * @param email Email đăng ký
     * @return Thông tin voucher được tạo
     */
    NewsletterSubscribeResponse subscribe(String email);

    /**
     * Kiểm tra email đã đăng ký chưa
     */
    boolean isEmailSubscribed(String email);

    /**
     * Hủy đăng ký nhận tin
     */
    void unsubscribe(String email);
}
