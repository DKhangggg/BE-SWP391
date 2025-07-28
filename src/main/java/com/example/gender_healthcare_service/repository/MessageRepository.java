package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Conversation;
import com.example.gender_healthcare_service.entity.Message;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {
    
    // Lấy tin nhắn trong conversation theo thứ tự thời gian
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.isDeleted = false ORDER BY m.createdAt ASC")
    List<Message> findByConversationOrderByCreatedAtAsc(@Param("conversation") Conversation conversation);
    
    // Lấy tin nhắn mới nhất trong conversation
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation AND m.isDeleted = false ORDER BY m.createdAt DESC")
    List<Message> findByConversationOrderByCreatedAtDesc(@Param("conversation") Conversation conversation);
    
    // Đếm tin nhắn chưa đọc của user trong conversation
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation AND m.sender != :user AND m.status != 'READ' AND m.isDeleted = false")
    Long countUnreadMessagesByUserInConversation(@Param("conversation") Conversation conversation, @Param("user") User user);
    
    // Đánh dấu tất cả tin nhắn trong conversation đã đọc
    @Query("UPDATE Message m SET m.status = 'READ', m.readAt = CURRENT_TIMESTAMP WHERE m.conversation = :conversation AND m.sender != :user AND m.status != 'read'")
    void markAllMessagesAsReadInConversation(@Param("conversation") Conversation conversation, @Param("user") User user);
}
