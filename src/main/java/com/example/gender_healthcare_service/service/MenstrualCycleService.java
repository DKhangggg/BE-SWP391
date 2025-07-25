package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.CreateMenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateDayLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuickLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateCycleSettingsRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.dto.response.DayLogResponseDTO;
import com.example.gender_healthcare_service.dto.response.PhaseInfoDTO;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;
import com.example.gender_healthcare_service.entity.MenstrualLog;

public interface MenstrualCycleService {
    MenstrualCycleResponseDTO getCurrentMenstrualCycle(Authentication authentication);
    List<MenstrualCycleResponseDTO> createMenstrualCycle(Authentication authentication, List<CreateMenstrualCycleRequestDTO> requests);
    MenstrualCycleResponseDTO updateMenstrualCycle(Integer userId, MenstrualCycleRequestDTO request);
    void deleteMenstrualCycle(Integer userId, Integer cycleId);
    
    DayLogResponseDTO getDayLog(Authentication authentication, String date);
    DayLogResponseDTO updateDayLog(Authentication authentication, UpdateDayLogRequestDTO request);
    DayLogResponseDTO quickLog(Authentication authentication, QuickLogRequestDTO request);
    
    String calculatePhaseForDate(Authentication authentication, LocalDate date);
    Map<LocalDate, String> calculatePhasesForMonth(Authentication authentication, int year, int month);
    Map<LocalDate, PhaseInfoDTO> calculateDetailedPhasesForMonth(Authentication authentication, int year, int month);
    List<MenstrualLog> getLogsForDateRange(Authentication authentication, LocalDate startDate, LocalDate endDate);
    
    MenstrualCycleResponseDTO updateCycleSettings(Authentication authentication, UpdateCycleSettingsRequestDTO request);
}
