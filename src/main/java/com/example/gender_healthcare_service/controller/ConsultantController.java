package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ConsultantUpdateDTO;
import com.example.gender_healthcare_service.dto.request.ReminderRequestDTO;
import com.example.gender_healthcare_service.dto.request.UnavailabilityRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationHistoryDTO;
import com.example.gender_healthcare_service.dto.response.ReminderResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.service.ConsultantService;
import com.example.gender_healthcare_service.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.gender_healthcare_service.dto.response.ConsultationBookingResponseDTO;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import com.example.gender_healthcare_service.service.ConsultationService;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/consultant")
@PreAuthorize("hasAuthority('ROLE_CONSULTANT') or hasAuthority('ROLE_ADMIN')")
public class ConsultantController {
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private ReminderService reminderService;

    @GetMapping("/getProfile")
    public ResponseEntity<?> getProfile(){
        ConsultantDTO consultantDTO = consultantService.getCurrentConsultant();
        if(consultantDTO == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(consultantDTO);
    }

    @PutMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(@RequestBody ConsultantUpdateDTO ConsultantUpdate) {
        try {
            consultantService.updateConsultant(ConsultantUpdate);
            return ResponseEntity.ok().body("Profile updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to update profile: " + e.getMessage());
        }
    }
    //unavailability
    @PostMapping("/unavailability")
    public ResponseEntity<?> addUnavailability(@RequestBody UnavailabilityRequest addUnavailabilityRequestDTO) {
            boolean stage=consultantService.addUnavailability(addUnavailabilityRequestDTO);
            if(stage){
            return ResponseEntity.ok().body("Unavailability added successfully");}
            else{
                return ResponseEntity.status(400).body("Failed to add unavailability");
            }
    }

    @GetMapping("/unavailability")
    public ResponseEntity<?> getUnavailability(@RequestBody String date) {
        try {
            List<ConsultantUnavailability> unavailabilities = consultantService.getUnavailabilityByDate(date);
            return ResponseEntity.ok().body(unavailabilities);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get unavailabilities: " + e.getMessage());
        }
    }
    //consultation bookings
    @GetMapping("/consultation-bookings")
    public ResponseEntity<?> getConsultationBookings() {
        try {
            List<ConsultationBookingResponseDTO> bookings = consultationService.getConsultationBookingsForCurrentConsultant();
            return ResponseEntity.ok().body(bookings);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get consultation bookings: " + e.getMessage());
        }
    }

    @GetMapping("/consultation-history")
    public ResponseEntity<?> getConsultationHistory() {
        try {
            List<ConsultationHistoryDTO> history = consultationService.getConsultationHistoryForCurrentConsultant();
            return ResponseEntity.ok().body(history);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get consultation history: " + e.getMessage());
        }
    }

    @GetMapping("/consultation-history/{id}/patient")
    public ResponseEntity<?> getPatientInfoForConsultation(@PathVariable Integer id) {
        try {
            UserResponseDTO patient = consultationService.getPatientInfoForConsultation(id);
            return ResponseEntity.ok().body(patient);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get patient info: " + e.getMessage());
        }
    }

    //reminder

    @GetMapping("/patient/{userId}/reminders")
    public ResponseEntity<?> getUserReminders(@PathVariable Integer userId) {
        try {
            List<ReminderResponseDTO> reminders = reminderService.getRemindersByUserId(userId);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get reminders: " + e.getMessage());
        }
    }

    @PostMapping("/patient/reminder")
    public ResponseEntity<?> createReminder(@RequestBody ReminderRequestDTO reminderRequest) {
        try {
            ReminderResponseDTO createdReminder = reminderService.createReminder(reminderRequest);
            return ResponseEntity.ok(createdReminder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to create reminder: " + e.getMessage());
        }
    }

    @GetMapping("/patient/reminder/{id}")
    public ResponseEntity<?> getReminderById(@PathVariable Integer id) {
        try {
            ReminderResponseDTO reminder = reminderService.getReminderById(id);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get reminder: " + e.getMessage());
        }
    }

    @PutMapping("/patient/reminder/{id}")
    public ResponseEntity<?> updateReminder(@PathVariable Integer id, @RequestBody ReminderRequestDTO reminderRequest) {
        try {
            ReminderResponseDTO updatedReminder = reminderService.updateReminder(id, reminderRequest);
            return ResponseEntity.ok(updatedReminder);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to update reminder: " + e.getMessage());
        }
    }

    @DeleteMapping("/patient/reminder/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer id) {
        try {
            reminderService.deleteReminder(id);
            return ResponseEntity.ok().body("Reminder deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to delete reminder: " + e.getMessage());
        }
    }

    @GetMapping("/messages/unread-count")
    public ResponseEntity<?> getUnreadMessagesCount() {
        try {
            long count = consultantService.getUnreadMessagesCount();
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get unread messages count: " + e.getMessage());
        }
    }


    @GetMapping("/revenue")
    public ResponseEntity<?> getRevenue(
            @RequestParam(value = "date", required = false) String date,
            @RequestParam(value = "month", required = false) String month) {
        try {
            Map<String, Object> result = consultantService.getRevenue(date, month);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get revenue: " + e.getMessage());
        }
    }
    
    @GetMapping("/revenue/total")
    public ResponseEntity<?> getTotalRevenue() {
        try {
            Map<String, Object> result = consultantService.getTotalRevenue();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(400).body("Failed to get total revenue: " + e.getMessage());
        }
    }
}
