package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrentCycleDTO {
    private PeriodPredictionDTO periodPrediction;
    private FertilityWindowDTO fertilityWindow;
    private CycleAnalyticsDTO cycleAnalytics;
    private List<MenstrualLogResponseDTO> recentLogs;
    private String message;
}
