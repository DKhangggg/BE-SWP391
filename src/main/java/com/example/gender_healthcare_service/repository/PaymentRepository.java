package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByCustomer(User customer);
    
    // Find payments by consultant (through consultation)
    @Query("SELECT p FROM Payment p WHERE p.consultation.consultant = :consultant AND p.isDeleted = false")
    List<Payment> findByConsultation_ConsultantAndIsDeletedFalse(@Param("consultant") User consultant);
    
    // Find payments by consultant and date range
    @Query("SELECT p FROM Payment p WHERE p.consultation.consultant = :consultant " +
           "AND p.paymentDate BETWEEN :startDate AND :endDate AND p.isDeleted = false")
    List<Payment> findByConsultation_ConsultantAndPaymentDateBetweenAndIsDeletedFalse(
            @Param("consultant") User consultant, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);
}

