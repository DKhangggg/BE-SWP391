package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    
    // Tìm tất cả time slots active
    List<TimeSlot> findByIsDeletedFalseAndIsActiveTrueOrderByStartTime();
    
    // Tìm time slots theo thời gian bắt đầu
    List<TimeSlot> findByStartTimeAndIsDeletedFalseAndIsActiveTrue(LocalTime startTime);
    
    // Tìm time slots trong khoảng thời gian
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.startTime >= :startTime AND ts.endTime <= :endTime " +
           "AND ts.isDeleted = false AND ts.isActive = true ORDER BY ts.startTime")
    List<TimeSlot> findTimeSlotsBetween(@Param("startTime") LocalTime startTime, 
                                       @Param("endTime") LocalTime endTime);
    
    // Tìm time slot theo slot number
    Optional<TimeSlot> findBySlotNumberAndIsDeletedFalseAndIsActiveTrue(Integer slotNumber);
    
    // Tìm time slots có duration cụ thể
    List<TimeSlot> findByDurationAndIsDeletedFalseAndIsActiveTrueOrderByStartTime(Integer duration);
    
    // Tìm time slots có overlap với thời gian cho trước
    @Query("SELECT ts FROM TimeSlot ts WHERE " +
           "(ts.startTime < :endTime AND ts.endTime > :startTime) " +
           "AND ts.isDeleted = false AND ts.isActive = true")
    List<TimeSlot> findOverlappingTimeSlots(@Param("startTime") LocalTime startTime, 
                                           @Param("endTime") LocalTime endTime);
    
    // Tìm time slots available cho booking (có ít nhất 1 consultant available)
    @Query("SELECT DISTINCT ts FROM TimeSlot ts " +
           "JOIN ts.consultantAvailabilities ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek AND ca.isAvailable = true " +
           "AND ca.isDeleted = false AND ts.isDeleted = false AND ts.isActive = true " +
           "ORDER BY ts.startTime")
    List<TimeSlot> findAvailableTimeSlotsForDay(@Param("dayOfWeek") java.time.DayOfWeek dayOfWeek);
    
    // Đếm số lượng time slots active
    @Query("SELECT COUNT(ts) FROM TimeSlot ts WHERE ts.isDeleted = false AND ts.isActive = true")
    long countActiveTimeSlots();
    
    // Tìm time slots theo duration range
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.duration BETWEEN :minDuration AND :maxDuration " +
           "AND ts.isDeleted = false AND ts.isActive = true ORDER BY ts.startTime")
    List<TimeSlot> findByDurationRange(@Param("minDuration") Integer minDuration, 
                                      @Param("maxDuration") Integer maxDuration);
    
    // Tìm time slot gần nhất với thời gian cho trước
    @Query("SELECT ts FROM TimeSlot ts WHERE ts.startTime >= :targetTime " +
           "AND ts.isDeleted = false AND ts.isActive = true " +
           "ORDER BY ts.startTime ASC")
    List<TimeSlot> findNextAvailableTimeSlots(@Param("targetTime") LocalTime targetTime);
    
    // Lấy tất cả time slots được sử dụng bởi consultant
    @Query("SELECT DISTINCT ts FROM TimeSlot ts " +
           "JOIN ts.consultantAvailabilities ca " +
           "WHERE ca.consultant.id = :consultantId AND ca.isDeleted = false " +
           "AND ts.isDeleted = false AND ts.isActive = true " +
           "ORDER BY ts.startTime")
    List<TimeSlot> findTimeSlotsByConsultant(@Param("consultantId") Integer consultantId);
}

