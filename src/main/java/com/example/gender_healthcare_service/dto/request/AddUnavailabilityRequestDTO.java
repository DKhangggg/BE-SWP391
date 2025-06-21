package com.example.gender_healthcare_service.dto.request;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUnavailabilityRequestDTO {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reason; // Optional
}

