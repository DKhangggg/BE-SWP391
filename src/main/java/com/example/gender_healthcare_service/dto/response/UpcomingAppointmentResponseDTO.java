package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpcomingAppointmentResponseDTO {
    private Integer id;
    private String type; // CONSULTATION hoặc BOOKING
    private String title;
    private String description;
    private LocalDateTime appointmentDate;
    private String status;
    private String consultantName;
    private String serviceName;
    private String meetingLink;
    private String location;
} 