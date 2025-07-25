package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DayLogResponseDTO {
    private LocalDate date;
    private Boolean isPeriodDay;
    private String intensity;
    private String symptoms;
    private String mood;
    private String notes;
    private String phase; // PERIOD, OVULATION, FERTILE, PREDICTED
} 