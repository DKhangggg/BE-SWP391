package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    private Integer receiverId;
    private String content;
    private String messageType = "TEXT"; // TEXT, IMAGE, FILE, SYSTEM
} 