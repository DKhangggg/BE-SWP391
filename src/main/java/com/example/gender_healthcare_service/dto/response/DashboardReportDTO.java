package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardReportDTO {
    private long totalConsultants;
    private long pendingConsultants;
    private long totalTestingServices;
    private long totalRevenue;
}
