package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
    public DashboardReportDTO generateDashboardReport(LocalDateTime startDate, LocalDateTime endDate) {
        DashboardReportDTO report = new DashboardReportDTO();

        report.setOverviewStats(getOverviewStats());

        report.setBookingStats(generateBookingStatsList(startDate, endDate));

        // Get revenue stats
        report.setRevenueStats(generateRevenueStatsList(startDate, endDate));

        // Get user stats
        report.setUserStats(generateUserStatsList(startDate, endDate));

        // Get consultant stats
        report.setConsultantStats(generateConsultantStatsList());

        // Get service stats
        report.setServiceStats(generateServiceStatsList());

        return report;
    }

    @Override
    public DashboardReportDTO.OverviewStats getOverviewStats() {
        DashboardReportDTO.OverviewStats stats = new DashboardReportDTO.OverviewStats();

        // Total users
        stats.setTotalUsers(userRepository.count());

        // Total bookings
        stats.setTotalBookings(bookingRepository.count());

        // Total consultants
        stats.setTotalConsultants(consultantRepository.count());

        // Total revenue (mock data - replace with actual calculation)
        stats.setTotalRevenue(calculateTotalRevenue());

        // Active users (users who have made bookings in last 30 days)
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minus(30, ChronoUnit.DAYS);

        // Booking status counts
        stats.setCompletedBookings(bookingRepository.countByStatus("COMPLETED"));
        stats.setPendingBookings(bookingRepository.countByStatus("PENDING"));
        stats.setCancelledBookings(bookingRepository.countByStatus("CANCELLED"));

        return stats;
    }

    @Override
    public Object generateBookingsReport(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<DashboardReportDTO.BookingStats> bookingStats = generateBookingStatsList(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("bookingStats", bookingStats);
        report.put("totalBookings", bookingStats.stream().mapToLong(DashboardReportDTO.BookingStats::getTotalBookings).sum());
        report.put("completedBookings", bookingStats.stream().mapToLong(DashboardReportDTO.BookingStats::getCompletedBookings).sum());
        report.put("pendingBookings", bookingStats.stream().mapToLong(DashboardReportDTO.BookingStats::getPendingBookings).sum());
        report.put("cancelledBookings", bookingStats.stream().mapToLong(DashboardReportDTO.BookingStats::getCancelledBookings).sum());

        return report;
    }

    @Override
    public Object generateFinancialsReport(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<DashboardReportDTO.RevenueStats> revenueStats = generateRevenueStatsList(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("revenueStats", revenueStats);
        report.put("totalRevenue", revenueStats.stream().mapToDouble(DashboardReportDTO.RevenueStats::getTotalRevenue).sum());
        report.put("averageTransactionValue", calculateAverageTransactionValue(revenueStats));

        return report;
    }

    @Override
    public Object generateUsersReport(LocalDateTime startDate, LocalDateTime endDate, String period) {
        List<DashboardReportDTO.UserStats> userStats = generateUserStatsList(startDate, endDate);

        Map<String, Object> report = new HashMap<>();
        report.put("userStats", userStats);
        report.put("totalNewUsers", userStats.stream().mapToLong(DashboardReportDTO.UserStats::getNewUsers).sum());
        report.put("currentActiveUsers", userStats.isEmpty() ? 0 : userStats.get(userStats.size() - 1).getActiveUsers());
        report.put("totalUsers", userStats.isEmpty() ? 0 : userStats.get(userStats.size() - 1).getTotalUsers());

        return report;
    }

    @Override
    public Object generateConsultantsReport() {
        List<DashboardReportDTO.ConsultantStats> consultantStats = generateConsultantStatsList();

        Map<String, Object> report = new HashMap<>();
        report.put("consultantStats", consultantStats);
        report.put("totalConsultants", consultantStats.size());
        report.put("averageRating", consultantStats.stream().mapToDouble(DashboardReportDTO.ConsultantStats::getRating).average().orElse(0.0));
        report.put("totalConsultations", consultantStats.stream().mapToLong(DashboardReportDTO.ConsultantStats::getTotalConsultations).sum());

        return report;
    }

    @Override
    public Object generateServicesReport() {
        List<DashboardReportDTO.ServiceStats> serviceStats = generateServiceStatsList();

        Map<String, Object> report = new HashMap<>();
        report.put("serviceStats", serviceStats);
        report.put("totalServices", serviceStats.size());
        report.put("totalServiceBookings", serviceStats.stream().mapToLong(DashboardReportDTO.ServiceStats::getTotalBookings).sum());
        report.put("averageServiceRating", serviceStats.stream().mapToDouble(DashboardReportDTO.ServiceStats::getAverageRating).average().orElse(0.0));

        return report;
    }

    private List<DashboardReportDTO.BookingStats> generateBookingStatsList(LocalDateTime startDate, LocalDateTime endDate) {
        List<DashboardReportDTO.BookingStats> stats = new ArrayList<>();
        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            DashboardReportDTO.BookingStats dayStat = new DashboardReportDTO.BookingStats();
            dayStat.setDate(current.toLocalDate());
            dayStat.setPeriod("daily");

            dayStat.setTotalBookings(bookingRepository.countByBookingDate(current.toLocalDate()));
            dayStat.setCompletedBookings(bookingRepository.countByBookingDateAndStatus(current.toLocalDate(), "COMPLETED"));
            dayStat.setPendingBookings(bookingRepository.countByBookingDateAndStatus(current.toLocalDate(), "PENDING"));
            dayStat.setCancelledBookings(bookingRepository.countByBookingDateAndStatus(current.toLocalDate(), "CANCELLED"));

            stats.add(dayStat);
            current = current.plusDays(1);
        }

        return stats;
    }

    private List<DashboardReportDTO.RevenueStats> generateRevenueStatsList(LocalDateTime startDate, LocalDateTime endDate) {
        List<DashboardReportDTO.RevenueStats> stats = new ArrayList<>();

        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            DashboardReportDTO.RevenueStats dayStat = new DashboardReportDTO.RevenueStats();
            dayStat.setDate(current.toLocalDate());
            dayStat.setPeriod("daily");

            // Mock data - replace with actual repository calls
            dayStat.setTotalRevenue(calculateDailyRevenue(current));
            dayStat.setTransactionCount(transactionHistoryRepository.countByTransactionDate(current.toLocalDate()));
            dayStat.setAverageTransactionValue(
                dayStat.getTransactionCount() > 0 ?
                dayStat.getTotalRevenue() / dayStat.getTransactionCount() : 0.0
            );

            stats.add(dayStat);
            current = current.plusDays(1);
        }

        return stats;
    }

    private List<DashboardReportDTO.UserStats> generateUserStatsList(LocalDateTime startDate, LocalDateTime endDate) {
        List<DashboardReportDTO.UserStats> stats = new ArrayList<>();

        LocalDateTime current = startDate;
        while (!current.isAfter(endDate)) {
            DashboardReportDTO.UserStats dayStat = new DashboardReportDTO.UserStats();
            dayStat.setDate(current.toLocalDate());
            dayStat.setPeriod("daily");

            dayStat.setNewUsers(userRepository.countByRegistrationDate(current.toLocalDate(), current.toLocalDate().plusDays(1)));
            dayStat.setActiveUsers(userRepository.countActiveUsersByDate(current.toLocalDate()));
            dayStat.setTotalUsers(userRepository.countByRegistrationDateBefore(current.toLocalDate().plusDays(1)));

            stats.add(dayStat);
            current = current.plusDays(1);
        }

        return stats;
    }

    private List<DashboardReportDTO.ConsultantStats> generateConsultantStatsList() {
        List<Consultant> consultants = consultantRepository.findAll();

        return consultants.stream().map(consultant -> {
            DashboardReportDTO.ConsultantStats stat = new DashboardReportDTO.ConsultantStats();
            stat.setConsultantName(consultant.getUser().getFullName());
            stat.setTotalConsultations(consultationRepository.countByConsultantId(consultant.getId()));
            stat.setCompletedConsultations(consultationRepository.countByConsultantIdAndStatus(consultant.getId(), ConsultationStatus.COMPLETED));
            stat.setRating(calculateConsultantRating(consultant.getId()));
            stat.setRevenue(calculateConsultantRevenue(consultant.getId()));
            return stat;
        }).collect(Collectors.toList());
    }

    private List<DashboardReportDTO.ServiceStats> generateServiceStatsList() {
        List<TestingService> services = testingServiceRepository.findAll();

        return services.stream().map(service -> {
            DashboardReportDTO.ServiceStats stat = new DashboardReportDTO.ServiceStats();
            stat.setServiceName(service.getServiceName());
            stat.setTotalBookings(bookingRepository.countByServiceId(service.getId()));
            stat.setCompletedBookings(bookingRepository.countByServiceIdAndStatus(service.getId(), "COMPLETED"));
            stat.setRevenue(calculateServiceRevenue(service.getId()));
            stat.setAverageRating(calculateServiceRating(service.getId()));
            return stat;
        }).collect(Collectors.toList());
    }

    // Helper methods with mock implementations
    private Double calculateTotalRevenue() {
        // Mock implementation - replace with actual calculation
        return 50000.0;
    }

    private Double calculateDailyRevenue(LocalDateTime date) {
        // Mock implementation - replace with actual calculation
        return 1000.0 + (date.getDayOfMonth() * 50);
    }

    private Double calculateConsultantRating(Integer consultantId) {
        // Mock implementation - replace with actual calculation from feedback
        return 4.5;
    }

    private Double calculateConsultantRevenue(Integer consultantId) {
        // Mock implementation - replace with actual calculation
        return 5000.0;
    }

    private Double calculateServiceRevenue(Integer serviceId) {
        // Mock implementation - replace with actual calculation
        return 3000.0;
    }

    private Double calculateServiceRating(Integer serviceId) {
        // Mock implementation - replace with actual calculation
        return 4.2;
    }

    private Double calculateAverageTransactionValue(List<DashboardReportDTO.RevenueStats> revenueStats) {
        double totalRevenue = revenueStats.stream().mapToDouble(DashboardReportDTO.RevenueStats::getTotalRevenue).sum();
        long totalTransactions = revenueStats.stream().mapToLong(DashboardReportDTO.RevenueStats::getTransactionCount).sum();
        return totalTransactions > 0 ? totalRevenue / totalTransactions : 0.0;
    }
}
