package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantSchedule;
import com.example.gender_healthcare_service.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultantScheduleRepository extends JpaRepository<ConsultantSchedule, Integer> {

    // Tìm schedule của consultant trong khoảng thời gian
    List<ConsultantSchedule> findByConsultantIdAndScheduleDateBetweenAndIsDeletedFalse(
            Integer consultantId, LocalDate startDate, LocalDate endDate);

    // Tìm schedule của consultant theo ngày cụ thể
    List<ConsultantSchedule> findByConsultantAndScheduleDateAndIsDeletedFalse(
            Consultant consultant, LocalDate scheduleDate);

    // Tìm schedule cụ thể của consultant cho một time slot trong một ngày
    Optional<ConsultantSchedule> findByConsultantAndScheduleDateAndTimeSlotAndIsDeletedFalse(
            Consultant consultant, LocalDate scheduleDate, TimeSlot timeSlot);

    // Tìm tất cả schedule available cho booking
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.scheduleDate = :date " +
           "AND cs.status = 'AVAILABLE' AND cs.currentBookings < cs.maxBookings " +
           "AND cs.isDeleted = false ORDER BY cs.startTime")
    List<ConsultantSchedule> findAvailableSchedules(@Param("date") LocalDate date);

    // Tìm schedule available của consultant cụ thể
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.consultant = :consultant " +
           "AND cs.scheduleDate = :date AND cs.status = 'AVAILABLE' " +
           "AND cs.currentBookings < cs.maxBookings AND cs.isDeleted = false " +
           "ORDER BY cs.startTime")
    List<ConsultantSchedule> findAvailableSchedulesByConsultant(
            @Param("consultant") Consultant consultant, @Param("date") LocalDate date);

    // Tìm schedule theo time slot trong ngày cụ thể
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.scheduleDate = :date " +
           "AND cs.timeSlot = :timeSlot AND cs.isDeleted = false")
    List<ConsultantSchedule> findByDateAndTimeSlot(@Param("date") LocalDate date, 
                                                  @Param("timeSlot") TimeSlot timeSlot);

    // Kiểm tra consultant có schedule trong time slot cụ thể không
    @Query("SELECT COUNT(cs) > 0 FROM ConsultantSchedule cs WHERE cs.consultant = :consultant " +
           "AND cs.scheduleDate = :date AND cs.timeSlot = :timeSlot AND cs.isDeleted = false")
    boolean existsByConsultantAndDateAndTimeSlot(@Param("consultant") Consultant consultant,
                                               @Param("date") LocalDate date,
                                               @Param("timeSlot") TimeSlot timeSlot);

    // Lấy schedule trong tuần của consultant
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.consultant = :consultant " +
           "AND cs.scheduleDate BETWEEN :startOfWeek AND :endOfWeek " +
           "AND cs.isDeleted = false ORDER BY cs.scheduleDate, cs.startTime")
    List<ConsultantSchedule> findWeeklySchedule(@Param("consultant") Consultant consultant,
                                              @Param("startOfWeek") LocalDate startOfWeek,
                                              @Param("endOfWeek") LocalDate endOfWeek);

    // Tìm schedule có thể accept thêm booking
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.consultant = :consultant " +
           "AND cs.scheduleDate >= :fromDate AND cs.status = 'AVAILABLE' " +
           "AND cs.currentBookings < cs.maxBookings AND cs.isDeleted = false " +
           "ORDER BY cs.scheduleDate, cs.startTime")
    List<ConsultantSchedule> findBookableSchedules(@Param("consultant") Consultant consultant,
                                                  @Param("fromDate") LocalDate fromDate);

    // Đếm số booking hiện tại trong schedule
    @Query("SELECT cs.currentBookings FROM ConsultantSchedule cs WHERE cs.id = :scheduleId")
    Integer getCurrentBookingCount(@Param("scheduleId") Integer scheduleId);

    // Tìm schedule theo status trong ngày
    List<ConsultantSchedule> findByScheduleDateAndStatusAndIsDeletedFalse(LocalDate date, String status);

    // Lấy tổng số slot available trong ngày
    @Query("SELECT SUM(cs.maxBookings - cs.currentBookings) FROM ConsultantSchedule cs " +
           "WHERE cs.scheduleDate = :date AND cs.status = 'AVAILABLE' AND cs.isDeleted = false")
    Long getTotalAvailableSlots(@Param("date") LocalDate date);

    // Tìm consultant có nhiều slot trống nhất trong ngày
    @Query("SELECT cs.consultant, SUM(cs.maxBookings - cs.currentBookings) as availableSlots " +
           "FROM ConsultantSchedule cs " +
           "WHERE cs.scheduleDate = :date AND cs.status = 'AVAILABLE' AND cs.isDeleted = false " +
           "GROUP BY cs.consultant ORDER BY availableSlots DESC")
    List<Object[]> findConsultantsWithMostAvailableSlots(@Param("date") LocalDate date);

    // Tìm schedule theo availability template
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.consultantAvailability = :availability " +
           "AND cs.scheduleDate = :date AND cs.isDeleted = false")
    Optional<ConsultantSchedule> findByAvailabilityAndDate(
            @Param("availability") com.example.gender_healthcare_service.entity.ConsultantAvailability availability,
            @Param("date") LocalDate date);

    // Lấy schedule history của consultant
    @Query("SELECT cs FROM ConsultantSchedule cs WHERE cs.consultant = :consultant " +
           "AND cs.scheduleDate <= :endDate AND cs.isDeleted = false " +
           "ORDER BY cs.scheduleDate DESC, cs.startTime")
    List<ConsultantSchedule> findScheduleHistory(@Param("consultant") Consultant consultant,
                                               @Param("endDate") LocalDate endDate);
}
