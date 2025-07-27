package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.MessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.AvailableConsultantResponseDTO;
import com.example.gender_healthcare_service.dto.response.MessageResponseDTO;

import java.util.List;

public interface ChatService {
    
    // Conversation management
    List<ConversationResponseDTO> getConversations();
    ConversationResponseDTO createConversation(Integer consultantId);
    ConversationResponseDTO getConversationDetails(Integer conversationId);
    void deleteConversation(Integer conversationId);
    void markConversationAsRead(Integer conversationId);
    
    // Message management
    List<MessageResponseDTO> getConversationMessages(Integer conversationId);
    MessageResponseDTO sendMessage(Integer conversationId, MessageRequestDTO messageRequest);
    
    // Consultant specific
    Integer getConsultantUnreadCount();
    List<ConversationResponseDTO> getAvailableCustomers();
    
    // Customer specific
    List<AvailableConsultantResponseDTO> getAvailableConsultants();
} 