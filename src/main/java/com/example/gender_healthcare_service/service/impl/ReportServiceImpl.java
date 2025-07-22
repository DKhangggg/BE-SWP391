package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.TestingServiceRepository;
import com.example.gender_healthcare_service.repository.PaymentRepository;
import com.example.gender_healthcare_service.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ConsultantRepository consultantRepository;
    private final TestingServiceRepository testingServiceRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public DashboardReportDTO getDashboardStats(int month, int year) {
        long totalConsultants = consultantRepository.count();
        long pendingConsultants = 0; // Không đếm theo status
        long totalTestingServices = testingServiceRepository.count();
        long totalRevenue = paymentRepository.sumRevenueByMonthAndYear(month, year);
        return new DashboardReportDTO(totalConsultants, pendingConsultants, totalTestingServices, totalRevenue);
    }
}
