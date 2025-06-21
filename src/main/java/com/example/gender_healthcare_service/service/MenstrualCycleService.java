package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleTrackerResponseDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualLogResponseDTO;

import java.util.List;

public interface MenstrualCycleService {
    MenstrualCycleResponseDTO addOrUpdateMenstrualCycle(MenstrualCycleRequestDTO requestDTO);
    void logMenstrualPeriod(MenstrualLogRequestDTO logDTO);
    List<MenstrualLogResponseDTO> getMenstrualLogs(Integer cycleId);
    MenstrualCycleTrackerResponseDTO getMenstrualCycleTracker();
}
