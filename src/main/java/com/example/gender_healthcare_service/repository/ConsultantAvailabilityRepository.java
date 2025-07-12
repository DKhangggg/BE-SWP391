package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantAvailability;
import com.example.gender_healthcare_service.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultantAvailabilityRepository extends JpaRepository<ConsultantAvailability, Integer> {

    // Tìm availability của consultant theo ngày trong tuần
    List<ConsultantAvailability> findByConsultantAndDayOfWeekAndIsDeletedFalse(Consultant consultant, DayOfWeek dayOfWeek);

    // Tìm availability của consultant cho tất cả ngày trong tuần
    List<ConsultantAvailability> findByConsultantAndIsDeletedFalse(Consultant consultant);

    // Tìm availability cụ thể
    Optional<ConsultantAvailability> findByConsultantAndDayOfWeekAndTimeSlotAndIsDeletedFalse(
            Consultant consultant, DayOfWeek dayOfWeek, TimeSlot timeSlot);

    // Tìm tất cả consultant available cho một time slot trong ngày cụ thể
    @Query("SELECT ca FROM ConsultantAvailability ca WHERE ca.dayOfWeek = :dayOfWeek " +
           "AND ca.timeSlot = :timeSlot AND ca.isAvailable = true AND ca.isDeleted = false")
    List<ConsultantAvailability> findAvailableConsultants(@Param("dayOfWeek") DayOfWeek dayOfWeek, 
                                                          @Param("timeSlot") TimeSlot timeSlot);

    // Tìm availability theo consultant ID và day of week
    @Query("SELECT ca FROM ConsultantAvailability ca WHERE ca.consultant.id = :consultantId " +
           "AND ca.dayOfWeek = :dayOfWeek AND ca.isDeleted = false")
    List<ConsultantAvailability> findByConsultantIdAndDayOfWeek(@Param("consultantId") Integer consultantId, 
                                                               @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Kiểm tra xem consultant có available trong time slot cụ thể không
    @Query("SELECT COUNT(ca) > 0 FROM ConsultantAvailability ca WHERE ca.consultant = :consultant " +
           "AND ca.dayOfWeek = :dayOfWeek AND ca.timeSlot = :timeSlot " +
           "AND ca.isAvailable = true AND ca.isDeleted = false")
    boolean isConsultantAvailable(@Param("consultant") Consultant consultant, 
                                 @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                 @Param("timeSlot") TimeSlot timeSlot);

    // Lấy tất cả time slots available của consultant trong một ngày
    @Query("SELECT ca.timeSlot FROM ConsultantAvailability ca WHERE ca.consultant = :consultant " +
           "AND ca.dayOfWeek = :dayOfWeek AND ca.isAvailable = true AND ca.isDeleted = false " +
           "ORDER BY ca.timeSlot.startTime")
    List<TimeSlot> getAvailableTimeSlots(@Param("consultant") Consultant consultant, 
                                        @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // Đếm số lượng availability của consultant
    @Query("SELECT COUNT(ca) FROM ConsultantAvailability ca WHERE ca.consultant = :consultant " +
           "AND ca.isAvailable = true AND ca.isDeleted = false")
    long countActiveAvailabilities(@Param("consultant") Consultant consultant);

    // Tìm consultant có nhiều slot available nhất trong một ngày
    @Query("SELECT ca.consultant, COUNT(ca) as slotCount FROM ConsultantAvailability ca " +
           "WHERE ca.dayOfWeek = :dayOfWeek AND ca.isAvailable = true AND ca.isDeleted = false " +
           "GROUP BY ca.consultant ORDER BY slotCount DESC")
    List<Object[]> findConsultantsWithMostAvailableSlots(@Param("dayOfWeek") DayOfWeek dayOfWeek);
} 