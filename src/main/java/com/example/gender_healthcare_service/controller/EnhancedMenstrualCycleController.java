package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.EnhancedMenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.UserRepository;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menstrual-cycle")
@PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN') or hasAuthority('CUSTOMER') or hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_CUSTOMER')")
public class EnhancedMenstrualCycleController {

    private static final Logger logger = LoggerFactory.getLogger(EnhancedMenstrualCycleController.class);

    @Autowired
    private MenstrualCycleService menstrualCycleService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Log enhanced menstrual data with symptoms, mood, and flow tracking
     */
    @PostMapping("/log-enhanced")
    public ResponseEntity<?> logEnhancedMenstrualData(@RequestBody EnhancedMenstrualLogRequestDTO requestDTO) {
        try {
            menstrualCycleService.logEnhancedMenstrualData(requestDTO);
            return ResponseEntity.ok("Enhanced menstrual data logged successfully");
        } catch (Exception e) {
            logger.error("Error logging enhanced menstrual data: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to log enhanced menstrual data: " + e.getMessage());
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
            List<MenstrualLogResponseDTO> logs = menstrualCycleService.getMenstrualLogsByDateRange(userId, startDate, endDate);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            logger.error("Error getting menstrual logs by date range: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to get menstrual logs: " + e.getMessage());
        }
    }

    /**
     * Get comprehensive dashboard data for menstrual cycle tracking
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getMenstrualCycleDashboard() {
        try {
            Integer userId = getCurrentUserId();

            // Collect all relevant data for dashboard
            PeriodPredictionDTO prediction = menstrualCycleService.getPeriodPrediction(userId);
            FertilityWindowDTO fertilityWindow = menstrualCycleService.getFertilityWindow(userId);
            CycleAnalyticsDTO analytics = menstrualCycleService.getCycleAnalytics(userId);
            List<String> insights = menstrualCycleService.getHealthInsights(userId);

            // Create dashboard response
            MenstrualCycleDashboardDTO dashboard = new MenstrualCycleDashboardDTO();
            dashboard.setPeriodPrediction(prediction);
            dashboard.setFertilityWindow(fertilityWindow);
            dashboard.setCycleAnalytics(analytics);
            dashboard.setHealthInsights(insights);

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
