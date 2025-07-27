package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation,Integer> {
    List<Consultation> findConsultationsByConsultant(User consultant);
    
    List<Consultation> findConsultationsByCustomer(User customer);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.consultant = :consultant AND c.status = :status")
    List<Consultation> findConsultationsByConsultantAndStatus(User consultant, ConsultationStatus status);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.id =:id")
    Consultation findConsultationById(Integer id);
    
    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND CAST(c.createdAt AS date) = :date" +
            " AND (:status IS NULL OR c.status = :status) " +
            " AND (:userId IS NULL OR c.customer.id = :userId) " +
            " AND (:consultantId IS NULL OR c.consultant.id = :consultantId)")
    List<Consultation> findWithFilters(LocalDate date, ConsultationStatus status, Integer userId, Integer consultantId);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND CAST(c.createdAt AS date) = :date" +
            " AND (:status IS NULL OR c.status = :status) " +
            " AND (:userId IS NULL OR c.customer.id = :userId) " +
            " AND (:consultantId IS NULL OR c.consultant.id = :consultantId)")
    org.springframework.data.domain.Page<Consultation> findWithFiltersPaginated(LocalDate date, ConsultationStatus status, Integer userId, Integer consultantId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultant.id = :consultantId")
    long countByConsultantId(Integer consultantId);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultant.id = :consultantId AND c.status = :status")
    long countByConsultantIdAndStatus(Integer consultantId, ConsultationStatus status);

    // Kiểm tra xem consultant đã có booking trong timeslot cụ thể chưa
    @Query("SELECT COUNT(c) > 0 FROM Consultation c WHERE c.consultant.id = :consultantId " +
           "AND c.timeSlot.timeSlotID = :timeSlotId " +
           "AND c.timeSlot.slotDate = :date " +
           "AND c.isDeleted = false")
    boolean existsByConsultantIdAndTimeSlotAndDate(@Param("consultantId") Integer consultantId, 
                                                  @Param("timeSlotId") Integer timeSlotId, 
                                                  @Param("date") LocalDate date);
    
    // Dashboard methods
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.customer.id = :customerId AND c.status = :status")
    long countByCustomer_IdAndStatus(@Param("customerId") Integer customerId, @Param("status") ConsultationStatus status);
    
    @Query("SELECT c FROM Consultation c WHERE c.customer.id = :customerId AND c.timeSlot.slotDate > :date ORDER BY c.timeSlot.slotDate ASC")
    List<Consultation> findByCustomer_IdAndTimeSlot_SlotDateAfterOrderByTimeSlot_SlotDateAsc(@Param("customerId") Integer customerId, @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.customer.id = :customerId AND c.timeSlot.slotDate > :date")
    long countByCustomer_IdAndTimeSlot_SlotDateAfter(@Param("customerId") Integer customerId, @Param("date") LocalDate date);
}
