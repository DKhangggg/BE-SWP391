package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.DashboardStatsResponseDTO;
import com.example.gender_healthcare_service.dto.response.NotificationResponseDTO;
import com.example.gender_healthcare_service.dto.response.UpcomingAppointmentResponseDTO;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/homepage")
@RequiredArgsConstructor
public class HomepageController {

    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private  DashboardService dashboardService;
    @Autowired
    private  INotificationService notificationService;
    @Autowired
    private  BlogService blogService;
    @Autowired
    private UserService userService;



    @GetMapping("/featured-doctors")
    public ResponseEntity<ApiResponse<List<ConsultantDTO>>> getFeaturedDoctors() {
        try {
            List<ConsultantDTO> featuredDoctors = consultantService.getFeaturedConsultants();
            if (featuredDoctors == null || featuredDoctors.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Không có bác sĩ nổi bật nào", List.of()));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách bác sĩ nổi bật thành công", featuredDoctors));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách bác sĩ nổi bật: " + e.getMessage()));
        }
    }

    @GetMapping("/latest-blog-posts")
    public ResponseEntity<ApiResponse<List<BlogPostResponseDTO>>> getLatestBlogPosts(
            @RequestParam(defaultValue = "3") int limit) {
        try {
            List<BlogPostResponseDTO> latestPosts = blogService.getLatestBlogPosts(limit);
            if (latestPosts == null || latestPosts.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Chưa có bài viết nào", List.of()));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy bài viết mới nhất thành công", latestPosts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy bài viết mới nhất: " + e.getMessage()));
        }
    }

    @GetMapping("/details")
    public ResponseEntity<?> getHomepageDetails() {
        return null;
    }

    @GetMapping("/blog/posts")
    public ResponseEntity<?> getBlogPosts() {
        return null;
    }

    @GetMapping("/blog/posts/{postId}")
    public ResponseEntity<?> getBlogPostById(@PathVariable String postId) {
        return null;
    }

    @GetMapping("/consultants")
    public ResponseEntity<ApiResponse<List<ConsultantDTO>>> getPublicConsultants() {
        try {
            List<ConsultantDTO> consultants = consultantService.getAllConsultants();
            if (consultants == null || consultants.isEmpty()) {
                return ResponseEntity.ok(ApiResponse.success("Không có tư vấn viên nào", List.of()));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tư vấn viên thành công", consultants));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách tư vấn viên: " + e.getMessage()));
        }
    }

    // ============= DASHBOARD ENDPOINTS =============
    
    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<DashboardStatsResponseDTO>> getDashboardStats() {
        try {
            User user= userService.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Bạn chưa đăng nhập hoặc không có quyền truy cập"));
            }
            DashboardStatsResponseDTO stats = dashboardService.getDashboardStats(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Lấy thống kê dashboard thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thống kê dashboard: " + e.getMessage()));
        }
    }

    @GetMapping("/upcoming-appointments")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<UpcomingAppointmentResponseDTO>>> getUpcomingAppointments() {
        try {
            User user= userService.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Bạn chưa đăng nhập hoặc không có quyền truy cập"));
            }
            List<UpcomingAppointmentResponseDTO> appointments = dashboardService.getUpcomingAppointments(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Lấy lịch hẹn sắp tới thành công", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy lịch hẹn sắp tới: " + e.getMessage()));
        }
    }

    @GetMapping("/notifications")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<NotificationResponseDTO>>> getNotifications(
            @RequestParam(required = false) String type) {
        try {
            User user= userService.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Bạn chưa đăng nhập hoặc không có quyền truy cập"));
            }
            List<NotificationResponseDTO> notifications;
            if (type != null && !type.isEmpty()) {
                notifications = notificationService.getUserNotificationsByType(user.getId(), type);
            } else {
                notifications = notificationService.getUserNotifications(user.getId());
            }
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thông báo thành công", notifications));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông báo: " + e.getMessage()));
        }
    }

    @PostMapping("/notifications/{notificationId}/mark-read")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markNotificationAsRead(notificationId);
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu thông báo đã đọc thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đánh dấu thông báo đã đọc: " + e.getMessage()));
        }
    }

    @PostMapping("/notifications/mark-all-read")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<String>> markAllNotificationsAsRead() {
        try {
            User user= userService.getCurrentUser();
            if(user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Bạn chưa đăng nhập hoặc không có quyền truy cập"));
            }
            notificationService.markAllNotificationsAsRead(user.getId());
            return ResponseEntity.ok(ApiResponse.success("Đánh dấu tất cả thông báo đã đọc thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đánh dấu tất cả thông báo đã đọc: " + e.getMessage()));
        }
    }
}


