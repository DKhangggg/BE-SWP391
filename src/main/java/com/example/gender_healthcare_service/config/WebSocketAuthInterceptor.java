package com.example.gender_healthcare_service.config;

import com.example.gender_healthcare_service.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("🔌 WebSocket CONNECT attempt");
            
            // Get Authorization header
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.info("📋 Auth header: {}", authHeader != null ? "Present" : "Missing");
            
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                
                try {
                    // Validate token
                    if (jwtService.validateToken(token)) {
                        String username = jwtService.getUserNameFromJWT(token);
                        log.info("✅ Valid token for user: {}", username);
                        
                        // Load user details
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        // Create authentication
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        
                        // Set user in accessor
                        accessor.setUser(authentication);
                        log.info("✅ WebSocket authentication successful for user: {}", username);
                    } else {
                        log.warn("❌ Invalid JWT token for WebSocket connection");
                    }
                } catch (Exception e) {
                    log.error("❌ Error authenticating WebSocket connection: {}", e.getMessage());
                }
            } else {
                log.warn("⚠️ No Authorization header in WebSocket connection");
            }
        }
        
        return message;
    }
}
