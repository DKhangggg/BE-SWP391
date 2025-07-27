package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    
    // Find available time slots for a specific date
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate = :date AND ts.isAvailable = true AND ts.bookedCount < ts.capacity")
    List<TimeSlot> findAvailableTimeSlotsByDate(@Param("date") LocalDate date);
    
    // Find available time slots for a specific consultant and date
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate = :date AND ts.consultant.id = :consultantId AND ts.isAvailable = true AND ts.bookedCount < ts.capacity")
    List<TimeSlot> findAvailableTimeSlotsByDateAndConsultant(@Param("date") LocalDate date, @Param("consultantId") Integer consultantId);
    
    // Find facility time slots (consultant is null) for a specific date
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate = :date AND ts.consultant IS NULL AND ts.isAvailable = true AND ts.bookedCount < ts.capacity")
    List<TimeSlot> findAvailableFacilityTimeSlotsByDate(@Param("date") LocalDate date);
    
    // Find facility time slots from today onwards
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate >= :date AND ts.consultant IS NULL AND ts.isAvailable = true AND ts.bookedCount < ts.capacity ORDER BY ts.slotDate, ts.startTime")
    List<TimeSlot> findAvailableFacilityTimeSlotsFromDate(@Param("date") LocalDate date);
    
    // Find time slots by slot type
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotType = :slotType AND ts.isAvailable = true")
    List<TimeSlot> findBySlotType(@Param("slotType") String slotType);
    
    // Find time slots by consultant
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.consultant.id = :consultantId AND ts.isAvailable = true")
    List<TimeSlot> findByConsultantId(@Param("consultantId") Integer consultantId);
    
    // Find time slots by date range
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.slotDate BETWEEN :startDate AND :endDate AND ts.isAvailable = true")
    List<TimeSlot> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Check if time slot exists for specific date and time
    @Query("SELECT COUNT(ts) > 0 FROM TimeSlot ts WHERE ts.slotDate = :date AND ts.startTime = :startTime AND ts.endTime = :endTime AND ts.consultant.id = :consultantId")
    boolean existsByDateAndTimeAndConsultant(@Param("date") LocalDate date, @Param("startTime") String startTime, 
                                           @Param("endTime") String endTime, @Param("consultantId") Integer consultantId);

    // Find active time slots ordered by start time
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.isAvailable = true ORDER BY ts.startTime")
    List<TimeSlot> findByIsActiveTrueOrderByStartTime();

    @Query("SELECT t FROM TimeSlot t WHERE t.consultant.id = :consultantId AND t.slotDate BETWEEN :fromDate AND :toDate AND t.isAvailable = true")
    List<TimeSlot> findAvailableByConsultantAndDateRange(@Param("consultantId") Integer consultantId,
                                                     @Param("fromDate") LocalDate fromDate,
                                                     @Param("toDate") LocalDate toDate);
    @Query("SELECT t FROM TimeSlot t WHERE t.isAvailable = true ORDER BY t.startTime")
    List<TimeSlot> findByIsAvailableTrueOrderByStartTime();
}

