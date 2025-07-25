package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuickLogRequestDTO {
    private LocalDate date;
    private String type; // SYMPTOMS, MOOD, NOTES
    private String content;
} 