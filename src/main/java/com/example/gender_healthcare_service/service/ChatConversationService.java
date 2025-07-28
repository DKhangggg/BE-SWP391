package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.ChatCreateConversationRequestDTO;
import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;

import java.util.List;

public interface ChatConversationService {
    
    // Lấy danh sách conversations của user hiện tại
    List<ChatConversationResponseDTO> getConversations();
    
    // Lấy danh sách customers (cho consultant)
    List<UserResponseDTO> getCustomers();
    
    // Lấy danh sách consultants (cho customer)
    List<ConsultantDTO> getConsultants();
    
    // Tạo conversation mới
    ChatConversationResponseDTO createConversation(ChatCreateConversationRequestDTO request);
    
    // Gửi tin nhắn
    ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request);
    
    // Lấy tin nhắn trong conversation
    List<ChatMessageResponseDTO> getMessages(Integer conversationId);
    
    // Đánh dấu conversation đã đọc
    void markConversationAsRead(Integer conversationId);
}
