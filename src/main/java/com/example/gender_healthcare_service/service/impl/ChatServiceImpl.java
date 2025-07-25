package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import com.example.gender_healthcare_service.entity.ChatMessage;
import com.example.gender_healthcare_service.entity.ChatConversation;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.repository.ChatMessageRepository;
import com.example.gender_healthcare_service.repository.ChatConversationRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.service.ChatService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatConversationRepository chatConversationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatMessageResponseDTO sendMessage(Authentication authentication, ChatMessageRequestDTO request) {
        String username = authentication.getName();
        User sender = userRepository.findUserByUsername(username);
        if (sender == null) {
            throw new RuntimeException("Không tìm thấy người gửi");
        }

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người nhận"));

        // Tạo tin nhắn
        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setMessageType(request.getMessageType());
        message.setIsRead(false);

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Cập nhật hoặc tạo conversation
        updateOrCreateConversation(sender, receiver, request.getContent());

        // Gửi tin nhắn qua WebSocket
        ChatMessageResponseDTO messageDTO = convertToMessageDTO(savedMessage);
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),
                "/queue/chat",
                messageDTO
        );

        return messageDTO;
    }

    @Override
    public List<ChatMessageResponseDTO> getMessagesBetweenUsers(Authentication authentication, Integer otherUserId) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        List<ChatMessage> messages = chatMessageRepository.findMessagesBetweenUsers(currentUser.getId(), otherUserId);
        return messages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatMessageResponseDTO> getMessagesWithPagination(Authentication authentication, Integer otherUserId, int page, int size) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messagePage = chatMessageRepository.findMessagesBetweenUsersWithPagination(
                currentUser.getId(), otherUserId, pageable);

        return messagePage.getContent().stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(Authentication authentication, Integer senderId) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtAsc(currentUser.getId());
        
        for (ChatMessage message : unreadMessages) {
            if (message.getSender().getId().equals(senderId)) {
                message.setIsRead(true);
                message.setReadAt(LocalDateTime.now());
                chatMessageRepository.save(message);
            }
        }

        // Cập nhật unread count trong conversation
        updateConversationUnreadCount(currentUser.getId(), senderId);
    }

    @Override
    public List<ChatConversationResponseDTO> getConversations(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        List<ChatConversation> conversations;
        if (currentUser.getRoleName().equals("ROLE_CONSULTANT")) {
            Consultant consultant = consultantRepository.findByUserId(currentUser.getId());
            if (consultant == null) {
                throw new RuntimeException("Không tìm thấy consultant");
            }
            conversations = chatConversationRepository.findByConsultantIdOrderByUpdatedAtDesc(consultant.getId());
        } else {
            conversations = chatConversationRepository.findByUserIdOrderByUpdatedAtDesc(currentUser.getId());
        }

        return conversations.stream()
                .map(this::convertToConversationDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatConversationResponseDTO getOrCreateConversation(Authentication authentication, Integer consultantId) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy consultant"));

        Optional<ChatConversation> existingConversation = chatConversationRepository
                .findByUserIdAndConsultantId(currentUser.getId(), consultantId);

        if (existingConversation.isPresent()) {
            return convertToConversationDTO(existingConversation.get());
        }

        // Tạo conversation mới
        ChatConversation newConversation = new ChatConversation();
        newConversation.setUser(currentUser);
        newConversation.setConsultant(consultant);
        newConversation.setIsActive(true);
        newConversation.setUnreadCount(0);

        ChatConversation savedConversation = chatConversationRepository.save(newConversation);
        return convertToConversationDTO(savedConversation);
    }

    @Override
    public Long getUnreadMessageCount(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return chatMessageRepository.countUnreadMessages(currentUser.getId());
    }

    @Override
    public List<ChatMessageResponseDTO> getUnreadMessages(Authentication authentication) {
        String username = authentication.getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        List<ChatMessage> unreadMessages = chatMessageRepository.findByReceiverIdAndIsReadFalseOrderByCreatedAtAsc(currentUser.getId());
        return unreadMessages.stream()
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    private void updateOrCreateConversation(User sender, User receiver, String lastMessage) {
        Optional<ChatConversation> conversationOpt = chatConversationRepository
                .findByUserIdAndConsultantId(sender.getId(), receiver.getId());

        ChatConversation conversation;
        if (conversationOpt.isPresent()) {
            conversation = conversationOpt.get();
        } else {
            // Tạo conversation mới
            Consultant consultant = consultantRepository.findByUserId(receiver.getId());
            if (consultant == null) {
                throw new RuntimeException("Người nhận không phải là consultant");
            }

            conversation = new ChatConversation();
            conversation.setUser(sender);
            conversation.setConsultant(consultant);
            conversation.setIsActive(true);
        }

        conversation.setLastMessage(lastMessage);
        conversation.setLastMessageTime(LocalDateTime.now());
        
        // Tăng unread count nếu người gửi không phải là chủ conversation
        if (!sender.getId().equals(conversation.getUser().getId())) {
            conversation.setUnreadCount(conversation.getUnreadCount() + 1);
        }

        chatConversationRepository.save(conversation);
    }

    private void updateConversationUnreadCount(Integer userId, Integer senderId) {
        Optional<ChatConversation> conversationOpt = chatConversationRepository
                .findByUserIdAndConsultantId(userId, senderId);

        if (conversationOpt.isPresent()) {
            ChatConversation conversation = conversationOpt.get();
            Long unreadCount = chatMessageRepository.countUnreadMessagesFromSender(senderId, userId);
            conversation.setUnreadCount(unreadCount.intValue());
            chatConversationRepository.save(conversation);
        }
    }

    private ChatMessageResponseDTO convertToMessageDTO(ChatMessage message) {
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderRole(message.getSender().getRoleName());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverName(message.getReceiver().getFullName());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        return dto;
    }

    private ChatConversationResponseDTO convertToConversationDTO(ChatConversation conversation) {
        ChatConversationResponseDTO dto = new ChatConversationResponseDTO();
        dto.setId(conversation.getId());
        dto.setUserId(conversation.getUser().getId());
        dto.setUserName(conversation.getUser().getFullName());
        dto.setUserAvatar(null); // User entity không có avatar field
        dto.setConsultantId(conversation.getConsultant().getId());
        dto.setConsultantName(conversation.getConsultant().getUser().getFullName());
        dto.setConsultantAvatar(null); // User entity không có avatar field
        dto.setLastMessage(conversation.getLastMessage());
        dto.setLastMessageTime(conversation.getLastMessageTime());
        dto.setUnreadCount(conversation.getUnreadCount());
        dto.setIsActive(conversation.getIsActive());
        dto.setCreatedAt(conversation.getCreatedAt());
        dto.setUpdatedAt(conversation.getUpdatedAt());
        return dto;
    }
} 