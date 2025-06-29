package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualCycleResponseDTO {
    private LocalDate startDate;
    private LocalDate LastTimeTakePeriod;
    private LocalDate predictedPeriodDay;
}

