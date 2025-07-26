package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ConsultationBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationStatusUpdateDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationConfirmationDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationBookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationDetailResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantAvailabilityResponseDTO;
import com.example.gender_healthcare_service.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/consultation")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping("/consultant/{consultantId}/availability")
    public ResponseEntity<?> getConsultantAvailability(
            @PathVariable Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ConsultantAvailabilityResponseDTO> availableSlots = consultationService.getConsultantAvailability(consultantId, date);
        return ResponseEntity.ok(availableSlots);
    }

    @PostMapping("/book")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ConsultationBookingResponseDTO> scheduleConsultation(
            @Valid @RequestBody ConsultationBookingRequestDTO bookingRequest) {
        ConsultationBookingResponseDTO booking = consultationService.bookConsultation(bookingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/user-bookings")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<ConsultationBookingResponseDTO>> getUserBookings(
            @RequestParam(required = false) String status) {
        List<ConsultationBookingResponseDTO> bookings = consultationService.getUserConsultations(status);
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/consultant-bookings")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationBookingResponseDTO>> getConsultantBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status) {
        List<ConsultationBookingResponseDTO> bookings = consultationService.getConsultantBookings(date, status);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{consultationId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ConsultationBookingResponseDTO> updateConsultationStatus(
            @PathVariable Integer consultationId,
            @Valid @RequestBody ConsultationStatusUpdateDTO statusUpdateDTO) {
        ConsultationBookingResponseDTO updatedBooking = consultationService.updateConsultationStatus(consultationId, statusUpdateDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @PutMapping("/{consultationId}/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ConsultationBookingResponseDTO> confirmConsultation(
            @PathVariable Integer consultationId,
            @Valid @RequestBody ConsultationConfirmationDTO confirmationDTO) {
        ConsultationBookingResponseDTO confirmedBooking = consultationService.confirmConsultation(consultationId, confirmationDTO);
        return ResponseEntity.ok(confirmedBooking);
    }

    @GetMapping("/{consultationId}")
    public ResponseEntity<ConsultationDetailResponseDTO> getConsultationDetails(@PathVariable Integer consultationId) {
        ConsultationDetailResponseDTO details = consultationService.getConsultationDetails(consultationId);
        if(details==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(details);
    }

    @PutMapping("/{consultationId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT')")
    public ResponseEntity<ConsultationBookingResponseDTO> cancelConsultation(@PathVariable Integer consultationId) {
        ConsultationBookingResponseDTO cancelledBooking = consultationService.cancelConsultation(consultationId);
        return ResponseEntity.ok(cancelledBooking);
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<List<ConsultationBookingResponseDTO>> getUpcomingConsultations() {
        List<ConsultationBookingResponseDTO> upcomingBookings = consultationService.getUserUpcomingConsultations();
        return ResponseEntity.ok(upcomingBookings);
    }

    // API cho consultant xem danh sách lịch hẹn chờ xác nhận
    @GetMapping("/consultant/pending-appointments")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationBookingResponseDTO>> getPendingAppointments() {
        List<ConsultationBookingResponseDTO> pendingAppointments = consultationService.getPendingAppointments();
        return ResponseEntity.ok(pendingAppointments);
    }

    // API cho consultant xem tất cả lịch hẹn của mình
    @GetMapping("/consultant/my-appointments")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<List<ConsultationBookingResponseDTO>> getMyAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<ConsultationBookingResponseDTO> appointments = consultationService.getMyAppointments(status, date);
        return ResponseEntity.ok(appointments);
    }

    // API xác nhận lịch hẹn và tạo link meeting
    @PostMapping("/consultant/{consultationId}/confirm-with-meeting")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ConsultationBookingResponseDTO> confirmWithMeetingLink(
            @PathVariable Integer consultationId) {
        ConsultationBookingResponseDTO confirmedBooking = consultationService.confirmWithMeetingLink(consultationId);
        return ResponseEntity.ok(confirmedBooking);
    }
}
