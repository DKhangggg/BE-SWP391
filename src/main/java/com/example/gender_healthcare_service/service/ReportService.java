package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;

import java.time.LocalDateTime;

public interface ReportService {
    DashboardReportDTO generateDashboardReport(LocalDateTime startDate, LocalDateTime endDate);

    DashboardReportDTO.OverviewStats getOverviewStats();

    Object generateBookingsReport(LocalDateTime startDate, LocalDateTime endDate, String period);

    Object generateFinancialsReport(LocalDateTime startDate, LocalDateTime endDate, String period);

    Object generateUsersReport(LocalDateTime startDate, LocalDateTime endDate, String period);

    Object generateConsultantsReport();

    Object generateServicesReport();
}
