package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class ConsultantAvailabilityResponseDTO {
    private Integer slotId;
    private Integer consultantId;
    private String consultantName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String startTimeStr; // Format HH:mm
    private String endTimeStr;   // Format HH:mm
    private Boolean isAvailable;
}
