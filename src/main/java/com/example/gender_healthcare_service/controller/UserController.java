package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.MenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.UserProfileRequest;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.service.UserService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import com.example.gender_healthcare_service.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
