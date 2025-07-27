package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageRequestDTO {
    
    @NotBlank(message = "Nội dung tin nhắn không được để trống")
    private String content;
    
    @NotNull(message = "Loại tin nhắn không được để trống")
    private String messageType; // TEXT, IMAGE, FILE
    
    private String attachmentUrl; // URL của file đính kèm (nếu có)
    
    private String fileName; // Tên file (nếu có)
    
    private Long fileSize; // Kích thước file (nếu có)
} 