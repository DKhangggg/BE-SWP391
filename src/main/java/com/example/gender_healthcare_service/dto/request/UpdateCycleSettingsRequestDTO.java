package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class UpdateCycleSettingsRequestDTO {
    private Integer cycleLength;
    private Integer periodDuration;
} 