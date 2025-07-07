package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;

import java.time.LocalDate;

public interface ReportService {
    DashboardReportDTO generateDashboardReport(LocalDate startDate, LocalDate endDate);

    DashboardReportDTO.OverviewStats getOverviewStats();

    Object generateBookingsReport(LocalDate startDate, LocalDate endDate, String period);

    Object generateFinancialsReport(LocalDate startDate, LocalDate endDate, String period);

    Object generateUsersReport(LocalDate startDate, LocalDate endDate, String period);

    Object generateConsultantsReport();

    Object generateServicesReport();
}
