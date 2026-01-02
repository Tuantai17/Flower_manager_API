package com.flower.manager.repository;

import com.flower.manager.entity.NewsletterSubscriber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho NewsletterSubscriber
 */
@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {

    /**
     * Kiểm tra email đã đăng ký chưa
     */
    boolean existsByEmail(String email);

    /**
     * Tìm subscriber theo email
     */
    Optional<NewsletterSubscriber> findByEmail(String email);

    /**
     * Đếm số subscriber đang active
     */
    long countByIsActiveTrue();
}
