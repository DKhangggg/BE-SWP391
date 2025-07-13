package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation,Integer> {
    List<Consultation> findConsultationsByConsultant(User consultant);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.consultant = :consultant AND c.status = :status")
    List<Consultation> findConsultationsByConsultantAndStatus(User consultant, ConsultationStatus status);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.id =:id")
    Consultation findConsultationById(Integer id);
    
    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.createdAt =:date" +
            " AND (:status IS NULL OR c.status = :status) " +
            " AND (:userId IS NULL OR c.customer.id = :userId) " +
            " AND (:consultantId IS NULL OR c.consultant.id = :consultantId)")
    List<Consultation> findWithFilters(LocalDate date, ConsultationStatus status, Integer userId, Integer consultantId);

    @Query("SELECT c FROM Consultation c WHERE c.isDeleted = false AND c.createdAt =:date" +
            " AND (:status IS NULL OR c.status = :status) " +
            " AND (:userId IS NULL OR c.customer.id = :userId) " +
            " AND (:consultantId IS NULL OR c.consultant.id = :consultantId)")
    org.springframework.data.domain.Page<Consultation> findWithFiltersPaginated(LocalDate date, ConsultationStatus status, Integer userId, Integer consultantId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultant.id = :consultantId")
    long countByConsultantId(Integer consultantId);

    @Query("SELECT COUNT(c) FROM Consultation c WHERE c.consultant.id = :consultantId AND c.status = :status")
    long countByConsultantIdAndStatus(Integer consultantId, ConsultationStatus status);
}
