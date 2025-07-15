package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;

public interface ReportService {
    DashboardReportDTO generateDashboardReport();
    DashboardReportDTO.OverviewStats getOverviewStats();
    Object generateBookingsReport();
    Object generateFinancialsReport();
    Object generateUsersReport();
    Object generateConsultantsReport();
    Object generateServicesReport();
}
