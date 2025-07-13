package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.TimeSlotResponseDTO;
import com.example.gender_healthcare_service.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<List<TimeSlotResponseDTO>> getAvailableTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableTimeSlots(date);
        return ResponseEntity.ok(timeSlots);
    }

    // Get available time slots for a specific consultant and date
    @GetMapping("/consultant")
    public ResponseEntity<List<TimeSlotResponseDTO>> getAvailableTimeSlotsForConsultant(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Integer consultantId) {
        List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableTimeSlotsForConsultant(date, consultantId);
        return ResponseEntity.ok(timeSlots);
    }

    // Get available facility time slots for a specific date
    @GetMapping("/facility")
    public ResponseEntity<List<TimeSlotResponseDTO>> getAvailableFacilityTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotResponseDTO> timeSlots = timeSlotService.getAvailableFacilityTimeSlotsFromDate(date);
        return ResponseEntity.ok(timeSlots);
    }

    // Get available time slots by service and location
    @GetMapping("/available")
    public ResponseEntity<List<TimeSlotResponseDTO>> getAvailableTimeSlotsByConsultant(
            @RequestParam Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<TimeSlotResponseDTO> slots = timeSlotService.getAvailableTimeSlotsByConsultant(consultantId, fromDate, toDate);
        return ResponseEntity.ok(slots);
    }

    // Admin functions
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createTimeSlotsForDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String slotType,
            @RequestParam(required = false) Integer consultantId,
            @RequestParam(defaultValue = "1") Integer capacity) {
        timeSlotService.createTimeSlotsForDate(date, slotType, consultantId, capacity);
        return ResponseEntity.ok("Time slots created successfully for date: " + date);
    }

    @PostMapping("/create-recurring")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> createRecurringTimeSlots(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String slotType,
            @RequestParam(required = false) Integer consultantId,
            @RequestParam(defaultValue = "1") Integer capacity,
            @RequestParam String daysOfWeek) {
        timeSlotService.createRecurringTimeSlots(startDate, endDate, slotType, consultantId, capacity, daysOfWeek);
        return ResponseEntity.ok("Recurring time slots created successfully");
    }

    @PutMapping("/{timeSlotId}/capacity")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> updateTimeSlotCapacity(
            @PathVariable Integer timeSlotId,
            @RequestParam Integer newCapacity) {
        timeSlotService.updateTimeSlotCapacity(timeSlotId, newCapacity);
        return ResponseEntity.ok("Time slot capacity updated successfully");
    }

    @PutMapping("/{timeSlotId}/deactivate")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> deactivateTimeSlot(@PathVariable Integer timeSlotId) {
        timeSlotService.deactivateTimeSlot(timeSlotId);
        return ResponseEntity.ok("Time slot deactivated successfully");
    }

    // Get time slots by consultant
    @GetMapping("/consultant/{consultantId}")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<TimeSlotResponseDTO>> getTimeSlotsByConsultant(
            @PathVariable Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<TimeSlotResponseDTO> timeSlots = timeSlotService.getTimeSlotsByConsultant(consultantId, date);
        return ResponseEntity.ok(timeSlots);
    }
} 