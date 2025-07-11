package com.example.gender_healthcare_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class MenstrualCycleRequestDTO {
    private LocalDate startDate;
    private LocalDate periodDay;
}

