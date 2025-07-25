package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateDayLogRequestDTO {
    private LocalDate date;
    private Boolean isPeriodDay;
    private String intensity; // LIGHT, MEDIUM, HEAVY
    private String symptoms;
    private String mood;
    private String notes;
} 