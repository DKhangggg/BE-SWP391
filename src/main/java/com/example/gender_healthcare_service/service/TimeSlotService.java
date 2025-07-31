package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.response.TimeSlotResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface TimeSlotService {
    
    // Get available time slots for a specific date
    List<TimeSlotResponseDTO> getAvailableTimeSlots(LocalDate date);
    
    // Get available time slots for a specific consultant and date
    List<TimeSlotResponseDTO> getAvailableTimeSlotsForConsultant(LocalDate date, Integer consultantId);
    
    // Get available facility time slots for a specific date
    List<TimeSlotResponseDTO> getAvailableFacilityTimeSlots(LocalDate date);
    
    // Get available facility time slots from a specific date onwards
    List<TimeSlotResponseDTO> getAvailableFacilityTimeSlotsFromDate(LocalDate date);
    
    // Create time slots for a specific date (admin function)
    void createTimeSlotsForDate(LocalDate date, String slotType, Integer consultantId, Integer capacity);
    
    // Create recurring time slots (admin function)
    void createRecurringTimeSlots(LocalDate startDate, LocalDate endDate, String slotType, 
                                 Integer consultantId, Integer capacity, String daysOfWeek);
    
    // Auto create time slots for multiple days (admin function)
    void autoCreateTimeSlots(LocalDate startDate, int days, String slotType, Integer capacity, String description, Integer duration);
    
    // Update time slot capacity
    void updateTimeSlotCapacity(Integer timeSlotId, Integer newCapacity);
    
    // Deactivate time slot
    void deactivateTimeSlot(Integer timeSlotId);
    
    // Get time slots with pagination (admin function)
    Page<TimeSlotResponseDTO> getTimeSlotsWithPagination(Pageable pageable);
    
    // Get time slots by consultant
    List<TimeSlotResponseDTO> getTimeSlotsByConsultant(Integer consultantId, LocalDate date);

    List<TimeSlotResponseDTO> getAvailableTimeSlotsByConsultant(Integer consultantId, LocalDate fromDate, LocalDate toDate);
    public boolean checkTimeSlotExists(LocalDate slotDate, String startTime, String slotType);
}
