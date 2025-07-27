package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConsultantCreateBookingRequestDTO {

    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotNull(message = "Service ID is required")
    private Integer serviceId;

    @NotNull(message = "Time slot ID is required")
    private Integer timeSlotId;

    @NotNull(message = "Booking date is required")
    @FutureOrPresent(message = "Booking date must be in the future or present")
    private LocalDateTime bookingDate;

    private String description;
    private String notes;
} 