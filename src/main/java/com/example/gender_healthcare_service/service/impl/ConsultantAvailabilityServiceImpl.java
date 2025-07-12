package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantAvailability;
import com.example.gender_healthcare_service.entity.ConsultantSchedule;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.repository.ConsultantAvailabilityRepository;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.ConsultantScheduleRepository;
import com.example.gender_healthcare_service.repository.TimeSlotRepository;
import com.example.gender_healthcare_service.service.ConsultantAvailabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ConsultantAvailabilityServiceImpl implements ConsultantAvailabilityService {

    private final ConsultantAvailabilityRepository availabilityRepository;
    private final ConsultantScheduleRepository scheduleRepository;
    private final ConsultantRepository consultantRepository;
    private final TimeSlotRepository timeSlotRepository;

    @Override
    public ConsultantAvailability createAvailability(Consultant consultant, DayOfWeek dayOfWeek, 
                                                   TimeSlot timeSlot, Integer maxBookings, String notes) {
        log.info("Creating availability for consultant {} on {} at {}", 
                consultant.getId(), dayOfWeek, timeSlot.getStartTime());

        // Kiểm tra xem availability đã tồn tại chưa
        Optional<ConsultantAvailability> existing = availabilityRepository
                .findByConsultantAndDayOfWeekAndTimeSlotAndIsDeletedFalse(consultant, dayOfWeek, timeSlot);
        
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Availability already exists for this consultant, day and time slot");
        }

        ConsultantAvailability availability = new ConsultantAvailability();
        availability.setConsultant(consultant);
        availability.setDayOfWeek(dayOfWeek);
        availability.setTimeSlot(timeSlot);
        availability.setMaxBookings(maxBookings != null ? maxBookings : 1);
        availability.setNotes(notes);
        availability.setIsAvailable(true);
        availability.setIsDeleted(false);

        return availabilityRepository.save(availability);
    }

    @Override
    public ConsultantAvailability updateAvailability(Integer availabilityId, Boolean isAvailable, 
                                                   Integer maxBookings, String notes) {
        log.info("Updating availability {}", availabilityId);

        ConsultantAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        if (isAvailable != null) {
            availability.setIsAvailable(isAvailable);
        }
        if (maxBookings != null) {
            availability.setMaxBookings(maxBookings);
        }
        if (notes != null) {
            availability.setNotes(notes);
        }

        return availabilityRepository.save(availability);
    }

    @Override
    public void deleteAvailability(Integer availabilityId) {
        log.info("Deleting availability {}", availabilityId);

        ConsultantAvailability availability = availabilityRepository.findById(availabilityId)
                .orElseThrow(() -> new IllegalArgumentException("Availability not found"));

        availability.setIsDeleted(true);
        availability.setIsAvailable(false);
        availabilityRepository.save(availability);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultantAvailability> getConsultantAvailability(Consultant consultant, DayOfWeek dayOfWeek) {
        return availabilityRepository.findByConsultantAndDayOfWeekAndIsDeletedFalse(consultant, dayOfWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultantAvailability> getAllConsultantAvailability(Consultant consultant) {
        return availabilityRepository.findByConsultantAndIsDeletedFalse(consultant);
    }

    @Override
    public List<ConsultantSchedule> generateWeeklySchedule(Consultant consultant, LocalDate startOfWeek) {
        log.info("Generating weekly schedule for consultant {} starting {}", consultant.getId(), startOfWeek);

        List<ConsultantSchedule> weeklySchedules = new ArrayList<>();
        
        // Tạo schedule cho 7 ngày trong tuần
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            
            List<ConsultantSchedule> dailySchedules = generateDailySchedule(consultant, date);
            weeklySchedules.addAll(dailySchedules);
        }

        return weeklySchedules;
    }

    @Override
    public void generateWeeklyScheduleForAllConsultants(LocalDate startOfWeek) {
        log.info("Generating weekly schedule for all consultants starting {}", startOfWeek);

        List<Consultant> consultants = consultantRepository.findAll();
        
        for (Consultant consultant : consultants) {
            if (consultant.getIsDeleted() == null || !consultant.getIsDeleted()) {
                try {
                    generateWeeklySchedule(consultant, startOfWeek);
                } catch (Exception e) {
                    log.error("Error generating schedule for consultant {}: {}", consultant.getId(), e.getMessage());
                }
            }
        }
    }

    @Override
    public List<ConsultantSchedule> generateDailySchedule(Consultant consultant, LocalDate date) {
        log.debug("Generating daily schedule for consultant {} on {}", consultant.getId(), date);

        DayOfWeek dayOfWeek = date.getDayOfWeek();
        List<ConsultantAvailability> availabilities = 
                availabilityRepository.findByConsultantAndDayOfWeekAndIsDeletedFalse(consultant, dayOfWeek);

        List<ConsultantSchedule> schedules = new ArrayList<>();

        for (ConsultantAvailability availability : availabilities) {
            if (availability.getIsAvailable()) {
                // Kiểm tra xem schedule đã tồn tại chưa
                Optional<ConsultantSchedule> existingSchedule = 
                        scheduleRepository.findByConsultantAndScheduleDateAndTimeSlotAndIsDeletedFalse(
                                consultant, date, availability.getTimeSlot());

                if (existingSchedule.isEmpty()) {
                    ConsultantSchedule schedule = new ConsultantSchedule();
                    schedule.setConsultant(consultant);
                    schedule.setTimeSlot(availability.getTimeSlot());
                    schedule.setConsultantAvailability(availability);
                    schedule.setScheduleDate(date);
                    schedule.setStartTime(availability.getTimeSlot().getStartTime());
                    schedule.setEndTime(availability.getTimeSlot().getEndTime());
                    schedule.setStatus("AVAILABLE");
                    schedule.setCurrentBookings(0);
                    schedule.setMaxBookings(availability.getMaxBookings());
                    schedule.setNotes(availability.getNotes());
                    schedule.setIsDeleted(false);

                    schedules.add(scheduleRepository.save(schedule));
                }
            }
        }

        return schedules;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isConsultantAvailable(Consultant consultant, DayOfWeek dayOfWeek, TimeSlot timeSlot) {
        return availabilityRepository.isConsultantAvailable(consultant, dayOfWeek, timeSlot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableTimeSlots(Consultant consultant, DayOfWeek dayOfWeek) {
        return availabilityRepository.getAvailableTimeSlots(consultant, dayOfWeek);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Consultant> findAvailableConsultants(DayOfWeek dayOfWeek, TimeSlot timeSlot) {
        List<ConsultantAvailability> availabilities = 
                availabilityRepository.findAvailableConsultants(dayOfWeek, timeSlot);
        
        return availabilities.stream()
                .map(ConsultantAvailability::getConsultant)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void setWeeklyAvailability(Consultant consultant, List<DayOfWeek> workingDays, 
                                     List<TimeSlot> timeSlots, Integer maxBookings) {
        log.info("Setting weekly availability for consultant {}", consultant.getId());

        for (DayOfWeek day : workingDays) {
            for (TimeSlot timeSlot : timeSlots) {
                try {
                    createAvailability(consultant, day, timeSlot, maxBookings, null);
                } catch (IllegalArgumentException e) {
                    log.debug("Availability already exists for consultant {} on {} at {}", 
                            consultant.getId(), day, timeSlot.getStartTime());
                }
            }
        }
    }

    @Override
    public void copyAvailabilityToWeek(Consultant consultant, LocalDate fromWeek, LocalDate toWeek) {
        log.info("Copying availability for consultant {} from week {} to week {}", 
                consultant.getId(), fromWeek, toWeek);

        // Lấy tất cả availability của consultant
        List<ConsultantAvailability> availabilities = getAllConsultantAvailability(consultant);

        // Tạo schedule cho tuần mới dựa trên availability template
        generateWeeklySchedule(consultant, toWeek);
    }

    @Override
    public void bulkUpdateAvailability(Consultant consultant, DayOfWeek dayOfWeek, 
                                      List<TimeSlot> timeSlots, Boolean isAvailable, Integer maxBookings) {
        log.info("Bulk updating availability for consultant {} on {}", consultant.getId(), dayOfWeek);

        for (TimeSlot timeSlot : timeSlots) {
            Optional<ConsultantAvailability> availability = 
                    availabilityRepository.findByConsultantAndDayOfWeekAndTimeSlotAndIsDeletedFalse(
                            consultant, dayOfWeek, timeSlot);

            if (availability.isPresent()) {
                updateAvailability(availability.get().getId(), isAvailable, maxBookings, null);
            } else if (isAvailable != null && isAvailable) {
                createAvailability(consultant, dayOfWeek, timeSlot, maxBookings, null);
            }
        }
    }

    @Override
    public void createDefaultAvailabilityTemplate(Consultant consultant) {
        log.info("Creating default availability template for consultant {}", consultant.getId());

        // Lấy tất cả time slots active
        List<TimeSlot> timeSlots = timeSlotRepository.findByIsDeletedFalseAndIsActiveTrueOrderByStartTime();
        
        // Tạo availability cho thứ 2-6 (9AM-5PM)
        List<DayOfWeek> workingDays = Arrays.asList(
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
        );

        // Lọc time slots trong giờ làm việc (9AM-5PM)
        List<TimeSlot> workingHours = timeSlots.stream()
                .filter(ts -> !ts.getStartTime().isBefore(LocalTime.of(9, 0)) && 
                             !ts.getStartTime().isAfter(LocalTime.of(17, 0)))
                .collect(Collectors.toList());

        setWeeklyAvailability(consultant, workingDays, workingHours, 1);
    }

    @Override
    public void importAvailability(Consultant consultant, String availabilityData) {
        // TODO: Implement import logic (CSV, JSON, etc.)
        log.info("Importing availability for consultant {}", consultant.getId());
        throw new UnsupportedOperationException("Import functionality not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public String exportAvailability(Consultant consultant) {
        // TODO: Implement export logic
        log.info("Exporting availability for consultant {}", consultant.getId());
        throw new UnsupportedOperationException("Export functionality not implemented yet");
    }

    @Override
    @Transactional(readOnly = true)
    public long countActiveAvailabilities(Consultant consultant) {
        return availabilityRepository.countActiveAvailabilities(consultant);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getConsultantUtilizationRate(Consultant consultant, LocalDate startDate, LocalDate endDate) {
        // TODO: Implement utilization calculation
        log.info("Calculating utilization rate for consultant {} from {} to {}", 
                consultant.getId(), startDate, endDate);
        
        // Logic: (Total booked slots / Total available slots) * 100
        return 0.0; // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    public List<Consultant> findConsultantsWithMostAvailability(DayOfWeek dayOfWeek) {
        List<Object[]> results = availabilityRepository.findConsultantsWithMostAvailableSlots(dayOfWeek);
        
        return results.stream()
                .map(result -> (Consultant) result[0])
                .collect(Collectors.toList());
    }
} 