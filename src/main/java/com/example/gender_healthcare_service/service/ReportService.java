package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;

public interface ReportService {
    DashboardReportDTO getDashboardStats(int month, int year);
}
