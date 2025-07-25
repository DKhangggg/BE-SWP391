package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualCycleResponseDTO {
    private LocalDate startDate;
    private Integer cycleLength;
    private Integer periodDuration;
    private Double averageCycleLength;
    private Double averagePeriodDuration;
    private LocalDate periodDay;
    private LocalDate nextPredictedPeriod;
    private LocalDate fertilityWindowStart;
    private LocalDate fertilityWindowEnd;
    private LocalDate ovulationDate;
}
