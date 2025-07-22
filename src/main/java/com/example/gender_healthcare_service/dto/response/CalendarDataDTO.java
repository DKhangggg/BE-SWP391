package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDataDTO {
    private PeriodPredictionDTO periodPrediction;
    private FertilityWindowDTO fertilityWindow;
    private List<MenstrualLogResponseDTO> logs;
    private Integer year;
    private Integer month;
    private String message;
}
