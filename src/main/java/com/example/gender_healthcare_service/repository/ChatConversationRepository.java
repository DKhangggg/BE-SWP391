package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Integer> {
    
    // Lấy conversation giữa user và consultant
    Optional<ChatConversation> findByUserIdAndConsultantId(Integer userId, Integer consultantId);
    
    // Lấy tất cả conversation của user
    List<ChatConversation> findByUserIdOrderByUpdatedAtDesc(Integer userId);
    
    // Lấy tất cả conversation của consultant
    List<ChatConversation> findByConsultantIdOrderByUpdatedAtDesc(Integer consultantId);
    
    // Lấy conversation có tin nhắn chưa đọc
    @Query("SELECT cc FROM ChatConversation cc WHERE " +
           "(cc.user.id = :userId OR cc.consultant.id = :userId) AND cc.unreadCount > 0 " +
           "ORDER BY cc.updatedAt DESC")
    List<ChatConversation> findConversationsWithUnreadMessages(@Param("userId") Integer userId);
    
    // Đếm conversation chưa đọc của user
    @Query("SELECT COUNT(cc) FROM ChatConversation cc WHERE cc.user.id = :userId AND cc.unreadCount > 0")
    Long countUnreadConversationsForUser(@Param("userId") Integer userId);
    
    // Đếm conversation chưa đọc của consultant
    @Query("SELECT COUNT(cc) FROM ChatConversation cc WHERE cc.consultant.id = :consultantId AND cc.unreadCount > 0")
    Long countUnreadConversationsForConsultant(@Param("consultantId") Integer consultantId);
} 