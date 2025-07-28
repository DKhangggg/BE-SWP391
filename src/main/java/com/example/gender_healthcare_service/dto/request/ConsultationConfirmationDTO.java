package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConsultationConfirmationDTO {

    @NotNull(message = "Consultation status is required")
    private String status; // CONFIRMED, REJECTED, CANCELLED

    // meetingLink chỉ bắt buộc khi status là CONFIRMED
    private String meetingLink;

    private String notes;

    private String meetingPassword;

    private String meetingPlatform; // ZOOM, GOOGLE_MEET, TEAMS, etc.
} 