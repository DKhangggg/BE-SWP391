package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

@Data
public class AvailableConsultantResponseDTO {
    private Integer consultantId;
    private String consultantName;
    private String consultantAvatar;
    private String consultantSpecialization;
    private String status;
} 