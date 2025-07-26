package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.service.AvatarService;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import com.example.gender_healthcare_service.service.ReminderService;
import com.example.gender_healthcare_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    private final AvatarService avatarService;

    @PatchMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody(required = false) UserProfileRequest userProfileUpdate) {
        UserResponseDTO updatedUser = userService.updateUser(userProfileUpdate);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        } else {
            return ResponseEntity.status(400).body("Failed to update user profile");
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserInfo() {
        UserResponseDTO userInfo = userService.getInfo();
        if (userInfo != null) {
            return ResponseEntity.ok(userInfo);
        } else {
            return ResponseEntity.status(404).body("User not found");
        }
    }

    @PostMapping("/avatar/upload")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file") MultipartFile file) {
        try {
            UserResponseDTO updatedUser = avatarService.uploadAvatar(file);
            
            return ResponseEntity.ok(Map.of(
                "message", "Upload avatar thành công!",
                "user", updatedUser,
                "avatarUrl", updatedUser.getAvatarUrl(),
                "publicId", updatedUser.getAvatarPublicId()
            ));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Upload avatar error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi upload avatar: " + e.getMessage());
        }
    }

    @DeleteMapping("/avatar/delete")
    public ResponseEntity<?> deleteAvatar() {
        try {
            UserResponseDTO updatedUser = avatarService.deleteAvatar();
            
            return ResponseEntity.ok(Map.of(
                "message", "Xóa avatar thành công!",
                "user", updatedUser
            ));
            
        } catch (Exception e) {
            log.error("Delete avatar error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi xóa avatar: " + e.getMessage());
        }
    }

    @GetMapping("/avatar/check")
    public ResponseEntity<?> checkAvatar() {
        try {
            boolean hasAvatar = avatarService.hasAvatar();
            String currentUrl = avatarService.getCurrentAvatarUrl();
            
            return ResponseEntity.ok(Map.of(
                "hasAvatar", hasAvatar,
                "avatarUrl", currentUrl
            ));
            
        } catch (Exception e) {
            log.error("Check avatar error: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Lỗi khi kiểm tra avatar: " + e.getMessage());
        }
    }

    @GetMapping("/booking-history")
    public ResponseEntity<?> getBookingHistory() {
        List<BookingResponseDTO> bookingHistory = bookingService.getUserBookings();
        return ResponseEntity.ok(bookingHistory);
    }

    @GetMapping("/reminders")
    public ResponseEntity<?> getUserReminders() {
        try {
            return ResponseEntity.ok(reminderService.getUserReminders());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy danh sách nhắc nhở: " + e.getMessage());
        }
    }

    @GetMapping("/menstrual-cycle")
    public ResponseEntity<?> getUserMenstrualCycle() {
        try {
            return ResponseEntity.ok(menstrualCycleService.getCurrentMenstrualCycle(null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi lấy thông tin chu kỳ: " + e.getMessage());
        }
    }
}
