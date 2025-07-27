package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Booking;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByCustomerID(User customer);
    Optional<Booking> findByIdAndCustomerID(Integer id, User customer);
    
    // Check if user has booking for specific time slot
    boolean existsByCustomerIDAndTimeSlotAndStatusNot(User customer, TimeSlot timeSlot, String status);
    
    // Check if time slot has any active bookings
    boolean existsByTimeSlotAndStatusNot(TimeSlot timeSlot, String status);

    long countByStatus(String status);

    @Query("SELECT COUNT(DISTINCT b.customerID) FROM Booking b WHERE b.timeSlot.slotDate > :thirtyDaysAgo")
    long countDistinctUsersByBookingDateAfter(LocalDate thirtyDaysAgo);

    long countByTimeSlot_SlotDate(LocalDate bookingDate);

    long countByTimeSlot_SlotDateAndStatus(LocalDate bookingDate, String status);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId")
    long countByServiceId(Integer serviceId);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId AND b.status = :status")
    long countByServiceIdAndStatus(Integer serviceId, String status);

    @Query("SELECT b FROM Booking b WHERE b.customerID = :customer AND b.isDeleted = false")
    List<Booking> findByCustomerIDAndIsDeletedFalse(User customer);
    
    // Dashboard methods
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customerID.id = :customerId AND b.status = :status")
    long countByCustomerID_IdAndStatus(@Param("customerId") Integer customerId, @Param("status") String status);
    
    @Query("SELECT b FROM Booking b WHERE b.customerID.id = :customerId AND b.timeSlot.slotDate > :date ORDER BY b.timeSlot.slotDate ASC")
    List<Booking> findByCustomerID_IdAndTimeSlot_SlotDateAfterOrderByTimeSlot_SlotDateAsc(@Param("customerId") Integer customerId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.customerID.id = :customerId AND b.timeSlot.slotDate > :date")
    long countByCustomerID_IdAndTimeSlot_SlotDateAfter(@Param("customerId") Integer customerId, @Param("date") LocalDate date);
    
    // Pagination and filtering methods
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false")
    Page<Booking> findAllActive(Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false AND b.status = :status")
    Page<Booking> findByStatus(@Param("status") String status, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false AND b.customerID.id = :customerId")
    Page<Booking> findByCustomerId(@Param("customerId") Integer customerId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false AND b.service.id = :serviceId")
    Page<Booking> findByServiceId(@Param("serviceId") Integer serviceId, Pageable pageable);
    
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false AND b.timeSlot.slotDate BETWEEN :fromDate AND :toDate")
    Page<Booking> findByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate, Pageable pageable);
    
    // Advanced filtering with multiple criteria
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false " +
           "AND (:status IS NULL OR b.status = :status) " +
           "AND (:customerId IS NULL OR b.customerID.id = :customerId) " +
           "AND (:serviceId IS NULL OR b.service.id = :serviceId) " +
           "AND (:fromDate IS NULL OR b.timeSlot.slotDate >= :fromDate) " +
           "AND (:toDate IS NULL OR b.timeSlot.slotDate <= :toDate) " +
           "AND (:customerName IS NULL OR LOWER(b.customerID.fullName) LIKE LOWER(CONCAT('%', :customerName, '%'))) " +
           "AND (:serviceName IS NULL OR LOWER(b.service.serviceName) LIKE LOWER(CONCAT('%', :serviceName, '%')))")
    Page<Booking> findByFilters(
            @Param("status") String status,
            @Param("customerId") Integer customerId,
            @Param("serviceId") Integer serviceId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("customerName") String customerName,
            @Param("serviceName") String serviceName,
            Pageable pageable);
    
    // Statistics queries
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false")
    long countAllActive();
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false AND b.status = :status")
    long countByStatusActive(@Param("status") String status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false AND b.timeSlot.slotDate BETWEEN :fromDate AND :toDate")
    long countByDateRange(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false AND b.customerID.id = :customerId")
    long countByCustomerId(@Param("customerId") Integer customerId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.isDeleted = false AND b.service.id = :serviceId")
    long countByServiceIdActive(@Param("serviceId") Integer serviceId);

    // Find bookings by customer
    List<Booking> findByCustomerIDAndIsDeletedFalseOrderByCreatedAtDesc(User customer);
    
    // Find bookings by status
    List<Booking> findByStatusAndIsDeletedFalse(String status);
    
    // Find bookings by service
    @Query("SELECT b FROM Booking b WHERE b.service.id = :serviceId AND b.isDeleted = false")
    List<Booking> findByServiceIdAndIsDeletedFalse(@Param("serviceId") Integer serviceId);
    
    // Find bookings by time slot
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.id = :timeSlotId AND b.isDeleted = false")
    List<Booking> findByTimeSlotIdAndIsDeletedFalse(@Param("timeSlotId") Integer timeSlotId);
    
    // Find bookings by date range
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.slotDate BETWEEN :startDate AND :endDate AND b.isDeleted = false")
    List<Booking> findByBookingDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Find bookings by customer and status
    List<Booking> findByCustomerIDAndStatusAndIsDeletedFalse(User customer, String status);
    
    // Find bookings by service and status
    @Query("SELECT b FROM Booking b WHERE b.service.id = :serviceId AND b.status = :status AND b.isDeleted = false")
    List<Booking> findByServiceIdAndStatusAndIsDeletedFalse(@Param("serviceId") Integer serviceId, @Param("status") String status);
    
    // Find active bookings (not cancelled or completed)
    @Query("SELECT b FROM Booking b WHERE b.status NOT IN ('CANCELLED', 'COMPLETED') AND b.isDeleted = false")
    List<Booking> findActiveBookings();
    
    // Find pending bookings
    List<Booking> findByStatusAndIsDeletedFalseOrderByCreatedAtAsc(String status);
    
    // Count bookings by status
    long countByStatusAndIsDeletedFalse(String status);
    
    // Count bookings by service
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.service.id = :serviceId AND b.isDeleted = false")
    long countByServiceIdAndIsDeletedFalse(@Param("serviceId") Integer serviceId);
    
    // Count bookings by customer
    long countByCustomerIDAndIsDeletedFalse(User customer);
    
    // Find bookings with pagination and filtering
    @Query("SELECT b FROM Booking b WHERE " +
           "(:customerId IS NULL OR b.customerID.id = :customerId) AND " +
           "(:serviceId IS NULL OR b.service.id = :serviceId) AND " +
           "(:status IS NULL OR b.status = :status) AND " +
           "(:startDate IS NULL OR b.timeSlot.slotDate >= :startDate) AND " +
           "(:endDate IS NULL OR b.timeSlot.slotDate <= :endDate) AND " +
           "b.isDeleted = false " +
           "ORDER BY b.createdAt DESC")
    Page<Booking> findBookingsWithFilters(
            @Param("customerId") Integer customerId,
            @Param("serviceId") Integer serviceId,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // Find bookings for admin/staff view
    @Query("SELECT b FROM Booking b WHERE b.isDeleted = false ORDER BY b.createdAt DESC")
    Page<Booking> findAllActiveBookings(Pageable pageable);
    
    // Find bookings by consultant
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.consultant.id = :consultantId AND b.isDeleted = false ORDER BY b.createdAt DESC")
    Page<Booking> findByConsultantId(@Param("consultantId") Integer consultantId, Pageable pageable);
    
    // Find bookings by date
    @Query("SELECT b FROM Booking b WHERE b.timeSlot.slotDate = :date AND b.isDeleted = false ORDER BY b.createdAt DESC")
    List<Booking> findByBookingDate(@Param("date") LocalDate date);
    
    // Count bookings by date
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.timeSlot.slotDate = :date AND b.isDeleted = false")
    long countByBookingDate(@Param("date") LocalDate date);
    
    // Count bookings by date and status
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.timeSlot.slotDate = :date AND b.status = :status AND b.isDeleted = false")
    long countByBookingDateAndStatus(@Param("date") LocalDate date, @Param("status") String status);
    
    // Find booking by ID with customer info
    @Query("SELECT b FROM Booking b WHERE b.id = :id AND b.isDeleted = false")
    Optional<Booking> findByIdAndIsDeletedFalse(@Param("id") Integer id);
    
    // Check if customer has booking for time slot
    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.customerID.id = :customerId AND b.timeSlot.id = :timeSlotId AND b.isDeleted = false")
    boolean existsByCustomerIdAndTimeSlotId(@Param("customerId") Integer customerId, @Param("timeSlotId") Integer timeSlotId);
    
    // Find booking by ID and customer ID for cancellation
    @Query("SELECT b FROM Booking b WHERE b.id = :bookingId AND b.customerID.id = :customerId AND b.isDeleted = false")
    Optional<Booking> findByIdAndCustomerIdAndIsDeletedFalse(@Param("bookingId") Integer bookingId, @Param("customerId") Integer customerId);
    
    // Get booking statistics
    @Query("SELECT b.status, COUNT(b) FROM Booking b WHERE b.isDeleted = false GROUP BY b.status")
    List<Object[]> getBookingStatistics();
    
    // Get booking statistics by date range
    @Query("SELECT b.status, COUNT(b) FROM Booking b WHERE b.timeSlot.slotDate BETWEEN :startDate AND :endDate AND b.isDeleted = false GROUP BY b.status")
    List<Object[]> getBookingStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
