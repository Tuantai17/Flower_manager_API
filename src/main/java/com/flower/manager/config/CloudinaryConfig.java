package com.flower.manager.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Cau hinh Cloudinary SDK
 * Cloudinary la dich vu luu tru anh tren cloud
 * 
 * Cach hoat dong:
 * 1. Khi upload file, Cloudinary se luu file len cloud cua ho
 * 2. Tra ve URL cong khai de truy cap file (CDN toan cau)
 * 3. Ho tro tu dong optimize, resize, crop anh
 */
@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

    @Value("${cloudinary.api-key:}")
    private String apiKey;

    @Value("${cloudinary.api-secret:}")
    private String apiSecret;

    /**
     * Tao bean Cloudinary de su dung trong ung dung
     * Bean nay chi duoc tao khi cac thong tin Cloudinary da duoc cau hinh
     */
    @Bean
    public Cloudinary cloudinary() {
        if (cloudName.isEmpty() || apiKey.isEmpty() || apiSecret.isEmpty()) {
            // Tra ve Cloudinary voi config rong (se khong hoat dong)
            // Nhung van tao bean de tranh loi khi inject
            return new Cloudinary();
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true // Su dung HTTPS
        ));
    }
}
