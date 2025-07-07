package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomerID(User customer);
    Optional<Booking> findByIdAndCustomerID(Integer id, User customer);
    boolean existsByCustomerIDAndBookingDateAndTimeSlotTimeSlotID(User customer, LocalDate bookingDate, Integer timeSlotId);
    boolean existsByBookingDateAndTimeSlotTimeSlotIDAndStatusNot(LocalDate bookingDate, Integer timeSlotId, String status);

    long countByStatus(String status);

    @Query("SELECT COUNT(DISTINCT b.customerID) FROM Booking b WHERE b.bookingDate > :thirtyDaysAgo")
    long countDistinctUsersByBookingDateAfter(LocalDate thirtyDaysAgo);

    long countByBookingDate(LocalDate bookingDate);

    long countByBookingDateAndStatus(LocalDate bookingDate, String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId")
    long countByServiceId(Integer serviceId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId AND b.status = :status")
    long countByServiceIdAndStatus(Integer serviceId, String status);

    @Query("SELECT b FROM Booking b WHERE b.customerID = :customer AND b.isDeleted = false")
    List<Booking> findByCustomerIDAndIsDeletedFalse(User customer);
}
