package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.MessageRequestDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.ConversationResponseDTO;
import com.example.gender_healthcare_service.dto.response.AvailableConsultantResponseDTO;
import com.example.gender_healthcare_service.dto.response.MessageResponseDTO;
import com.example.gender_healthcare_service.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // Debug API để kiểm tra role của user hiện tại
    @GetMapping("/debug/user-role")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_CONSULTANT', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> debugUserRole() {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
            
            Map<String, Object> debugInfo = new HashMap<>();
            debugInfo.put("username", username);
            debugInfo.put("authorities", authorities.stream().map(Object::toString).collect(Collectors.toList()));
            debugInfo.put("principal", SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
            
            return ResponseEntity.ok(ApiResponse.success("Debug info retrieved", debugInfo));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy debug info: " + e.getMessage()));
        }
    }

    // Lấy danh sách conversations của user hiện tại
    @GetMapping("/conversations")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConversationResponseDTO>>> getConversations() {
        try {
            List<ConversationResponseDTO> conversations = chatService.getConversations();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách cuộc trò chuyện thành công", conversations));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách cuộc trò chuyện: " + e.getMessage()));
        }
    }

    // Tạo conversation mới (Customer và Admin có thể tạo)
    @PostMapping("/conversations")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ConversationResponseDTO>> createConversation(@RequestParam Integer consultantId) {
        try {
            ConversationResponseDTO conversation = chatService.createConversation(consultantId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo cuộc trò chuyện thành công", conversation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo cuộc trò chuyện: " + e.getMessage()));
        }
    }

    // Lấy chi tiết conversation
    @GetMapping("/conversations/{conversationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ConversationResponseDTO>> getConversationDetails(@PathVariable Integer conversationId) {
        try {
            ConversationResponseDTO conversation = chatService.getConversationDetails(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết cuộc trò chuyện thành công", conversation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết cuộc trò chuyện: " + e.getMessage()));
        }
    }

    // Xóa conversation (soft delete)
    @DeleteMapping("/conversations/{conversationId}")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteConversation(@PathVariable Integer conversationId) {
        try {
            chatService.deleteConversation(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Xóa cuộc trò chuyện thành công", "Cuộc trò chuyện đã được xóa"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa cuộc trò chuyện: " + e.getMessage()));
        }
    }

    // Đánh dấu conversation đã đọc
    @PutMapping("/conversations/{conversationId}/read")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> markConversationAsRead(@PathVariable Integer conversationId) {
        try {
            chatService.markConversationAsRead(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu đã đọc thành công", "Cuộc trò chuyện đã được đánh dấu đã đọc"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đánh dấu đã đọc: " + e.getMessage()));
        }
    }

    // Lấy danh sách tin nhắn trong conversation
    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<MessageResponseDTO>>> getConversationMessages(@PathVariable Integer conversationId) {
        try {
            List<MessageResponseDTO> messages = chatService.getConversationMessages(conversationId);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách tin nhắn: " + e.getMessage()));
        }
    }

    // Gửi tin nhắn trong conversation
    @PostMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<MessageResponseDTO>> sendMessage(
            @PathVariable Integer conversationId,
            @RequestBody MessageRequestDTO messageRequest) {
        try {
            MessageResponseDTO message = chatService.sendMessage(conversationId, messageRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Gửi tin nhắn thành công", message));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi gửi tin nhắn: " + e.getMessage()));
        }
    }

    // Lấy danh sách customer có sẵn cho consultant
    @GetMapping("/consultant/available-customers")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<List<ConversationResponseDTO>>> getAvailableCustomers() {
        try {
            List<ConversationResponseDTO> customers = chatService.getAvailableCustomers();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng có sẵn thành công", customers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách khách hàng: " + e.getMessage()));
        }
    }

    // Lấy danh sách consultant có sẵn cho customer
    @GetMapping("/customer/available-consultants")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_CONSULTANT', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<List<AvailableConsultantResponseDTO>>> getAvailableConsultants() {
        try {
            List<AvailableConsultantResponseDTO> consultants = chatService.getAvailableConsultants();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tư vấn viên có sẵn thành công", consultants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách tư vấn viên: " + e.getMessage()));
        }
    }
    
} 