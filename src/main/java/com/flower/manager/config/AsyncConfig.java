package com.flower.manager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration cho Async Task Execution
 * Đảm bảo các @Async methods chạy trong thread pool riêng
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Số thread tối thiểu
        executor.setMaxPoolSize(5); // Số thread tối đa
        executor.setQueueCapacity(100); // Queue cho tasks chờ
        executor.setThreadNamePrefix("AsyncEmail-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();

        log.info("✅ AsyncConfig initialized with ThreadPoolTaskExecutor");
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (throwable, method, objects) -> {
            log.error("❌ Async exception in method '{}': {}",
                    method.getName(), throwable.getMessage(), throwable);
        };
    }
}
