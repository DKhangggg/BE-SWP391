package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.EnhancedMenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.INotificationService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/menstrual-cycle")
@PreAuthorize("isAuthenticated()")
//@PreAuthorize("hasAuthority('CUSTOMER') or hasAuthority('ROLE_CUSTOMER') or hasRole('CUSTOMER') or hasRole('ROLE_CUSTOMER') or hasAuthority('USER') or hasAuthority('ROLE_USER') or hasRole('USER') or hasRole('ROLE_USER') or hasAuthority('ADMIN') or hasAuthority('ROLE_ADMIN') or hasRole('ADMIN') or hasRole('ROLE_ADMIN') or hasAuthority('ROLE_CONSULTANT') or hasRole('ROLE_CONSULTANT')")
public class EnhancedMenstrualCycleController {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedMenstrualCycleController.class);

    @Autowired
    private MenstrualCycleService menstrualCycleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private INotificationService notificationService;

    @PostMapping("/log-enhanced")
    public ResponseEntity<?> logEnhancedMenstrualData(@RequestBody EnhancedMenstrualLogRequestDTO requestDTO) {
        try {
            if(requestDTO.getLogDate()==null){requestDTO.setLogDate(LocalDateTime.now());}
            menstrualCycleService.logEnhancedMenstrualData(requestDTO);
            return ResponseEntity.ok("Enhanced menstrual data logged successfully");
        } catch (Exception e) {
            logger.error("Error logging enhanced menstrual data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to log enhanced menstrual data: " + e.getMessage());
        }
    }

    @GetMapping("/consultant/view/{userId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<?> getMenstrualLogsForUserByConsultant(
            @PathVariable Integer userId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusMonths(3);
            if (endDate == null) endDate = LocalDate.now();

            List<MenstrualLogResponseDTO> logs = menstrualCycleService.getMenstrualLogsByDateRange(userId, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Error getting menstrual logs for user by consultant: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get menstrual logs: " + e.getMessage());
        }
    }

    @PutMapping("/consultant/log/{logId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<?> updateMenstrualLogByConsultant(
            @PathVariable Long logId,
            @RequestBody EnhancedMenstrualLogRequestDTO requestDTO) {
        try {
            MenstrualLogResponseDTO updatedLog = menstrualCycleService.updateEnhancedMenstrualLog(logId, requestDTO);
            if (updatedLog != null) {
                User user = userRepository.findById(updatedLog.getUserId()).orElse(null);
                if (user != null) {
                    notificationService.createNotification(user, "Your menstrual log has been updated by a consultant.", "/menstrual-cycle/logs");
                }
                return ResponseEntity.ok(updatedLog);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Menstrual log not found or could not be updated.");
            }
        } catch (Exception e) {
            logger.error("Error updating menstrual log by consultant: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to update menstrual log: " + e.getMessage());
        }
    }

    /**
     * Get period prediction based on historical data
     */
    @GetMapping("/prediction")
    public ResponseEntity<?> getPeriodPrediction() {
        try {
            // For current user - you might want to add userId parameter for admin access
            Integer userId = getCurrentUserId();
            PeriodPredictionDTO prediction = menstrualCycleService.getPeriodPrediction(userId);
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            logger.error("Error getting period prediction: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get period prediction: " + e.getMessage());
        }
    }

    /**
     * Get fertility window calculation
     */
    @GetMapping("/fertility-window")
    public ResponseEntity<?> getFertilityWindow() {
        try {
            Integer userId = getCurrentUserId();
            FertilityWindowDTO fertilityWindow = menstrualCycleService.getFertilityWindow(userId);
            return ResponseEntity.ok(fertilityWindow);
        } catch (Exception e) {
            logger.error("Error getting fertility window: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get fertility window: " + e.getMessage());
        }
    }

    /**
     * Get comprehensive cycle analytics
     */
    @GetMapping("/analytics")
    public ResponseEntity<?> getCycleAnalytics() {
        try {
            Integer userId = getCurrentUserId();
            CycleAnalyticsDTO analytics = menstrualCycleService.getCycleAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            logger.error("Error getting cycle analytics: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get cycle analytics: " + e.getMessage());
        }
    }

    /**
     * Get symptom patterns analysis
     */
    @GetMapping("/symptom-patterns")
    public ResponseEntity<?> getSymptomPatterns() {
        try {
            Integer userId = getCurrentUserId();
            List<SymptomPatternDTO> patterns = menstrualCycleService.getSymptomPatterns(userId);
            return ResponseEntity.ok(patterns);
        } catch (Exception e) {
            logger.error("Error getting symptom patterns: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get symptom patterns: " + e.getMessage());
        }
    }

    /**
     * Get personalized health insights
     */
    @GetMapping("/health-insights")
    public ResponseEntity<?> getHealthInsights() {
        try {
            Integer userId = getCurrentUserId();
            List<String> insights = menstrualCycleService.getHealthInsights(userId);
            return ResponseEntity.ok(insights);
        } catch (Exception e) {
            logger.error("Error getting health insights: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get health insights: " + e.getMessage());
        }
    }

    /**
     * Get menstrual logs within a date range
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getMenstrualLogsByDateRange(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            if (startDate == null) startDate = LocalDate.now().minusMonths(3);
            if (endDate == null) endDate = LocalDate.now();

            Integer userId = getCurrentUserId();
            List<MenstrualLogResponseDTO> logs = menstrualCycleService.getMenstrualLogsByDateRange(userId, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Error getting menstrual logs by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get menstrual logs: " + e.getMessage());
        }
    }

    /**
     * Get current cycle information for the user
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentCycle() {
        try {
            Integer userId = getCurrentUserId();

            // Get current cycle data
            PeriodPredictionDTO prediction = menstrualCycleService.getPeriodPrediction(userId);
            FertilityWindowDTO fertilityWindow = menstrualCycleService.getFertilityWindow(userId);
            CycleAnalyticsDTO analytics = menstrualCycleService.getCycleAnalytics(userId);

            // Get recent logs (last 3 months)
            LocalDate startDate = LocalDate.now().minusMonths(3);
            LocalDate endDate = LocalDate.now();
            List<MenstrualLogResponseDTO> recentLogs = menstrualCycleService.getMenstrualLogsByDateRange(
                userId, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));

            CurrentCycleDTO currentCycle = new CurrentCycleDTO();
            currentCycle.setPeriodPrediction(prediction);
            currentCycle.setFertilityWindow(fertilityWindow);
            currentCycle.setCycleAnalytics(analytics);
            currentCycle.setRecentLogs(recentLogs);

            return ResponseEntity.ok(currentCycle);
        } catch (Exception e) {
            logger.error("Error getting current cycle: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get current cycle: " + e.getMessage());
        }
    }

    /**
     * Get calendar data for cycle tracking UI
     */
    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendarData(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month) {
        try {
            Integer userId = getCurrentUserId();

            // Default to current month if not specified
            if (year == null) year = LocalDate.now().getYear();
            if (month == null) month = LocalDate.now().getMonthValue();

            // Get period prediction and fertility window
            PeriodPredictionDTO prediction = menstrualCycleService.getPeriodPrediction(userId);
            FertilityWindowDTO fertilityWindow = menstrualCycleService.getFertilityWindow(userId);

            // Get logs for the specified month
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            List<MenstrualLogResponseDTO> logs = menstrualCycleService.getMenstrualLogsByDateRange(
                userId, startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));

            // Create calendar response
            CalendarDataDTO calendarData = new CalendarDataDTO();
            calendarData.setPeriodPrediction(prediction);
            calendarData.setFertilityWindow(fertilityWindow);
            calendarData.setLogs(logs);
            calendarData.setYear(year);
            calendarData.setMonth(month);

            return ResponseEntity.ok(calendarData);
        } catch (Exception e) {
            logger.error("Error getting calendar data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get calendar data: " + e.getMessage());
        }
    }

    /**
     * Get comprehensive dashboard data for menstrual cycle tracking
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getMenstrualCycleDashboard(HttpServletRequest request) {
        try {
            // Lấy token từ header
            String authHeader = request.getHeader("Authorization");
            logger.info("[DASHBOARD] Authorization header: {}", authHeader);

            // Lấy user từ SecurityContext
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = "unknown";
            Collection<?> authorities = null;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
                authorities = ((UserDetails) principal).getAuthorities();
            }
            logger.info("[DASHBOARD] Username: {}", username);
            logger.info("[DASHBOARD] Authorities: {}", authorities);

            Integer userId = getCurrentUserId();
            logger.info("[DASHBOARD] userId: {}", userId);

            PeriodPredictionDTO prediction = menstrualCycleService.getPeriodPrediction(userId);
            FertilityWindowDTO fertilityWindow = menstrualCycleService.getFertilityWindow(userId);
            CycleAnalyticsDTO analytics = menstrualCycleService.getCycleAnalytics(userId);
            List<String> insights = menstrualCycleService.getHealthInsights(userId);

            boolean isEmpty = (prediction == null || prediction.getNextPeriodDate() == null)
                    && (fertilityWindow == null || fertilityWindow.getOvulationDate() == null)
                    && (analytics == null || analytics.getTotalCyclesTracked() == 0);

            MenstrualCycleDashboardDTO dashboard = new MenstrualCycleDashboardDTO();
            if (isEmpty) {
                // Trả về dashboard mẫu nếu chưa có dữ liệu
                dashboard.setPeriodPrediction(null);
                dashboard.setFertilityWindow(null);
                dashboard.setCycleAnalytics(null);
                dashboard.setHealthInsights(List.of("Bạn chưa có dữ liệu chu kỳ nào. Hãy nhập nhật ký chu kỳ đầu tiên trong tháng này!"));
            } else {
                dashboard.setPeriodPrediction(prediction);
                dashboard.setFertilityWindow(fertilityWindow);
                dashboard.setCycleAnalytics(analytics);
                dashboard.setHealthInsights(insights);
            }

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            logger.error("Error getting menstrual cycle dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get dashboard data: " + e.getMessage());
        }
    }

    // Helper method to get current user ID - you may need to implement this based on your authentication
    private Integer getCurrentUserId() {
        // Get current authenticated user from SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Find user by username (you may need to inject UserRepository)
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }
        return user.getId();
    }
}
