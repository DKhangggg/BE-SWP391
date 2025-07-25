package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    
    // Lấy tin nhắn giữa 2 user
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :user1Id AND cm.receiver.id = :user2Id) OR " +
           "(cm.sender.id = :user2Id AND cm.receiver.id = :user1Id) " +
           "ORDER BY cm.createdAt ASC")
    List<ChatMessage> findMessagesBetweenUsers(@Param("user1Id") Integer user1Id, 
                                              @Param("user2Id") Integer user2Id);
    
    // Lấy tin nhắn với pagination
    @Query("SELECT cm FROM ChatMessage cm WHERE " +
           "(cm.sender.id = :user1Id AND cm.receiver.id = :user2Id) OR " +
           "(cm.sender.id = :user2Id AND cm.receiver.id = :user1Id) " +
           "ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findMessagesBetweenUsersWithPagination(@Param("user1Id") Integer user1Id, 
                                                             @Param("user2Id") Integer user2Id, 
                                                             Pageable pageable);
    
    // Đếm tin nhắn chưa đọc
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.receiver.id = :receiverId AND cm.isRead = false")
    Long countUnreadMessages(@Param("receiverId") Integer receiverId);
    
    // Đếm tin nhắn chưa đọc từ sender cụ thể
    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.sender.id = :senderId AND cm.receiver.id = :receiverId AND cm.isRead = false")
    Long countUnreadMessagesFromSender(@Param("senderId") Integer senderId, 
                                      @Param("receiverId") Integer receiverId);
    
    // Lấy tin nhắn chưa đọc
    List<ChatMessage> findByReceiverIdAndIsReadFalseOrderByCreatedAtAsc(Integer receiverId);
} 