package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageResponseDTO {
    private Integer id;
    private Integer conversationId;
    
    private Integer senderId;
    private String senderName;
    private String senderAvatar;
    private String senderRole; // CUSTOMER, CONSULTANT
    
    private String content;
    private String messageType; // TEXT, IMAGE, FILE
    
    private String attachmentUrl;
    private String fileName;
    private Long fileSize;
    
    private String status; // SENT, DELIVERED, READ
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    
    // Thông tin bổ sung
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private Boolean isDeleted;
} 