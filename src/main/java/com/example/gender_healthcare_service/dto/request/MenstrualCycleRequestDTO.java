package com.example.gender_healthcare_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MenstrualCycleRequestDTO {
    private LocalDate lastPeriodStartDate;
    private int cycleLength;
}

