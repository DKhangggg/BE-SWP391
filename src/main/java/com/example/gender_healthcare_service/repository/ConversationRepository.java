package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Conversation;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Integer> {
    
    // Tìm conversations theo customer
    @Query("SELECT c FROM Conversation c WHERE c.customer = :customer AND c.isDeleted = false ORDER BY c.updatedAt DESC")
    List<Conversation> findByCustomer(@Param("customer") User customer);
    
    // Tìm conversations theo consultant
    @Query("SELECT c FROM Conversation c WHERE c.consultant = :consultant AND c.isDeleted = false ORDER BY c.updatedAt DESC")
    List<Conversation> findByConsultant(@Param("consultant") User consultant);
    
    // Tìm conversation theo customer và consultant
    @Query("SELECT c FROM Conversation c WHERE c.customer = :customer AND c.consultant = :consultant AND c.isDeleted = false")
    Optional<Conversation> findByCustomerAndConsultant(@Param("customer") User customer, @Param("consultant") User consultant);
    
    // Tìm conversations active
    @Query("SELECT c FROM Conversation c WHERE c.status = 'ACTIVE' AND c.isDeleted = false")
    List<Conversation> findActiveConversations();
    
    // Đếm số conversations chưa đọc của customer
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.customer = :customer AND c.customerUnreadCount > 0 AND c.isDeleted = false")
    Long countUnreadConversationsByCustomer(@Param("customer") User customer);
    
    // Đếm số conversations chưa đọc của consultant
    @Query("SELECT COUNT(c) FROM Conversation c WHERE c.consultant = :consultant AND c.consultantUnreadCount > 0 AND c.isDeleted = false")
    Long countUnreadConversationsByConsultant(@Param("consultant") User consultant);
}
