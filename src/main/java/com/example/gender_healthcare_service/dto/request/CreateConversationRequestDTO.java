package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateConversationRequestDTO {
    
    @NotNull(message = "ID của consultant không được để trống")
    private Integer consultantId;
    
    // Optional: tin nhắn đầu tiên
    private String initialMessage;
} 