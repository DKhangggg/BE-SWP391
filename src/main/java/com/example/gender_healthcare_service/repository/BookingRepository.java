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

    @Query("SELECT b FROM Booking b WHERE b.customerID = :customer AND b.isDeleted = false")
    List<Booking> findByCustomerIDAndIsDeletedFalse(User customer);
}
