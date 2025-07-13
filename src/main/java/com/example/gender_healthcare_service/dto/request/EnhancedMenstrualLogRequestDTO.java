package com.example.gender_healthcare_service.dto.request;

import com.example.gender_healthcare_service.entity.MenstrualLog;
import com.example.gender_healthcare_service.entity.enumpackage.FlowIntensity;
import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedMenstrualLogRequestDTO {
    private LocalDateTime logDate;
    private Boolean isActualPeriod;
    private FlowIntensity flowIntensity;
    private MoodType mood;
    private Double temperature;
    private List<SymptomEntryDTO> symptoms;
    private String notes;
    private LocalDate OvulationDate;
    private Integer PeriodDuration;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymptomEntryDTO {
        private Integer symptomId;
        private String symptomName;
        private String severity; // MILD, MODERATE, SEVERE
        private String notes;
    }
}
