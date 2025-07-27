package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Chat;
import com.example.gender_healthcare_service.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findByConversationOrderByCreatedAtAsc(@Param("conversation") Chat conversation);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.status = 'SENT' AND m.readAt IS NULL AND m.isDeleted = false")
    Long countUnreadMessagesByConversation(@Param("conversation") Chat conversation);
    
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Message> findLastMessageByConversation(@Param("conversation") Chat conversation);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.isDeleted = false")
    Long countByConversation(@Param("conversation") Chat conversation);
} 