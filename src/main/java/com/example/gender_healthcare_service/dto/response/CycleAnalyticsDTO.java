package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleAnalyticsDTO {
    private Double averageCycleLength;
    private Double averagePeriodDuration;
    private Boolean isRegular;
    private Double cycleVariability; // Standard deviation
    private Integer totalCyclesTracked;
    private String regularityStatus; // "REGULAR", "IRREGULAR", "INSUFFICIENT_DATA"
    private List<String> trends; // e.g., "Cycles getting longer", "Period duration stable"
    private Map<String, Double> moodPatterns; // Mood frequency analysis
    private Map<String, Integer> symptomFrequency; // Most common symptoms
    private List<String> recommendations;
}
