package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserProfileTrendResponseDTO;
import com.example.gender_healthcare_service.service.AvatarService;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import com.example.gender_healthcare_service.service.ReminderService;
import com.example.gender_healthcare_service.service.UserService;
import com.example.gender_healthcare_service.service.INotificationService;
import com.example.gender_healthcare_service.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private MenstrualCycleService menstrualCycleService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private INotificationService notificationService;

    private final AvatarService avatarService;

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> updateProfile(@RequestBody(required = false) UserProfileRequest userProfileUpdate) {
        try {
            UserResponseDTO updatedUser = userService.updateUser(userProfileUpdate);
            if (updatedUser != null) {
                return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin cá nhân thành công", updatedUser));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Không thể cập nhật thông tin cá nhân"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật thông tin cá nhân: " + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo() {
        try {
            UserResponseDTO userInfo = userService.getInfo();
            if (userInfo != null) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", userInfo));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy người dùng"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin người dùng: " + e.getMessage()));
        }
    }
    
    @GetMapping("/profile/trends")
    public ResponseEntity<ApiResponse<UserProfileTrendResponseDTO>> getUserProfileWithTrends() {
        try {
            UserProfileTrendResponseDTO profileWithTrends = userService.getUserProfileWithTrends();
            if (profileWithTrends != null) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin hồ sơ với trends thành công", profileWithTrends));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy thông tin hồ sơ"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin hồ sơ với trends: " + e.getMessage()));
        }
    }

    @PostMapping("/create-sample-notifications")
    public ResponseEntity<ApiResponse<String>> createSampleNotifications() {
        try {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("Bạn chưa đăng nhập"));
            }
            return ResponseEntity.ok(ApiResponse.success("Tạo notification mẫu thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo notification mẫu: " + e.getMessage()));
        }
    }

    @PostMapping("/avatar/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            UserResponseDTO updatedUser = avatarService.uploadAvatar(file);
            
            Map<String, Object> responseData = Map.of(
                "user", updatedUser,
                "avatarUrl", updatedUser.getAvatarUrl(),
                "publicId", updatedUser.getAvatarPublicId()
            );
            
            return ResponseEntity.ok(ApiResponse.success("Upload avatar thành công!", responseData));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Upload avatar error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi upload avatar: " + e.getMessage()));
        }
    }

    @DeleteMapping("/avatar/delete")
    public ResponseEntity<ApiResponse<UserResponseDTO>> deleteAvatar() {
        try {
            UserResponseDTO updatedUser = avatarService.deleteAvatar();
            return ResponseEntity.ok(ApiResponse.success("Xóa avatar thành công!", updatedUser));
        } catch (Exception e) {
            log.error("Delete avatar error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xóa avatar: " + e.getMessage()));
        }
    }

    @GetMapping("/avatar/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAvatar() {
        try {
            boolean hasAvatar = avatarService.hasAvatar();
            String currentUrl = avatarService.getCurrentAvatarUrl();
            
            Map<String, Object> responseData = Map.of(
                "hasAvatar", hasAvatar,
                "avatarUrl", currentUrl
            );
            
            return ResponseEntity.ok(ApiResponse.success("Kiểm tra avatar thành công", responseData));
            
        } catch (Exception e) {
            log.error("Check avatar error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi kiểm tra avatar: " + e.getMessage()));
        }
    }

    @GetMapping("/booking-history")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getBookingHistory() {
        try {
            List<BookingResponseDTO> bookingHistory = bookingService.getUserBookings();
            return ResponseEntity.ok(ApiResponse.success("Lấy lịch sử booking thành công", bookingHistory));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy lịch sử booking: " + e.getMessage()));
        }
    }

    @GetMapping("/reminders")
    public ResponseEntity<ApiResponse<?>> getUserReminders() {
        try {
            Object reminders = reminderService.getUserReminders();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách nhắc nhở thành công", reminders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách nhắc nhở: " + e.getMessage()));
        }
    }

    @GetMapping("/menstrual-cycle")
    public ResponseEntity<ApiResponse<?>> getUserMenstrualCycle() {
        try {
            Object cycle = menstrualCycleService.getCurrentMenstrualCycle(null);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin chu kỳ thành công", cycle));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin chu kỳ: " + e.getMessage()));
        }
    }

    @GetMapping("/customers")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<List<UserResponseDTO>>> getCustomers() {
        try {
            List<UserResponseDTO> customers = userService.getCustomers();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách khách hàng thành công", customers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách khách hàng: " + e.getMessage()));
        }
    }
}
