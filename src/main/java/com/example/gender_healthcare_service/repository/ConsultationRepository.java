package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation,Integer> {
    List<Consultation> findByCustomerAndIsDeletedFalse(User customer);
    List<Consultation> findByConsultantAndIsDeletedFalse(User consultant);
    List<Consultation> findByStatusAndIsDeletedFalse(ConsultationStatus status);
    List<Consultation> findByConsultationDateBetweenAndIsDeletedFalse(Instant start, Instant end);
    List<Consultation> findByConsultantAndStatusAndIsDeletedFalse(User consultant, ConsultationStatus status);
    List<Consultation> findByCustomerAndStatusAndIsDeletedFalse(User customer, ConsultationStatus status);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.id =:id")
    Consultation findConsultationById(Integer id);
    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.createdAt =:date" +
            " AND (:status IS NULL OR c.status = :status) " +
            " AND (:userId IS NULL OR c.customer.id = :userId) " +
            " AND (:consultantId IS NULL OR c.consultant.id = :consultantId)")
    List<Consultation> findWithFilters(LocalDate date, String status, Integer userId, Integer consultantId);
}
