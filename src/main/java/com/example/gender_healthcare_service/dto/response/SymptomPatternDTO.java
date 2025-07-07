package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SymptomPatternDTO {
    private String symptomName;
    private String category;
    private Integer frequency; // How often it occurs
    private Double averageSeverity; // Average severity level
    private List<LocalDate> occurrenceDates;
    private String pattern; // e.g., "Occurs 2 days before period", "Random occurrence"
    private String cyclePhase; // "MENSTRUAL", "FOLLICULAR", "OVULATION", "LUTEAL"
    private String correlation; // Correlation with other symptoms or cycle events
}
