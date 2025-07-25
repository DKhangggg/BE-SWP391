package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatConversationResponseDTO {
    private Integer id;
    private Integer userId;
    private String userName;
    private String userAvatar;
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 