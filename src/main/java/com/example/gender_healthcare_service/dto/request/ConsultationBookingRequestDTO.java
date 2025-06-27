package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultationBookingRequestDTO {

    @NotNull(message = "Consultant ID is required")
    private Integer consultantId;

    @NotNull(message = "Consultation start time is required")
    @FutureOrPresent(message = "Consultation time must be in the future or present")
    private LocalDateTime startTime;

    @NotNull(message = "Consultation end time is required")
    @FutureOrPresent(message = "Consultation end time must be in the future or present")
    private LocalDateTime endTime;

    private String consultationType; // e.g., "ONLINE", "IN_PERSON"

    private String notes;
}
