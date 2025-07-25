package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.service.ChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class ChatWebSocketController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessageRequestDTO chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        try {
            // Lấy user từ WebSocket session
            String username = headerAccessor.getUser().getName();
            log.info("Received message from {}: {}", username, chatMessage.getContent());

            // Gửi tin nhắn qua service (sẽ lưu vào DB và gửi qua WebSocket)
            ChatMessageResponseDTO messageDTO = chatService.sendMessage(null, chatMessage);

            // Gửi tin nhắn đến người nhận
            messagingTemplate.convertAndSendToUser(
                    chatMessage.getReceiverId().toString(),
                    "/queue/chat",
                    messageDTO
            );

            // Gửi tin nhắn xác nhận cho người gửi
            messagingTemplate.convertAndSendToUser(
                    username,
                    "/queue/chat/confirm",
                    messageDTO
            );

        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            
            // Gửi lỗi về cho người gửi
            messagingTemplate.convertAndSendToUser(
                    headerAccessor.getUser().getName(),
                    "/queue/chat/error",
                    "Lỗi khi gửi tin nhắn: " + e.getMessage()
            );
        }
    }

    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload String receiverId, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        
        // Gửi thông báo typing đến người nhận
        messagingTemplate.convertAndSendToUser(
                receiverId,
                "/queue/chat/typing",
                username + " đang nhập tin nhắn..."
        );
    }

    @MessageMapping("/chat.read")
    public void markAsRead(@Payload Integer senderId, SimpMessageHeaderAccessor headerAccessor) {
        try {
            String username = headerAccessor.getUser().getName();
            log.info("User {} marking messages from {} as read", username, senderId);

            // Đánh dấu tin nhắn đã đọc
            chatService.markMessagesAsRead(null, senderId);

            // Gửi thông báo đã đọc đến người gửi
            messagingTemplate.convertAndSendToUser(
                    senderId.toString(),
                    "/queue/chat/read",
                    username + " đã đọc tin nhắn"
            );

        } catch (Exception e) {
            log.error("Error marking messages as read: {}", e.getMessage());
        }
    }
} 