package com.flower.manager.config;

import com.flower.manager.service.article.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * A7) SCHEDULED JOB - Tu dong publish bai viet da len lich
 * 
 * Chay moi phut de kiem tra cac bai viet co:
 * - status = SCHEDULED
 * - scheduled_at <= now
 * 
 * Va tu dong chuyen sang:
 * - status = PUBLISHED
 * - published_at = now
 * - scheduled_at = null
 */
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class ArticleScheduler {

    private final ArticleService articleService;

    /**
     * Chay moi phut (giay 0 cua moi phut)
     * Cron: "0 * * * * *" = Moi phut tai giay 0
     */
    @Scheduled(cron = "0 * * * * *")
    public void publishScheduledArticles() {
        try {
            int publishedCount = articleService.publishScheduledArticles();
            if (publishedCount > 0) {
                log.info("ArticleScheduler: Published {} scheduled article(s)", publishedCount);
            }
        } catch (Exception e) {
            log.error("ArticleScheduler: Error publishing scheduled articles", e);
        }
    }

    /**
     * Log startup message (chay 1 lan khi app start)
     */
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE)
    public void logStartup() {
        log.info("ArticleScheduler initialized - Running every minute to check scheduled articles");
    }
}
