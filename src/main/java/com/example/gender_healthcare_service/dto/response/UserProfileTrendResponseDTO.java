package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileTrendResponseDTO {
    private UserResponseDTO userInfo;
    private ProfileTrendData trendData;
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfileTrendData {
        private Integer profileCompletionRate;
        private Integer totalBookings;
        private Integer completedBookings;
        private Integer pendingBookings;
        private Integer cancelledBookings;
        private Double averageRating;
        private Integer totalConsultations;
        private Integer completedConsultations;
        private Integer totalQuestions;
        private Integer answeredQuestions;
        private Integer totalPayments;
        private Double totalSpent;
        private List<MonthlyTrend> monthlyTrends;
        private List<ServiceUsage> topServices;
        private List<RecentActivity> recentActivities;
        private HealthInsights healthInsights;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyTrend {
        private String month;
        private Integer bookings;
        private Integer consultations;
        private Double spending;
        private Double rating;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ServiceUsage {
        private String serviceName;
        private Integer usageCount;
        private Double totalSpent;
        private Double averageRating;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecentActivity {
        private String type; // "BOOKING", "CONSULTATION", "PAYMENT", "QUESTION"
        private String title;
        private String description;
        private String date;
        private String status;
        private String icon;
    }
    
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HealthInsights {
        private String cycleStatus; // "REGULAR", "IRREGULAR", "UNKNOWN"
        private Integer averageCycleLength;
        private String nextPeriodPrediction;
        private String fertilityWindow;
        private List<String> commonSymptoms;
        private String healthRecommendation;
        private Integer wellnessScore; // 0-100
    }
} 