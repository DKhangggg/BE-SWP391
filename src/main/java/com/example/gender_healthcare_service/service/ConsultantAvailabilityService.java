package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantAvailability;
import com.example.gender_healthcare_service.entity.ConsultantSchedule;
import com.example.gender_healthcare_service.entity.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultantAvailabilityService {

    ConsultantAvailability createAvailability(Consultant consultant, DayOfWeek dayOfWeek, 
                                            TimeSlot timeSlot, Integer maxBookings, String notes);
    ConsultantAvailability updateAvailability(Integer availabilityId, Boolean isAvailable, 
                                            Integer maxBookings, String notes);
    void deleteAvailability(Integer availabilityId);
    List<ConsultantAvailability> getConsultantAvailability(Consultant consultant, DayOfWeek dayOfWeek);

    List<ConsultantAvailability> getAllConsultantAvailability(Consultant consultant);

    List<ConsultantSchedule> generateWeeklySchedule(Consultant consultant, LocalDate startOfWeek);

    void generateWeeklyScheduleForAllConsultants(LocalDate startOfWeek);

    List<ConsultantSchedule> generateDailySchedule(Consultant consultant, LocalDate date);
    boolean isConsultantAvailable(Consultant consultant, DayOfWeek dayOfWeek, TimeSlot timeSlot);

    List<TimeSlot> getAvailableTimeSlots(Consultant consultant, DayOfWeek dayOfWeek);
    List<Consultant> findAvailableConsultants(DayOfWeek dayOfWeek, TimeSlot timeSlot);
    void setWeeklyAvailability(Consultant consultant, List<DayOfWeek> workingDays, 
                              List<TimeSlot> timeSlots, Integer maxBookings);

    void copyAvailabilityToWeek(Consultant consultant, LocalDate fromWeek, LocalDate toWeek);

    void bulkUpdateAvailability(Consultant consultant, DayOfWeek dayOfWeek, 
                               List<TimeSlot> timeSlots, Boolean isAvailable, Integer maxBookings);

    void createDefaultAvailabilityTemplate(Consultant consultant);

    void importAvailability(Consultant consultant, String availabilityData);

    String exportAvailability(Consultant consultant);
    long countActiveAvailabilities(Consultant consultant);
    Double getConsultantUtilizationRate(Consultant consultant, LocalDate startDate, LocalDate endDate);
    List<Consultant> findConsultantsWithMostAvailability(DayOfWeek dayOfWeek);
} 