package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ChatCreateConversationRequestDTO;
import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.Conversation;
import com.example.gender_healthcare_service.entity.Message;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.ConversationRepository;
import com.example.gender_healthcare_service.repository.MessageRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.ChatConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChatConversationServiceImpl implements ChatConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConsultantRepository consultantRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversationResponseDTO> getConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        List<Conversation> conversations;
        if ("ROLE_CUSTOMER".equals(currentUser.getRoleName())) {
            conversations = conversationRepository.findByCustomer(currentUser);
        } else if ("ROLE_CONSULTANT".equals(currentUser.getRoleName())) {
            conversations = conversationRepository.findByConsultant(currentUser);
        } else {
            throw new RuntimeException("Invalid user role");
        }

        return conversations.stream()
                .map(this::mapToConversationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getCustomers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null || !"ROLE_CONSULTANT".equals(currentUser.getRoleName())) {
            throw new RuntimeException("Access denied");
        }

        // Lấy danh sách customers đã có conversation với consultant này
        List<Conversation> conversations = conversationRepository.findByConsultant(currentUser);
        return conversations.stream()
                .map(conv -> mapToUserResponseDTO(conv.getCustomer()))
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConsultantDTO> getConsultants() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null || !"ROLE_CUSTOMER".equals(currentUser.getRoleName())) {
            throw new RuntimeException("Access denied");
        }

        // Lấy tất cả consultants
        List<User> consultants = userRepository.findUserByRoleName("ROLE_CONSULTANT");
        return consultants.stream()
                .map(this::mapToConsultantDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ChatConversationResponseDTO createConversation(ChatCreateConversationRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null || !"ROLE_CUSTOMER".equals(currentUser.getRoleName())) {
            throw new RuntimeException("Only customers can create conversations");
        }

        User consultant = userRepository.findById(request.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found"));

        if (!"ROLE_CONSULTANT".equals(consultant.getRoleName())) {
            throw new RuntimeException("Invalid consultant");
        }

        // Kiểm tra xem đã có conversation chưa
        var existingConversation = conversationRepository.findByCustomerAndConsultant(currentUser, consultant);
        if (existingConversation.isPresent()) {
            return mapToConversationResponseDTO(existingConversation.get());
        }

        // Tạo conversation mới
        Conversation conversation = new Conversation();
        conversation.setCustomer(currentUser);
        conversation.setConsultant(consultant);
        conversation.setStatus("ACTIVE");
        conversation.setCreatedAt(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());
        conversation.setIsDeleted(false);

        // Nếu có tin nhắn đầu tiên
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            conversation.setLastMessage(request.getInitialMessage());
            conversation.setLastMessageTime(LocalDateTime.now());
            conversation.setConsultantUnreadCount(1);
        }

        conversation = conversationRepository.save(conversation);

        // Tạo tin nhắn đầu tiên nếu có
        if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
            Message message = new Message();
            message.setConversation(conversation);
            message.setSender(currentUser);
            message.setContent(request.getInitialMessage());
            message.setMessageType("TEXT");
            message.setStatus("SENT");
            message.setCreatedAt(LocalDateTime.now());
            message.setIsDeleted(false);
            messageRepository.save(message);
        }

        return mapToConversationResponseDTO(conversation);
    }

    @Override
    public ChatMessageResponseDTO sendMessage(ChatMessageRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        Conversation conversation = conversationRepository.findById(request.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Kiểm tra quyền truy cập
        if (!conversation.getCustomer().getId().equals(currentUser.getId()) && 
            !conversation.getConsultant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Tạo tin nhắn mới
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(currentUser);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setStatus("SENT");
        message.setCreatedAt(LocalDateTime.now());
        message.setIsDeleted(false);

        message = messageRepository.save(message);

        // Cập nhật conversation
        conversation.setLastMessage(request.getContent());
        conversation.setLastMessageTime(LocalDateTime.now());
        conversation.setUpdatedAt(LocalDateTime.now());

        // Tăng unread count cho người nhận
        if (conversation.getCustomer().getId().equals(currentUser.getId())) {
            conversation.setConsultantUnreadCount(conversation.getConsultantUnreadCount() + 1);
        } else {
            conversation.setCustomerUnreadCount(conversation.getCustomerUnreadCount() + 1);
        }

        conversationRepository.save(conversation);

        return mapToMessageResponseDTO(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> getMessages(Integer conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Kiểm tra quyền truy cập
        if (!conversation.getCustomer().getId().equals(currentUser.getId()) && 
            !conversation.getConsultant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation);
        return messages.stream()
                .map(this::mapToMessageResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void markConversationAsRead(Integer conversationId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));

        // Kiểm tra quyền truy cập
        if (!conversation.getCustomer().getId().equals(currentUser.getId()) && 
            !conversation.getConsultant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Access denied");
        }

        // Reset unread count cho user hiện tại
        if (conversation.getCustomer().getId().equals(currentUser.getId())) {
            conversation.setCustomerUnreadCount(0);
        } else {
            conversation.setConsultantUnreadCount(0);
        }

        conversationRepository.save(conversation);
    }

    // Helper methods
    private ChatConversationResponseDTO mapToConversationResponseDTO(Conversation conversation) {
        ChatConversationResponseDTO dto = new ChatConversationResponseDTO();
        dto.setId(conversation.getId());
        
        // Customer info
        dto.setCustomerId(conversation.getCustomer().getId());
        dto.setCustomerName(conversation.getCustomer().getFullName());
        dto.setCustomerAvatar(conversation.getCustomer().getAvatarUrl());
        
        // Consultant info
        dto.setConsultantId(conversation.getConsultant().getId());
        dto.setConsultantName(conversation.getConsultant().getFullName());
        dto.setConsultantAvatar(conversation.getConsultant().getAvatarUrl());
        // Get consultant specialization from Consultant entity
        Consultant consultant = consultantRepository.findById(conversation.getConsultant().getId()).orElse(null);
        dto.setConsultantSpecialization(consultant != null ? consultant.getSpecialization() : null);
        
        // Message info
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        
        // Unread counts
        dto.setCustomerUnreadCount(conversation.getCustomerUnreadCount());
        dto.setConsultantUnreadCount(conversation.getConsultantUnreadCount());
        
        // Status
        dto.setStatus(conversation.getStatus());
        dto.setIsActive(conversation.isActive());
        
        // Timestamps
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        
        return dto;
    }

    private ChatMessageResponseDTO mapToMessageResponseDTO(Message message) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        
        // Sender info
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderAvatar(message.getSender().getAvatarUrl());
        dto.setSenderRole(message.getSender().getRoleName().replace("ROLE_", ""));
        
        // Message content
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setStatus(message.getStatus());
        
        // Timestamps
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        dto.setIsEdited(message.getIsEdited());
        dto.setEditedAt(message.getEditedAt());
        dto.setIsDeleted(message.getIsDeleted());
        
        return dto;
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRoleName(user.getRoleName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setGender(user.getGender());
        // User entity doesn't have status field, set default or remove
        // dto.setStatus(user.getStatus());
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }

    private ConsultantDTO mapToConsultantDTO(User user) {
        ConsultantDTO dto = new ConsultantDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setGender(user.getGender());
        dto.setAddress(user.getAddress());

        // Get consultant-specific info from Consultant entity
        Consultant consultant = consultantRepository.findById(user.getId()).orElse(null);
        if (consultant != null) {
            dto.setBiography(consultant.getBiography());
            dto.setQualifications(consultant.getQualifications());
            dto.setExperienceYears(consultant.getExperienceYears());
            dto.setSpecialization(consultant.getSpecialization());
            dto.setProfileImageUrl(consultant.getProfileImageUrl());
        }

        // Use user avatar if consultant profile image is not available
        if (dto.getProfileImageUrl() == null) {
            dto.setProfileImageUrl(user.getAvatarUrl());
        }

        return dto;
    }
}
