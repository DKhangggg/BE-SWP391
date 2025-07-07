package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardReportDTO {
    private OverviewStats overviewStats;
    private List<BookingStats> bookingStats;
    private List<RevenueStats> revenueStats;
    private List<UserStats> userStats;
    private List<ConsultantStats> consultantStats;
    private List<ServiceStats> serviceStats;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewStats {
        private Long totalUsers;
        private Long totalBookings;
        private Long totalConsultants;
        private Double totalRevenue;
        private Long completedBookings;
        private Long pendingBookings;
        private Long cancelledBookings;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingStats {
        private LocalDate date;
        private Long totalBookings;
        private Long completedBookings;
        private Long pendingBookings;
        private Long cancelledBookings;
        private String period;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueStats {
        private LocalDate date;
        private Double totalRevenue;
        private Long transactionCount;
        private Double averageTransactionValue;
        private String period;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserStats {
        private LocalDate date;
        private Long newUsers;
        private Long activeUsers;
        private Long totalUsers;
        private String period;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConsultantStats {
        private String consultantName;
        private Long totalConsultations;
        private Long completedConsultations;
        private Double rating;
        private Double revenue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServiceStats {
        private String serviceName;
        private Long totalBookings;
        private Long completedBookings;
        private Double revenue;
        private Double averageRating;
    }
}
