package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilityWindowDTO {
    private LocalDate fertileWindowStart;
    private LocalDate fertileWindowEnd;
    private LocalDate ovulationDate;
    private Double ovulationConfidence;
    private String fertilityStatus; // "HIGH", "MEDIUM", "LOW"
    private String notes;
}
