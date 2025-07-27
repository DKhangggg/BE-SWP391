package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.TimeSlotResponseDTO;
import com.example.gender_healthcare_service.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time-slots")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    // Get available time slots for a specific date
    @GetMapping
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableTimeSlots(date);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch trống thành công", timeSlots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch trống: " + e.getMessage()));
        }
    }

    // Get available time slots for a specific consultant and date
    @GetMapping("/consultant")
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getAvailableTimeSlotsForConsultant(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer consultantId) {
        try {
            List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableTimeSlotsForConsultant(date, consultantId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch trống của tư vấn viên thành công", timeSlots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch trống của tư vấn viên: " + e.getMessage()));
        }
    }

    // Get available facility time slots for a specific date
    @GetMapping("/facility")
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getAvailableFacilityTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableFacilityTimeSlots(date);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch trống cơ sở thành công", timeSlots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch trống cơ sở: " + e.getMessage()));
        }
    }

    // Get available time slots by service and location
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getAvailableTimeSlotsByConsultant(
            @RequestParam Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        try {
            List<TimeSlotResponseDTO> slots = timeSlotService.getAvailableTimeSlotsByConsultant(consultantId, fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch trống theo tư vấn viên thành công", slots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch trống: " + e.getMessage()));
        }
    }

    // Admin functions
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> createTimeSlotsForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String slotType,
            @RequestParam(required = false) Integer consultantId,
            @RequestParam(defaultValue = "1") Integer capacity) {
        try {
            timeSlotService.createTimeSlotsForDate(date, slotType, consultantId, capacity);
            return ResponseEntity.ok(ApiResponse.success("Tạo lịch trống thành công cho ngày: " + date, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo lịch trống: " + e.getMessage()));
        }
    }

    @PostMapping("/create-recurring")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> createRecurringTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String slotType,
            @RequestParam(required = false) Integer consultantId,
            @RequestParam(defaultValue = "1") Integer capacity,
            @RequestParam String daysOfWeek) {
        try {
            timeSlotService.createRecurringTimeSlots(startDate, endDate, slotType, consultantId, capacity, daysOfWeek);
            return ResponseEntity.ok(ApiResponse.success("Tạo lịch trống định kỳ thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo lịch trống định kỳ: " + e.getMessage()));
        }
    }

    @PutMapping("/{timeSlotId}/capacity")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> updateTimeSlotCapacity(
            @PathVariable Integer timeSlotId,
            @RequestParam Integer newCapacity) {
        try {
            timeSlotService.updateTimeSlotCapacity(timeSlotId, newCapacity);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật sức chứa lịch trống thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật sức chứa lịch trống: " + e.getMessage()));
        }
    }

    @PutMapping("/{timeSlotId}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deactivateTimeSlot(@PathVariable Integer timeSlotId) {
        try {
            timeSlotService.deactivateTimeSlot(timeSlotId);
            return ResponseEntity.ok(ApiResponse.success("Vô hiệu hóa lịch trống thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi vô hiệu hóa lịch trống: " + e.getMessage()));
        }
    }

    // Get time slots by consultant
    @GetMapping("/consultant/{consultantId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<TimeSlotResponseDTO>>> getTimeSlotsByConsultant(
            @PathVariable Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<TimeSlotResponseDTO> timeSlots = timeSlotService.getTimeSlotsByConsultant(consultantId, date);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch trống theo tư vấn viên thành công", timeSlots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch trống: " + e.getMessage()));
        }
    }
} 