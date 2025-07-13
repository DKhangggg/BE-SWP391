package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationRequestDTO {
    @NotNull(message = "Consultant ID is required")
    private Integer consultantId;
    
    @NotNull(message = "Time slot ID is required")
    private Integer timeSlotId;
    
    private String notes;
    private String meetingLink;
} 