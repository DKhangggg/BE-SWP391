package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationBookingResponseDTO {
    private Long id;
    private Long userId;
    private String userName;
    private Long consultantId;
    private String consultantName;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private String notes;
    private String adminNotes;

}

