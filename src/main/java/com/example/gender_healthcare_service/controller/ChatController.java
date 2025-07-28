package com.example.gender_healthcare_service.controller;

// ✅ TẠMTHỜI DISABLE TẤT CẢ IMPORTS CHO CHAT
/*
import com.example.gender_healthcare_service.dto.request.ChatCreateConversationRequestDTO;
import com.example.gender_healthcare_service.dto.request.ChatMessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.ChatConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.ChatMessageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.service.ChatConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
*/

// ✅ TẠMTHỜI DISABLE CHAT CONTROLLER ĐỂ TRÁNH INFINITE LOOP
// @RestController
// @RequestMapping("/api/chat")
// @RequiredArgsConstructor
// @CrossOrigin(origins = "*")
public class ChatController {

    // private final ChatConversationService chatConversationService;

    // ✅ TẠMTHỜI DISABLE TẤT CẢ CHAT ENDPOINTS
    /*
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ChatConversationResponseDTO>>> getConversations() {
        try {
            List<ChatConversationResponseDTO> conversations = chatConversationService.getConversations();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách conversations thành công", conversations));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách conversations: " + e.getMessage()));
        }
    }

    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getCustomers() {
        try {
            List<UserResponseDTO> customers = chatConversationService.getCustomers();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách customers thành công", customers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách customers: " + e.getMessage()));
        }
    }

    @GetMapping("/consultants")
    public ResponseEntity<ApiResponse<List<ConsultantDTO>>> getConsultants() {
        try {
            List<ConsultantDTO> consultants = chatConversationService.getConsultants();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách consultants thành công", consultants));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách consultants: " + e.getMessage()));
        }
    }

    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ChatConversationResponseDTO>> createConversation(
            @Valid @RequestBody ChatCreateConversationRequestDTO request) {
        try {
            ChatConversationResponseDTO conversation = chatConversationService.createConversation(request);
            return ResponseEntity.ok(ApiResponse.success("Tạo conversation thành công", conversation));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi tạo conversation: " + e.getMessage()));
        }
    }

    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<ChatMessageResponseDTO>> sendMessage(
            @Valid @RequestBody ChatMessageRequestDTO request) {
        try {
            ChatMessageResponseDTO message = chatConversationService.sendMessage(request);
            return ResponseEntity.ok(ApiResponse.success("Gửi tin nhắn thành công", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi gửi tin nhắn: " + e.getMessage()));
        }
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageResponseDTO>>> getMessages(
            @PathVariable Integer conversationId) {
        try {
            List<ChatMessageResponseDTO> messages = chatConversationService.getMessages(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Lấy tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy tin nhắn: " + e.getMessage()));
        }
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<ApiResponse<String>> markConversationAsRead(
            @PathVariable Integer conversationId) {
        try {
            chatConversationService.markConversationAsRead(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu đã đọc thành công", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi đánh dấu đã đọc: " + e.getMessage()));
        }
    }
    */
}
