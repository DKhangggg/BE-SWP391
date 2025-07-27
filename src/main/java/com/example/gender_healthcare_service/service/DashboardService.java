package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.DashboardStatsResponseDTO;
import com.example.gender_healthcare_service.dto.response.UpcomingAppointmentResponseDTO;

import java.util.List;

public interface DashboardService {
    DashboardStatsResponseDTO getDashboardStats(Integer userId);
    List<UpcomingAppointmentResponseDTO> getUpcomingAppointments(Integer userId);
} 