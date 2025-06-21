package com.example.gender_healthcare_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/consultation")
public class ConsultationController {

    @PostMapping("/schedule")
    public ResponseEntity<?> scheduleConsultation() {
        return null;
    }

    @GetMapping("/user-bookings")
    public ResponseEntity<?> getUserBookings() {
        // TODO: Implement get user's upcoming/past consultations logic
        return null;
    }

    @GetMapping("/consultant-bookings/{consultantId}")
    public ResponseEntity<?> getConsultantBookings(@PathVariable Long consultantId) {
        // TODO: Implement get consultant's upcoming/past consultations logic
        return null;
    }

    @PutMapping("/{consultationId}/status")
    public ResponseEntity<?> updateConsultationStatus(@PathVariable Long consultationId/*, @RequestBody ConsultationStatusUpdateDTO statusUpdateDTO*/) {
        // TODO: Implement update consultation status logic
        return null;
    }

    @GetMapping("/{consultationId}/details")
    public ResponseEntity<?> getConsultationDetails(@PathVariable Long consultationId) {
        // TODO: Implement get details of a specific consultation logic
        return null;
    }

}
