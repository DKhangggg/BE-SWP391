package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import com.example.gender_healthcare_service.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<ChatMessageResponseDTO>> sendMessage(
            Authentication authentication,
            @RequestBody ChatMessageRequestDTO request) {
        try {
            ChatMessageResponseDTO message = chatService.sendMessage(authentication, request);
            return ResponseEntity.ok(ApiResponse.success("Gửi tin nhắn thành công", message));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/messages/{otherUserId}")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDTO>>> getMessages(
            Authentication authentication,
            @PathVariable Integer otherUserId) {
        try {
            List<ChatMessageResponseDTO> messages = chatService.getMessagesBetweenUsers(authentication, otherUserId);
            return ResponseEntity.ok(ApiResponse.success("Lấy tin nhắn thành công", messages));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/messages/{otherUserId}/page")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDTO>>> getMessagesWithPagination(
            Authentication authentication,
            @PathVariable Integer otherUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<ChatMessageResponseDTO> messages = chatService.getMessagesWithPagination(authentication, otherUserId, page, size);
            return ResponseEntity.ok(ApiResponse.success("Lấy tin nhắn thành công", messages));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/read/{senderId}")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(
            Authentication authentication,
            @PathVariable Integer senderId) {
        try {
            chatService.markMessagesAsRead(authentication, senderId);
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu đã đọc thành công", "OK"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ChatConversationResponseDTO>>> getConversations(
            Authentication authentication) {
        try {
            List<ChatConversationResponseDTO> conversations = chatService.getConversations(authentication);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách chat thành công", conversations));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @PostMapping("/conversations/{consultantId}")
    public ResponseEntity<ApiResponse<ChatConversationResponseDTO>> getOrCreateConversation(
            Authentication authentication,
            @PathVariable Integer consultantId) {
        try {
            ChatConversationResponseDTO conversation = chatService.getOrCreateConversation(authentication, consultantId);
            return ResponseEntity.ok(ApiResponse.success("Tạo hoặc lấy conversation thành công", conversation));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Authentication authentication) {
        try {
            Long count = chatService.getUnreadMessageCount(authentication);
            return ResponseEntity.ok(ApiResponse.success("Lấy số tin nhắn chưa đọc thành công", count));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }

    @GetMapping("/unread-messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDTO>>> getUnreadMessages(
            Authentication authentication) {
        try {
            List<ChatMessageResponseDTO> messages = chatService.getUnreadMessages(authentication);
            return ResponseEntity.ok(ApiResponse.success("Lấy tin nhắn chưa đọc thành công", messages));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        }
    }
} 