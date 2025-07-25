package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDTO {
    private Integer id;
    private Integer senderId;
    private String senderName;
    private String senderRole;
    private Integer receiverId;
    private String receiverName;
    private String content;
    private String messageType;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
} 