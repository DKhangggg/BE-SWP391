package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.EnhancedMenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface MenstrualCycleService {
    MenstrualCycleResponseDTO addOrUpdateMenstrualCycle(MenstrualCycleRequestDTO requestDTO);
    void logMenstrualPeriod(MenstrualLogRequestDTO logDTO);
    List<MenstrualLogResponseDTO> getMenstrualLogs(Integer cycleId);
    MenstrualCycleTrackerResponseDTO getMenstrualCycleTracker();

    // Enhanced features
    void logEnhancedMenstrualData(EnhancedMenstrualLogRequestDTO requestDTO);
    PeriodPredictionDTO getPeriodPrediction(Integer userId);
    FertilityWindowDTO getFertilityWindow(Integer userId);
    CycleAnalyticsDTO getCycleAnalytics(Integer userId);
    List<SymptomPatternDTO> getSymptomPatterns(Integer userId);
    List<String> getHealthInsights(Integer userId);
    List<MenstrualLogResponseDTO> getMenstrualLogsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    // Consultant features for managing user menstrual data
    void logMenstrualDataForUser(Integer userId, EnhancedMenstrualLogRequestDTO requestDTO, Integer consultantId);
    MenstrualCycleResponseDTO updateUserMenstrualCycle(Integer userId, MenstrualCycleRequestDTO requestDTO, Integer consultantId);
    List<MenstrualLogResponseDTO> getUserMenstrualHistory(Integer userId, Integer consultantId);
    CycleAnalyticsDTO getUserCycleAnalytics(Integer userId, Integer consultantId);
    PeriodPredictionDTO getUserPeriodPrediction(Integer userId, Integer consultantId);
    void deleteMenstrualLog(Integer logId, Integer consultantId);
    MenstrualLogResponseDTO updateMenstrualLog(Integer logId, EnhancedMenstrualLogRequestDTO requestDTO, Integer consultantId);
}
