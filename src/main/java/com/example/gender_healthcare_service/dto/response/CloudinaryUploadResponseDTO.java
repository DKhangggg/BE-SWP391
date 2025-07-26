package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CloudinaryUploadResponseDTO {
    private String message;
    private String publicId;
    private String url;
    private String secureUrl;
    private String format;
    private long size;
    private int width;
    private int height;
    private String resourceType;
    private String createdAt;
} 