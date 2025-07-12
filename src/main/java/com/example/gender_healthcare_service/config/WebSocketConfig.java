package com.example.gender_healthcare_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker for broadcasting messages to clients
        registry.enableSimpleBroker("/topic", "/queue");
        
        // Set application destination prefix for messages that are bound for methods annotated with @MessageMapping
        registry.setApplicationDestinationPrefixes("/app");
        
        // Set user destination prefix for private messages
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoint for WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Allow all origins (adjust for production)
                .withSockJS(); // Enable SockJS fallback options
        
        // Additional endpoint for booking tracking
        registry.addEndpoint("/booking-tracking")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
