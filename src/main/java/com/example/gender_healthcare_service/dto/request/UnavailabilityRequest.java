package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class UnavailabilityRequest {
    private Integer consultantId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
}
