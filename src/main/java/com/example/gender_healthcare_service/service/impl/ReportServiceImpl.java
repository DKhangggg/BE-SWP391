package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private TestingServiceRepository testingServiceRepository;

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Override
    public DashboardReportDTO generateDashboardReport() {
        DashboardReportDTO report = new DashboardReportDTO();
        report.setOverviewStats(getOverviewStats());
        return report;
    }

    @Override
    public DashboardReportDTO.OverviewStats getOverviewStats() {
        DashboardReportDTO.OverviewStats stats = new DashboardReportDTO.OverviewStats();
        
        // Tổng số liệu cơ bản
        stats.setTotalUsers(userRepository.count());
        stats.setTotalBookings(bookingRepository.count());
        stats.setTotalConsultants(consultantRepository.count());
        stats.setTotalRevenue(calculateTotalRevenue());
        
        // Thống kê booking theo trạng thái
        stats.setCompletedBookings(bookingRepository.countByStatus("COMPLETED"));
        stats.setPendingBookings(bookingRepository.countByStatus("PENDING"));
        stats.setCancelledBookings(bookingRepository.countByStatus("CANCELLED"));

        return stats;
    }

    @Override
    public Object generateBookingsReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Thống kê tổng quan booking
        report.put("totalBookings", bookingRepository.count());
        report.put("completedBookings", bookingRepository.countByStatus("COMPLETED"));
        report.put("pendingBookings", bookingRepository.countByStatus("PENDING"));
        report.put("cancelledBookings", bookingRepository.countByStatus("CANCELLED"));
        
        return report;
    }

    @Override
    public Object generateFinancialsReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Thống kê tài chính cơ bản
        report.put("totalRevenue", calculateTotalRevenue());
        report.put("totalTransactions", transactionHistoryRepository.count());
        report.put("averageTransactionValue", calculateAverageTransactionValue());
        
        return report;
    }

    @Override
    public Object generateUsersReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Thống kê user cơ bản
        report.put("totalUsers", userRepository.count());
        
        return report;
    }

    @Override
    public Object generateConsultantsReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Thống kê consultant cơ bản
        report.put("totalConsultants", consultantRepository.count());
        
        return report;
    }

    @Override
    public Object generateServicesReport() {
        Map<String, Object> report = new HashMap<>();
        
        // Thống kê service cơ bản
        report.put("totalServices", testingServiceRepository.count());
        
        return report;
    }

    // Helper methods
    private Double calculateTotalRevenue() {
        // Mock implementation - có thể thay bằng query thực tế sau
        return 50000.0;
    }

    private Double calculateAverageTransactionValue() {
        Double totalRevenue = calculateTotalRevenue();
        Long totalTransactions = transactionHistoryRepository.count();
        return totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
    }

    private Double calculateAverageConsultantRating() {
        // Mock implementation - có thể thay bằng query thực tế sau
        return 4.5;
    }
}
