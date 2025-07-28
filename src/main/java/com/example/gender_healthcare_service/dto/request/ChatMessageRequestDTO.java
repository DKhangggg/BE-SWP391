package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChatMessageRequestDTO {
    
    @NotNull(message = "ID conversation không được để trống")
    private Integer conversationId;
    
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String content;
    
    private String messageType = "TEXT"; // TEXT, IMAGE, FILE
}
