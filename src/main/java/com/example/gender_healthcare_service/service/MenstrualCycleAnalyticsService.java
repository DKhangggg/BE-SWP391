package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.CycleAnalyticsDTO;
import com.example.gender_healthcare_service.dto.response.FertilityWindowDTO;
import com.example.gender_healthcare_service.dto.response.PeriodPredictionDTO;
import com.example.gender_healthcare_service.dto.response.SymptomPatternDTO;

import java.time.LocalDate;
import java.util.List;

public interface MenstrualCycleAnalyticsService {

    /**
     * Calculate advanced period prediction based on historical data
     */
    PeriodPredictionDTO predictNextPeriod(Integer userId);

    /**
     * Calculate fertility window based on ovulation prediction
     */
    FertilityWindowDTO calculateFertilityWindow(Integer userId);

    /**
     * Analyze cycle regularity and patterns
     */
    CycleAnalyticsDTO analyzeCyclePatterns(Integer userId);

    /**
     * Find symptom patterns and correlations
     */
    List<SymptomPatternDTO> analyzeSymptomPatterns(Integer userId);

    /**
     * Update cycle statistics and predictions
     */
    void updateCycleStatistics(Integer userId);

    /**
     * Detect irregular cycles
     */
    boolean isIrregularCycle(Integer userId);

    /**
     * Get health insights and recommendations
     */
    List<String> getHealthInsights(Integer userId);

    /**
     * Calculate average cycle length with weighted recent cycles
     */
    Double calculateWeightedAverageCycleLength(Integer userId);
}
