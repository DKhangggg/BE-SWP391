package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.NotificationResponseDTO;
import com.example.gender_healthcare_service.service.INotificationService;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final INotificationService notificationService;
    private final UserRepository userRepository;

    /**
     * Lấy tất cả notifications của user hiện tại
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getUserNotifications(Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thông báo thành công", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách thông báo: " + e.getMessage()));
        }
    }

    /**
     * Lấy notifications theo loại
     */
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getUserNotificationsByType(
            @PathVariable String type,
            Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            List<NotificationResponseDTO> notifications = notificationService.getUserNotificationsByType(userId, type);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách thông báo theo loại thành công", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy danh sách thông báo: " + e.getMessage()));
        }
    }

    /**
     * Lấy số lượng notifications chưa đọc
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount(Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            long unreadCount = notificationService.getUnreadNotificationCount(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy số lượng thông báo chưa đọc thành công", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi lấy số lượng thông báo chưa đọc: " + e.getMessage()));
        }
    }

    /**
     * Đánh dấu một notification là đã đọc
     */
    @PatchMapping("/{notificationId}/read")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu thông báo đã đọc thành công", "Notification marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi đánh dấu thông báo: " + e.getMessage()));
        }
    }

    /**
     * Đánh dấu tất cả notifications của user là đã đọc
     */
    @PatchMapping("/mark-all-read")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> markAllNotificationsAsRead(Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            notificationService.markAllNotificationsAsRead(userId);
            
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu tất cả thông báo đã đọc thành công", "All notifications marked as read"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi đánh dấu tất cả thông báo: " + e.getMessage()));
        }
    }

    /**
     * Tạo notifications mẫu cho testing
     */
    @PostMapping("/create-sample")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'CONSULTANT', 'STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> createSampleNotifications(Authentication authentication) {
        try {
            Integer userId = Integer.parseInt(authentication.getName());
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Tạo notifications mẫu như trong hình
            notificationService.createNotification(user, 
                "Lịch tư vấn sắp tới", 
                "Bạn có lịch tư vấn với Dr. Nguyễn Hoa vào 15:00 ngày mai", 
                "/consultation/upcoming", 
                "CONSULTATION", 
                "Lịch tư vấn với bác sĩ chuyên khoa");

            notificationService.createNotification(user, 
                "Kết quả xét nghiệm", 
                "Kết quả xét nghiệm STI của bạn đã có. Nhấn để xem chi tiết.", 
                "/test-results", 
                "TEST_RESULT", 
                "Kết quả xét nghiệm STI");

            notificationService.createNotification(user, 
                "Nhắc nhở chu kỳ", 
                "Hôm nay là ngày dự đoán rụng trứng. Hãy theo dõi cẩn thận!", 
                "/cycle-tracking", 
                "CYCLE_REMINDER", 
                "Nhắc nhở chu kỳ kinh nguyệt");

            notificationService.createNotification(user, 
                "Trả lời câu hỏi", 
                "Dr. Lê Minh đã trả lời câu hỏi của bạn về chu kỳ kinh nguyệt", 
                "/qa/questions", 
                "QUESTION_ANSWERED", 
                "Câu hỏi đã được trả lời");

            return ResponseEntity.ok(ApiResponse.success("Tạo notifications mẫu thành công", "4 sample notifications created"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Lỗi khi tạo notifications mẫu: " + e.getMessage()));
        }
    }
} 