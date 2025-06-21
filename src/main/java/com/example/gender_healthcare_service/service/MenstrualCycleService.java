package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;

public interface MenstrualCycleService {
    MenstrualCycleResponseDTO saveMenstrualCycle(Integer userId, MenstrualCycleRequestDTO requestDTO);
    MenstrualCycleResponseDTO getMenstrualCycle(Integer userId);
}
