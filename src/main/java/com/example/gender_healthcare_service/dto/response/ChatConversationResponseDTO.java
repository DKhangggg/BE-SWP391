package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatConversationResponseDTO {
    private Integer id;
    
    // Thông tin customer
    private Integer customerId;
    private String customerName;
    private String customerAvatar;
    
    // Thông tin consultant
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;
    private String consultantSpecialization;
    
    // Thông tin tin nhắn cuối
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    
    // Số tin nhắn chưa đọc
    private Integer customerUnreadCount;
    private Integer consultantUnreadCount;
    
    // Trạng thái conversation
    private String status; // ACTIVE, ARCHIVED, BLOCKED
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Danh sách tin nhắn (optional)
    private List<ChatMessageResponseDTO> messages;
    
    // Thông tin bổ sung
    private Boolean isActive;
    private Integer totalMessages;
}
