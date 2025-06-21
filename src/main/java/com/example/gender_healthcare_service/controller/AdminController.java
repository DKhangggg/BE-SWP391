package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;

import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.dto.request.TestingServiceUpdateDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.service.*;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.dto.request.AddUnavailabilityRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantScheduleResponseDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationBookingResponseDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.AdminUpdateUserRequestDTO;

import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/admin")
@RestController()
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private static final int DEFAULT_SCHEDULE_WEEKS = 4;

    @Autowired
    private UserService userService;
    @Autowired
    private ConsultantService consultantService;
    @Autowired
    private ConsultationService consultationService;
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private TestingServiceService testingServiceService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ConsultantScheduleService consultantScheduleService;
    @Autowired
    private TransactionHistoryService transactionHistoryService;

    // Testing Services Management
   @PostMapping("/testing-services")
    public ResponseEntity<?> createTestingService(@RequestBody TestingService request) {
        boolean created = testingServiceService.createService(request);
        if(!created) {
            return new ResponseEntity<>("Service creation failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Service creation successful", HttpStatus.CREATED);
    }

    @PutMapping("/testing-services/{serviceId}")
    public ResponseEntity<?> updateTestingService(@PathVariable Integer serviceId, @RequestBody TestingServiceUpdateDTO updateDTO) {
        TestingServiceResponseDTO updated = testingServiceService.updateService(serviceId, updateDTO);
        if(updated!= null) {
            return new ResponseEntity<>("Service updated successfully", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Service update failed", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/testing-services/{serviceId}")
    public ResponseEntity<?> deleteTestingService(@PathVariable Integer serviceId) {
        testingServiceService.deleteService(serviceId,true);
        return new ResponseEntity<>("Service delete successfully", HttpStatus.OK);
    }

    @GetMapping("/testing-services/bookings")
    public ResponseEntity<?> viewAllTestBookings() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookingsForStaff();
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @PutMapping("/testing-services/bookings/{bookingId}/results")
    public ResponseEntity<?> manageTestResults(@PathVariable Integer bookingId, @RequestBody UpdateBookingStatusRequestDTO Status) {
        BookingResponseDTO updated = bookingService.updateBookingStatus(bookingId, Status);
        if (updated==null) {
            return new ResponseEntity<>("Failed to update test results", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Test results updated successfully", HttpStatus.OK);
    }
    // Feedback Management
    @GetMapping("/feedback")
    public ResponseEntity<?> viewAllFeedback() {
        // TODO: Implement view all feedback logic
        return null;
    }

    @PutMapping("/feedback/{feedbackId}/status")
    public ResponseEntity<?> moderateFeedback(@PathVariable Long feedbackId/*, @RequestBody FeedbackStatusDTO statusDTO*/) {
        // TODO: Implement moderate/approve feedback logic
        return null;
    }

    // Dashboard & Reports
    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        // TODO: Implement get key statistics for the dashboard logic
        return null;
    }

    // Consultant Management
    @GetMapping("/consultants/{consultantId}")
    public ResponseEntity<?> getConsultantDetailsAdmin(@PathVariable Integer consultantId) {
        try {
            ConsultantDTO consultant = consultantService.getConsultantById(consultantId);
            if (consultant == null) {
                return new ResponseEntity<>("Consultant not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(consultant, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching consultant details for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to fetch consultant details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/consultants/{consultantId}/schedule")
    public ResponseEntity<?> getConsultantScheduleAdmin(@PathVariable Integer consultantId) {
        try {
            ConsultantScheduleResponseDTO schedule = consultantScheduleService.getConsultantSchedule(consultantId);
            if (schedule == null) {
                return new ResponseEntity<>("Schedule not found for consultant", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(schedule, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching consultant schedule for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to fetch consultant schedule.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/consultants/{consultantId}/unavailability")
    public ResponseEntity<?> addConsultantUnavailabilityAdmin(@PathVariable Integer consultantId, @RequestBody AddUnavailabilityRequestDTO request) {
        try {
            consultantScheduleService.addUnavailability(consultantId, request);
            return ResponseEntity.ok("Unavailability added successfully.");
        } catch (Exception e) {
            logger.error("Error adding unavailability for consultant {}: {}", consultantId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to add unavailability.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/listConsultant")
    public ResponseEntity<List<ConsultantDTO>> getConsultantInfo() {
        List<ConsultantDTO> consultantDTOList= consultantService.getAllConsultants();
        if(consultantDTOList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(consultantDTOList, HttpStatus.OK);
        }
    }

    @PostMapping("/consultants")
    public ResponseEntity<?> createNewConsultant(@RequestBody CreateNewConsultantRequest request) {
        Consultant consultant=consultantService.createNewConsultant(request);
        if(consultant == null) {
            return new ResponseEntity<>("Consultant creation failed", HttpStatus.BAD_REQUEST);
        }
        consultantScheduleService.createDefaultScheduleForConsultant(consultant, LocalDate.now(), DEFAULT_SCHEDULE_WEEKS);
        return ResponseEntity.ok("Consultant created successfully. Default schedule creation attempted.");
    }

    @PutMapping("/setUserToConsultant/{id}")
    public ResponseEntity<?> setConsultantUser(@PathVariable("id") Integer userId) {
        authenticationService.setConsultantUser(userId);
        try {
            Consultant consultant = consultantService.findConsultantByUserId(userId);
            if (consultant != null) {
                consultantScheduleService.createDefaultScheduleForConsultant(consultant, LocalDate.now(), DEFAULT_SCHEDULE_WEEKS);
                logger.info("Successfully created default schedule for user ID: {} set to consultant.", userId);
            } else {
                logger.warn("Could not find consultant record for user ID: {} after setting role. Schedule not created.", userId);
            }
        } catch (Exception e) {
            logger.error("Error creating default schedule for user ID {}: {}", userId, e.getMessage(), e);
        }
        return ResponseEntity.ok("Set consultant role successfully. Default schedule creation attempted.");
    }

    @PutMapping("/consultants/{consultantId}")
    public ResponseEntity<?> updateConsultantDetailsAdmin(@PathVariable Integer consultantId, @RequestBody ConsultantDTO updateDTO) {
        consultantService.updateConsultant(consultantId, updateDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/consultants/{consultantId}")
    public ResponseEntity<?> removeConsultant(@PathVariable Integer consultantId) {
        consultantService.deleteConsultant(consultantId);
        return new ResponseEntity<>("Consultant successfully", HttpStatus.OK);
    }
    //User Management
    @GetMapping("/users/{userId}")
    public ResponseEntity<?> getUserDetailsAdmin(@PathVariable Integer userId) {
        UserResponseDTO user = authenticationService.findUserById(userId);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
    @GetMapping("/users")
    public ResponseEntity<?> getUsersAdmin() {
       List<UserResponseDTO> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUserAdmin(@PathVariable Integer userId, @RequestBody AdminUpdateUserRequestDTO updateUserDTO) {
        try {
            UserResponseDTO updatedUser = userService.updateUserByAdmin(userId, updateUserDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error updating user: {}", e.getMessage(), e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUserAdmin(@PathVariable Integer userId) {
        try {
            userService.deleteUserByAdmin(userId);
            return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error deleting user: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to delete user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Consultation Booking Management by Admin
    @GetMapping("/consultation-bookings")
    public ResponseEntity<?> getAllConsultationBookingsAdmin(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer consultantId) {
        try {
            List<ConsultationBookingResponseDTO> bookings = consultationService.getAllConsultationBookingsForAdmin(date, status, userId, consultantId);
            if (bookings.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching consultation bookings for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to fetch consultation bookings.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/consultation-bookings/{bookingId}")
    public ResponseEntity<?> getConsultationBookingDetailsAdmin(@PathVariable Integer bookingId) {
        try {
            ConsultationBookingResponseDTO booking = consultationService.getConsultationBookingByIdForAdmin(bookingId);
            if (booking == null) {
                return new ResponseEntity<>("Consultation booking not found", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error fetching consultation booking details for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>("Failed to fetch consultation booking details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/consultation-bookings/{bookingId}/status")
    public ResponseEntity<?> updateConsultationBookingStatusAdmin(@PathVariable Integer bookingId, @RequestBody UpdateConsultationStatusRequestDTO statusRequest) {
        try {
            ConsultationBookingResponseDTO updatedBooking = consultationService.updateConsultationBookingStatusByAdmin(bookingId, statusRequest);
            return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Invalid status value") || e.getMessage().contains("not found")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("Failed to update consultation booking status.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/consultation-bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelConsultationBookingAdmin(@PathVariable Integer bookingId, @RequestBody(required = false) String adminNotes) {
        try {
            consultationService.cancelConsultationBookingByAdmin(bookingId, adminNotes);
            return ResponseEntity.ok("Consultation booking cancelled successfully by admin.");
        } catch (RuntimeException e) {
            logger.error("Error cancelling consultation booking by admin: {}", e.getMessage(), e);
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Failed to cancel consultation booking.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/consultation-bookings/{bookingId}/reschedule")
    public ResponseEntity<?> rescheduleConsultationBookingAdmin(@PathVariable Integer bookingId, @RequestBody RescheduleBookingRequestDTO rescheduleRequest) {
        try {
            ConsultationBookingResponseDTO updatedBooking = consultationService.rescheduleConsultationBookingByAdmin(bookingId, rescheduleRequest);
            return new ResponseEntity<>(updatedBooking, HttpStatus.OK);
        } catch (RuntimeException e) {
            logger.error("Error rescheduling consultation booking by admin: {}", e.getMessage(), e);
            if (e.getMessage().contains("not found")) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>("Failed to reschedule consultation booking.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


//    @GetMapping("/reports/bookings")
//    public ResponseEntity<?> generateBookingsReport() {
//        try {
//            Object reportData = bookingService.generateBookingsReportData();
//            if (reportData == null) {
//                return new ResponseEntity<>("No booking data available for report.", HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(reportData, HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("Error generating bookings report: {}", e.getMessage(), e);
//            return new ResponseEntity<>("Failed to generate bookings report.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @GetMapping("/reports/financials")
//    public ResponseEntity<?> generateFinancialsReport() {
//        try {
//            Object reportData = transactionHistoryService.generateFinancialsReportData();
//            if (reportData == null) {
//                return new ResponseEntity<>("No financial data available for report.", HttpStatus.NOT_FOUND);
//            }
//            return new ResponseEntity<>(reportData, HttpStatus.OK);
//        } catch (Exception e) {
//            logger.error("Error generating financials report: {}", e.getMessage(), e);
//            return new ResponseEntity<>("Failed to generate financials report.", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


}
