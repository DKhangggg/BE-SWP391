package com.example.gender_healthcare_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MenstrualCycleResponseDTO {
    private Integer id;
    private Integer userId;
    private LocalDate lastPeriodStartDate;
    private int cycleLength;
    private LocalDate nextPeriodStartDate;
    private LocalDate fertileWindowStartDate;
    private LocalDate fertileWindowEndDate;
    private LocalDate ovulationDate;
}
