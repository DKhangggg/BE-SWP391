package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ChatService {
    
    // Gửi tin nhắn
    ChatMessageResponseDTO sendMessage(Authentication authentication, ChatMessageRequestDTO request);
    
    // Lấy tin nhắn giữa 2 user
    List<ChatMessageResponseDTO> getMessagesBetweenUsers(Authentication authentication, Integer otherUserId);
    
    // Lấy tin nhắn với pagination
    List<ChatMessageResponseDTO> getMessagesWithPagination(Authentication authentication, Integer otherUserId, int page, int size);
    
    // Đánh dấu tin nhắn đã đọc
    void markMessagesAsRead(Authentication authentication, Integer senderId);
    
    // Lấy tất cả conversation
    List<ChatConversationResponseDTO> getConversations(Authentication authentication);
    
    // Tạo hoặc lấy conversation
    ChatConversationResponseDTO getOrCreateConversation(Authentication authentication, Integer consultantId);
    
    // Đếm tin nhắn chưa đọc
    Long getUnreadMessageCount(Authentication authentication);
    
    // Lấy tin nhắn chưa đọc
    List<ChatMessageResponseDTO> getUnreadMessages(Authentication authentication);
} 