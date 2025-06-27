package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConsultationStatusUpdateDTO {

    @NotBlank(message = "Status is required")
    private String status; // e.g., "CONFIRMED", "COMPLETED", "CANCELLED"

    private String notes;
}
