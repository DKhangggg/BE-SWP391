package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhaseInfoDTO {
    private String phase;
    private String icon;
    private String color;
    private String description;
    private String vietnameseName;
} 