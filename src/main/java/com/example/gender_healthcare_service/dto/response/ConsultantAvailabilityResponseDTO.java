package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultantAvailabilityResponseDTO {
    private Integer slotId;
    private Integer consultantId;
    private String consultantName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isAvailable;
}
