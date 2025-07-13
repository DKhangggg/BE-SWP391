package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.TimeSlotResponseDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.TimeSlotRepository;
import com.example.gender_healthcare_service.service.TimeSlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimeSlotServiceImpl implements TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final ConsultantRepository consultantRepository;

    @Override
    public List<TimeSlotResponseDTO> getAvailableTimeSlots(LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAvailableTimeSlotsByDate(date);
        return timeSlots.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotResponseDTO> getAvailableTimeSlotsForConsultant(LocalDate date, Integer consultantId) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAvailableTimeSlotsByDateAndConsultant(date, consultantId);
        return timeSlots.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotResponseDTO> getAvailableFacilityTimeSlots(LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAvailableFacilityTimeSlotsByDate(date);
        return timeSlots.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotResponseDTO> getAvailableFacilityTimeSlotsFromDate(LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findAvailableFacilityTimeSlotsFromDate(date);
        return timeSlots.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void createTimeSlotsForDate(LocalDate date, String slotType, Integer consultantId, Integer capacity) {
        Consultant consultant = null;
        if (consultantId != null) {
            consultant = consultantRepository.findById(consultantId)
                    .orElseThrow(() -> new ServiceNotFoundException("Consultant not found with ID: " + consultantId));
        }

        // Create time slots for the day (8:00 AM to 5:00 PM, 1-hour slots)
        LocalTime startTime = LocalTime.of(8, 0);
        LocalTime endTime = LocalTime.of(17, 0);
        
        for (LocalTime time = startTime; time.isBefore(endTime); time = time.plusHours(1)) {
            TimeSlot timeSlot = new TimeSlot();
            timeSlot.setSlotDate(date);
            timeSlot.setStartTime(time);
            timeSlot.setEndTime(time.plusHours(1));
            timeSlot.setSlotType(slotType);
            timeSlot.setConsultant(consultant);
            timeSlot.setCapacity(capacity != null ? capacity : 1);
            timeSlot.setBookedCount(0);
            timeSlot.setIsAvailable(true);
            timeSlot.setIsDeleted(false);
            timeSlot.setDescription(slotType + " slot");
            
            timeSlotRepository.save(timeSlot);
        }
    }

    @Override
    @Transactional
    public void createRecurringTimeSlots(LocalDate startDate, LocalDate endDate, String slotType, 
                                       Integer consultantId, Integer capacity, String daysOfWeek) {
        // Implementation for recurring time slots
        // This would create time slots for specific days of the week within the date range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            String dayOfWeek = currentDate.getDayOfWeek().toString();
            if (daysOfWeek.contains(dayOfWeek)) {
                createTimeSlotsForDate(currentDate, slotType, consultantId, capacity);
            }
            currentDate = currentDate.plusDays(1);
        }
    }

    @Override
    @Transactional
    public void updateTimeSlotCapacity(Integer timeSlotId, Integer newCapacity) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ServiceNotFoundException("Time slot not found with ID: " + timeSlotId));
        
        if (newCapacity < timeSlot.getBookedCount()) {
            throw new IllegalArgumentException("New capacity cannot be less than current booked count");
        }
        
        timeSlot.setCapacity(newCapacity);
        timeSlotRepository.save(timeSlot);
    }

    @Override
    @Transactional
    public void deactivateTimeSlot(Integer timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ServiceNotFoundException("Time slot not found with ID: " + timeSlotId));
        
        timeSlot.setIsAvailable(false);
        timeSlotRepository.save(timeSlot);
    }

    @Override
    public Page<TimeSlotResponseDTO> getTimeSlotsWithPagination(Pageable pageable) {
        Page<TimeSlot> timeSlotsPage = timeSlotRepository.findAll(pageable);
        return timeSlotsPage.map(this::convertToDto);
    }

    @Override
    public List<TimeSlotResponseDTO> getTimeSlotsByConsultant(Integer consultantId, LocalDate date) {
        List<TimeSlot> timeSlots = timeSlotRepository.findByConsultantId(consultantId);
        return timeSlots.stream()
                .filter(ts -> ts.getSlotDate().equals(date))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TimeSlotResponseDTO> getAvailableTimeSlotsByConsultant(Integer consultantId, LocalDate fromDate, LocalDate toDate) {
        List<TimeSlot> slots = timeSlotRepository.findAvailableByConsultantAndDateRange(consultantId, fromDate, toDate);
        return slots.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private TimeSlotResponseDTO convertToDto(TimeSlot timeSlot) {
        TimeSlotResponseDTO dto = new TimeSlotResponseDTO();
        dto.setTimeSlotId(timeSlot.getTimeSlotID());
        dto.setSlotDate(timeSlot.getSlotDate());
        dto.setStartTime(timeSlot.getStartTime());
        dto.setEndTime(timeSlot.getEndTime());
        dto.setDuration(timeSlot.getDuration());
        dto.setDescription(timeSlot.getDescription());
        dto.setIsActive(timeSlot.getIsAvailable());
        dto.setCapacity(timeSlot.getCapacity());
        dto.setBookedCount(timeSlot.getBookedCount());
        dto.setAvailableSlots(timeSlot.getCapacity() - timeSlot.getBookedCount());
        dto.setSlotType(timeSlot.getSlotType());
        dto.setIsAvailable(timeSlot.isAvailable());
        dto.setDisplayInfo(timeSlot.getDisplayInfo());
        
        if (timeSlot.getConsultant() != null) {
            dto.setConsultantId(timeSlot.getConsultant().getId());
            dto.setConsultantName(timeSlot.getConsultant().getUser().getFullName());
            dto.setConsultantSpecialization(timeSlot.getConsultant().getSpecialization());
        }
        
        return dto;
    }
} 