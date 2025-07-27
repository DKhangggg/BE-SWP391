package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.MessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.AvailableConsultantResponseDTO;
import com.example.gender_healthcare_service.dto.response.MessageResponseDTO;
import com.example.gender_healthcare_service.entity.Chat;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.Message;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ChatRepository;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.MessageRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ConsultantRepository consultantRepository;
    private final MessageRepository messageRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }

        List<Chat> chats;
        if (currentUser.getRoleName().equals("ROLE_CONSULTANT")) {
            chats = chatRepository.findByConsultant(currentUser);
        } else {
            chats = chatRepository.findByCustomer(currentUser);
        }

        return chats.stream()
                .filter(chat -> !chat.getIsDeleted())
                .map(this::mapToConversationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ConversationResponseDTO createConversation(Integer consultantId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findUserByUsername(username);
        User consultant = userRepository.findById(consultantId).orElse(null);
        
        if (customer == null || consultant == null) {
            throw new RuntimeException("User not found");
        }

        // Kiểm tra xem conversation đã tồn tại chưa
        Chat existingChat = chatRepository.findByCustomerAndConsultant(customer, consultant);

        if (existingChat != null) {
            return mapToConversationResponseDTO(existingChat);
        }

        // Tạo conversation mới
        Chat newChat = new Chat();
        newChat.setCustomer(customer);
        newChat.setConsultant(consultant);
        newChat.setStatus("ACTIVE");
        newChat.setCreatedAt(LocalDateTime.now());
        newChat.setIsDeleted(false);

        Chat savedChat = chatRepository.save(newChat);
        return mapToConversationResponseDTO(savedChat);
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationResponseDTO getConversationDetails(Integer conversationId) {
        Chat chat = chatRepository.findById(conversationId).orElse(null);
        if (chat == null || chat.getIsDeleted()) {
            throw new RuntimeException("Conversation not found");
        }

        // Kiểm tra quyền truy cập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (!chat.getCustomer().getId().equals(currentUser.getId()) && 
            (chat.getConsultant() == null || !chat.getConsultant().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied");
        }

        return mapToConversationResponseDTO(chat);
    }

    @Override
    @Transactional
    public void deleteConversation(Integer conversationId) {
        Chat chat = chatRepository.findById(conversationId).orElse(null);
        if (chat == null) {
            throw new RuntimeException("Conversation not found");
        }

        // Kiểm tra quyền truy cập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (!chat.getCustomer().getId().equals(currentUser.getId()) && 
            (chat.getConsultant() == null || !chat.getConsultant().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied");
        }

        chat.setIsDeleted(true);
        chatRepository.save(chat);
    }

    @Override
    @Transactional
    public void markConversationAsRead(Integer conversationId) {
        Chat chat = chatRepository.findById(conversationId).orElse(null);
        if (chat == null) {
            throw new RuntimeException("Conversation not found");
        }

        // Kiểm tra quyền truy cập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (!chat.getCustomer().getId().equals(currentUser.getId()) && 
            (chat.getConsultant() == null || !chat.getConsultant().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied");
        }

        // Cập nhật trạng thái đã đọc dựa trên role của user
        
        if (chat.getCustomer().getId().equals(currentUser.getId())) {
            chat.resetCustomerUnread();
        } else if (chat.getConsultant().getId().equals(currentUser.getId())) {
            chat.resetConsultantUnread();
        }
        
        chatRepository.save(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getConversationMessages(Integer conversationId) {
        Chat chat = chatRepository.findById(conversationId).orElse(null);
        if (chat == null || chat.getIsDeleted()) {
            throw new RuntimeException("Conversation not found");
        }

        // Kiểm tra quyền truy cập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (!chat.getCustomer().getId().equals(currentUser.getId()) && 
            (chat.getConsultant() == null || !chat.getConsultant().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied");
        }

        // Lấy tin nhắn từ bảng Messages
        List<Message> messages = messageRepository.findByConversationOrderByCreatedAtAsc(chat);
        
        return messages.stream()
                .map(this::mapToMessageResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(Integer conversationId, MessageRequestDTO messageRequest) {
        Chat chat = chatRepository.findById(conversationId).orElse(null);
        if (chat == null || chat.getIsDeleted()) {
            throw new RuntimeException("Conversation not found");
        }

        // Kiểm tra quyền truy cập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (!chat.getCustomer().getId().equals(currentUser.getId()) && 
            (chat.getConsultant() == null || !chat.getConsultant().getId().equals(currentUser.getId()))) {
            throw new RuntimeException("Access denied");
        }

        // Tạo tin nhắn mới
        Message newMessage = new Message();
        newMessage.setConversation(chat);
        newMessage.setSender(currentUser);
        newMessage.setContent(messageRequest.getContent());
        newMessage.setMessageType(messageRequest.getMessageType());
        newMessage.setAttachmentUrl(messageRequest.getAttachmentUrl());
        newMessage.setFileName(messageRequest.getFileName());
        newMessage.setFileSize(messageRequest.getFileSize());
        newMessage.setStatus("SENT");
        newMessage.setCreatedAt(LocalDateTime.now());
        newMessage.setIsDeleted(false);

        Message savedMessage = messageRepository.save(newMessage);

        // Cập nhật conversation với tin nhắn mới
        chat.setLastMessage(messageRequest.getContent());
        chat.setLastMessageTime(LocalDateTime.now());
        chat.setStatus("ACTIVE");
        
        // Tăng unread count cho người nhận
        if (chat.getCustomer().getId().equals(currentUser.getId())) {
            chat.incrementConsultantUnread();
        } else {
            chat.incrementCustomerUnread();
        }
        
        chatRepository.save(chat);

        return mapToMessageResponseDTO(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getConsultantUnreadCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null || !currentUser.getRoleName().equals("ROLE_CONSULTANT")) {
            throw new RuntimeException("Access denied");
        }

        List<Chat> chats = chatRepository.findByConsultant(currentUser);
        int totalUnread = 0;
        
        for (Chat chat : chats) {
            if (!chat.getIsDeleted()) {
                totalUnread += chat.getConsultantUnreadCount() != null ? chat.getConsultantUnreadCount() : 0;
            }
        }
        
        return totalUnread;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationResponseDTO> getAvailableCustomers() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null || !currentUser.getRoleName().equals("ROLE_CONSULTANT")) {
            throw new RuntimeException("Access denied");
        }

        List<Chat> chats = chatRepository.findByConsultant(currentUser);
        return chats.stream()
                .filter(chat -> !chat.getIsDeleted())
                .map(this::mapToConversationResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailableConsultantResponseDTO> getAvailableConsultants() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found");
        }
        List<Consultant> consultants = consultantRepository.findAll();
        return consultants.stream()
                .filter(consultant -> !consultant.getIsDeleted()) // Chỉ lấy consultant chưa bị xóa
                .map(consultant -> {
                    AvailableConsultantResponseDTO dto = new AvailableConsultantResponseDTO();
                    dto.setConsultantId(consultant.getId());
                    dto.setConsultantName(consultant.getUser() != null ? consultant.getUser().getFullName() : "Unknown");
                    dto.setConsultantAvatar(consultant.getUser() != null ? consultant.getProfileImageUrl() : null);
                    dto.setConsultantSpecialization(consultant.getSpecialization());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private ConversationResponseDTO mapToConversationResponseDTO(Chat chat) {
        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setId(chat.getId());
        dto.setCustomerId(chat.getCustomer().getId());
        dto.setCustomerName(chat.getCustomer().getFullName());
        dto.setCustomerAvatar(chat.getCustomer().getAvatarUrl());
        
        if (chat.getConsultant() != null) {
            dto.setConsultantId(chat.getConsultant().getId());
            dto.setConsultantName(chat.getConsultant().getFullName());
            dto.setConsultantAvatar(chat.getConsultant().getAvatarUrl());
            
            // Lấy thông tin consultant
            Consultant consultant = consultantRepository.findById(chat.getConsultant().getId()).orElse(null);
            if (consultant != null) {
                dto.setConsultantSpecialization(consultant.getSpecialization());
            }
        }
        
        // Lấy tin nhắn cuối cùng từ bảng Messages
        List<Message> lastMessages = messageRepository.findLastMessageByConversation(chat);
        if (!lastMessages.isEmpty()) {
            Message lastMessage = lastMessages.get(0);
            dto.setLastMessage(lastMessage.getContent());
            dto.setLastMessageTime(lastMessage.getCreatedAt());
        } else {
            // Fallback nếu chưa có tin nhắn
            dto.setLastMessage(chat.getLastMessage());
            dto.setLastMessageTime(chat.getLastMessageTime());
        }
        
        dto.setCustomerUnreadCount(chat.getCustomerUnreadCount());
        dto.setConsultantUnreadCount(chat.getConsultantUnreadCount());
        dto.setStatus(chat.getStatus());
        dto.setCreatedAt(chat.getCreatedAt());
        dto.setUpdatedAt(chat.getUpdatedAt());
        dto.setIsActive(chat.isActive());
        
        // Tính tổng số tin nhắn
        Long totalMessages = messageRepository.countByConversation(chat);
        dto.setTotalMessages(totalMessages != null ? totalMessages.intValue() : 0);
        
        return dto;
    }

    private MessageResponseDTO mapToMessageResponseDTO(Message message) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getFullName());
        dto.setSenderRole(message.getSender().getRoleName());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType());
        dto.setAttachmentUrl(message.getAttachmentUrl());
        dto.setFileName(message.getFileName());
        dto.setFileSize(message.getFileSize());
        dto.setStatus(message.getStatus());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setReadAt(message.getReadAt());
        dto.setIsEdited(message.getIsEdited());
        dto.setEditedAt(message.getEditedAt());
        dto.setIsDeleted(message.getIsDeleted());
        
        return dto;
    }
} 