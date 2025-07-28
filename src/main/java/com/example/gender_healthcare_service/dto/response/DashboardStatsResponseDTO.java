package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponseDTO {
    private int totalConsultations;
    private int totalSTITests;
    private int totalQuestions;
    private int newNotifications;
    private int upcomingAppointments;
    private String cycleStatus;
    private String ovulationStatus;
} 