package com.example.gender_healthcare_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMenstrualCycleRequestDTO {
    private String startDate;
    private Integer cycleLength;
    private Integer periodDuration;
} 