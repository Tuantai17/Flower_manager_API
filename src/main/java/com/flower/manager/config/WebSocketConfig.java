package com.flower.manager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket Configuration for Live Chat
 * Uses STOMP protocol over SockJS for browser compatibility
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enable simple broker for subscriptions
        // /topic - for broadcasting to multiple subscribers
        // /queue - for point-to-point messaging
        config.enableSimpleBroker("/topic", "/queue");

        // Application destination prefix for @MessageMapping
        config.setApplicationDestinationPrefixes("/app");

        // User destination prefix for private messages
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket endpoint for chat
        registry.addEndpoint("/ws/chat")
                .setAllowedOriginPatterns("*") // Allow all origins for development
                .withSockJS(); // Enable SockJS fallback
    }
}
