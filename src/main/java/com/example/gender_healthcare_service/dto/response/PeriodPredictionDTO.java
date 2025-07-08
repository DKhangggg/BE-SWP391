package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodPredictionDTO {
    private LocalDate nextPeriodDate;
    private LocalDate periodEndDate;
    private Double confidence; // 0.0 to 1.0
    private String predictionMethod; // "historical_average", "weighted_average", "pattern_analysis"
    private Integer predictedCycleLength;
    private Integer predictedPeriodDuration;
    private String reliabilityNote;
}
