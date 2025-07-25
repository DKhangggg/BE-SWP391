package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.CreateMenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateDayLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuickLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateCycleSettingsRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.dto.response.DayLogResponseDTO;
import com.example.gender_healthcare_service.dto.response.PhaseInfoDTO;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import com.example.gender_healthcare_service.entity.SymptomLog;
import com.example.gender_healthcare_service.repository.SymptomLogRepository;

@RequestMapping("/api/menstrual-cycle")
@RestController()
public class EnhancedMenstrualCycleController {

    @Autowired
    private MenstrualCycleService menstrualCycleService;

    @Autowired
    private SymptomLogRepository symptomLogRepository;

    @GetMapping("/current")
    public ResponseEntity<ApiResponse<MenstrualCycleResponseDTO>> getCurrentMenstrualCycle(Authentication authentication) {
        try {
            MenstrualCycleResponseDTO dto = menstrualCycleService.getCurrentMenstrualCycle(authentication);
            if (dto == null) {
                return ResponseEntity.ok(ApiResponse.error("Bạn chưa có chu kỳ nào trong hệ thống"));
            }
            return ResponseEntity.ok(ApiResponse.success("Thành công", dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<List<MenstrualCycleResponseDTO>>> createMenstrualCycle(
            Authentication authentication,
            @RequestBody List<CreateMenstrualCycleRequestDTO> requests) {
        try {
            List<MenstrualCycleResponseDTO> result = menstrualCycleService.createMenstrualCycle(authentication, requests);
            return ResponseEntity.ok(ApiResponse.success("Tạo chu kỳ thành công", result));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/day-log/{date}")
    public ResponseEntity<ApiResponse<DayLogResponseDTO>> getDayLog(
            Authentication authentication,
            @PathVariable String date) {
        try {
            DayLogResponseDTO dto = menstrualCycleService.getDayLog(authentication, date);
            return ResponseEntity.ok(ApiResponse.success("Thành công", dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/update-day-log")
    public ResponseEntity<ApiResponse<DayLogResponseDTO>> updateDayLog(
            Authentication authentication,
            @RequestBody UpdateDayLogRequestDTO request) {
        try {
            DayLogResponseDTO dto = menstrualCycleService.updateDayLog(authentication, request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thành công", dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/quick-log")
    public ResponseEntity<ApiResponse<DayLogResponseDTO>> quickLog(
            Authentication authentication,
            @RequestBody QuickLogRequestDTO request) {
        try {
            DayLogResponseDTO result = menstrualCycleService.quickLog(authentication, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Ghi nhận nhanh thành công", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/phases/{year}/{month}")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPhasesForMonth(
            Authentication authentication,
            @PathVariable int year,
            @PathVariable int month) {
        try {
            Map<LocalDate, String> phases = menstrualCycleService.calculatePhasesForMonth(authentication, year, month);
            
            // Convert LocalDate to String for JSON response
            Map<String, String> phasesResponse = phases.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    Map.Entry::getValue
                ));
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phases thành công", phasesResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/phases-detailed/{year}/{month}")
    public ResponseEntity<ApiResponse<Map<String, PhaseInfoDTO>>> getDetailedPhasesForMonth(
            Authentication authentication,
            @PathVariable int year,
            @PathVariable int month) {
        try {
            Map<LocalDate, PhaseInfoDTO> detailedPhases = menstrualCycleService.calculateDetailedPhasesForMonth(authentication, year, month);
            
            // Convert LocalDate to String for JSON response
            Map<String, PhaseInfoDTO> phasesResponse = detailedPhases.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    Map.Entry::getValue
                ));
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy phases chi tiết thành công", phasesResponse));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/cycle-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCycleSummary(Authentication authentication) {
        try {
            MenstrualCycleResponseDTO currentCycle = menstrualCycleService.getCurrentMenstrualCycle(authentication);
            if (currentCycle == null) {
                return ResponseEntity.ok(ApiResponse.error("Chưa có dữ liệu chu kỳ"));
            }

            Map<String, Object> summary = new HashMap<>();
            summary.put("currentCycle", currentCycle);
            
            // Tính toán thông tin bổ sung
            LocalDate today = LocalDate.now();
            LocalDate nextPeriod = currentCycle.getNextPredictedPeriod();
            LocalDate ovulationDate = currentCycle.getOvulationDate();
            
            if (nextPeriod != null) {
                long daysToNextPeriod = ChronoUnit.DAYS.between(today, nextPeriod);
                summary.put("daysToNextPeriod", daysToNextPeriod);
            }
            
            if (ovulationDate != null) {
                long daysToOvulation = ChronoUnit.DAYS.between(today, ovulationDate);
                summary.put("daysToOvulation", daysToOvulation);
            }
            
            summary.put("cycleLength", currentCycle.getCycleLength());
            summary.put("periodDuration", currentCycle.getPeriodDuration());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin chu kỳ thành công", summary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/logs/{startDate}/{endDate}")
    public ResponseEntity<ApiResponse<List<DayLogResponseDTO>>> getLogsForDateRange(
            Authentication authentication,
            @PathVariable String startDate,
            @PathVariable String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<MenstrualLog> logs = menstrualCycleService.getLogsForDateRange(authentication, start, end);
            
            List<DayLogResponseDTO> response = logs.stream()
                .map(log -> {
                    DayLogResponseDTO dto = new DayLogResponseDTO();
                    dto.setDate(log.getLogDate().toLocalDate());
                    dto.setIsPeriodDay(log.getIsActualPeriod());
                    dto.setIntensity(log.getFlowIntensity() != null ? log.getFlowIntensity().name() : null);
                    dto.setMood(log.getMood() != null ? log.getMood().name() : null);
                    dto.setNotes(log.getNotes());
                    
                    // Get symptoms
                    List<SymptomLog> symptomLogs = symptomLogRepository.findByMenstrualLog(log);
                    String symptoms = symptomLogs.stream()
                        .map(sl -> sl.getSymptom().getSymptomName() + 
                             (sl.getSeverity() != null ? " (" + sl.getSeverity().name() + ")" : ""))
                        .collect(Collectors.joining(", "));
                    dto.setSymptoms(symptoms);
                    
                    return dto;
                })
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy logs thành công", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<?> updateMenstrualCycle() {
        return null;
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMenstrualCycle() {
        return null;
    }

    @PatchMapping("/update-settings")
    public ResponseEntity<ApiResponse<MenstrualCycleResponseDTO>> updateCycleSettings(
            Authentication authentication,
            @RequestBody UpdateCycleSettingsRequestDTO request) {
        try {
            MenstrualCycleResponseDTO dto = menstrualCycleService.updateCycleSettings(authentication, request);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật thiết lập chu kỳ thành công", dto));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
}