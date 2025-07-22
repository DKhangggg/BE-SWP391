package com.example.gender_healthcare_service.dto.response;

import com.example.gender_healthcare_service.entity.enumpackage.FlowIntensity;
import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenstrualLogResponseDTO {
    private Integer logId;
    private Integer userId;
    private LocalDateTime logDate;
    private Boolean isActualPeriod;
    private FlowIntensity flowIntensity;
    private MoodType mood;
    private Double temperature;
    private String notes;
    private List<SymptomResponseDTO> symptoms;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SymptomResponseDTO {
        private Integer symptomId;
        private String symptomName;
        private String severity;
        private String notes;
        private String category;
    }
}
