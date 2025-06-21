package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.springframework.stereotype.Service;

@Service
public class MenstrualCycleServiceImpl implements MenstrualCycleService {

    @Override
    public MenstrualCycleResponseDTO saveMenstrualCycle(Integer userId, MenstrualCycleRequestDTO requestDTO) {
        // Logic to save menstrual cycle data will be implemented here
        return null;
    }

    @Override
    public MenstrualCycleResponseDTO getMenstrualCycle(Integer userId) {
        // Logic to retrieve menstrual cycle data will be implemented here
        return null;
    }
}

