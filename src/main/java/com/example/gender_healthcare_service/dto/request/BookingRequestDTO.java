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
public class BookingRequestDTO {
    @NotNull(message = "Service ID is required")
    private Integer serviceId;
    
    @NotNull(message = "Time slot ID is required")
    private Integer timeSlotId;

    private String description;
    private String notes;
}

