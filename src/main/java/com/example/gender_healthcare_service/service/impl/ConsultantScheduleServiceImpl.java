package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantSchedule;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.ConsultantScheduleRepository;
import com.example.gender_healthcare_service.repository.TimeSlotRepository;
import com.example.gender_healthcare_service.dto.request.AddUnavailabilityRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantScheduleResponseDTO;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import com.example.gender_healthcare_service.repository.ConsultantUnavailabilityRepository;
import com.example.gender_healthcare_service.service.ConsultantScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultantScheduleServiceImpl implements ConsultantScheduleService {

    private final ConsultantScheduleRepository consultantScheduleRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private ConsultantUnavailabilityRepository consultantUnavailabilityRepository;

    @Override
    public void createDefaultScheduleForConsultant(Consultant consultant, LocalDate startDate, int numberOfWeeks) {
        List<TimeSlot> allTimeSlots = timeSlotRepository.findAll();
        if (allTimeSlots.isEmpty()) {
            return;
        }

        LocalDate currentDate = startDate;
        for (int week = 0; week < numberOfWeeks; week++) {
            for (int dayOfWeek = 0; dayOfWeek < 5; dayOfWeek++) {
                if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    currentDate = currentDate.plusDays(1);
                    dayOfWeek--;
                    continue;
                }
                for (TimeSlot slot : allTimeSlots) {
                    ConsultantSchedule schedule = new ConsultantSchedule();
                    schedule.setConsultant(consultant);
                    schedule.setScheduleDate(currentDate);
                    schedule.setTimeSlot(slot);
                    schedule.setStatus("Available");
                    consultantScheduleRepository.save(schedule);
                }
                currentDate = currentDate.plusDays(1);
            }

        }
    }

    @Override
    public ConsultantScheduleResponseDTO getConsultantSchedule(Integer consultantId) {
        // Placeholder: Implement logic to fetch consultant's schedule, available slots, and unavailabilities
        // Consultant consultant = consultantRepository.findById(consultantId)
        // .orElseThrow(() -> new RuntimeException("Consultant not found: " + consultantId));
        // List<ConsultantSchedule> schedules = consultantScheduleRepository.findByConsultant(consultant);
        // List<ConsultantUnavailability> unavailabilities = consultantUnavailabilityRepository.findByConsultant(consultant);
        // Map to ConsultantScheduleResponseDTO
        System.out.println("Fetching schedule for consultant ID: " + consultantId);
        return new ConsultantScheduleResponseDTO(); // Return dummy DTO
    }

    @Override
    public void addUnavailability(Integer consultantId, AddUnavailabilityRequestDTO request) {
        // Placeholder: Implement logic to add unavailability for the consultant
        // Consultant consultant = consultantRepository.findById(consultantId)
        // .orElseThrow(() -> new RuntimeException("Consultant not found: " + consultantId));
        // ConsultantUnavailability unavailability = new ConsultantUnavailability();
        // unavailability.setConsultant(consultant);
        // unavailability.setDate(request.getDate());
        // unavailability.setStartTime(request.getStartTime());
        // unavailability.setEndTime(request.getEndTime());
        // unavailability.setReason(request.getReason());
        // consultantUnavailabilityRepository.save(unavailability);
        System.out.println("Adding unavailability for consultant ID: " + consultantId + " on date: " + request.getDate());
    }
}
