package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class UnavailabilityRequest {
    private Integer consultantId;
    private String startDate;
    private String endDate;
    private String reason;
}
