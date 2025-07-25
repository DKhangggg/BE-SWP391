package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.*;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.dto.response.DashboardReportDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;

import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.service.*;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.dto.request.AutoCreateTimeSlotsRequestDTO;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.repository.TimeSlotRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.gender_healthcare_service.entity.User;
import org.modelmapper.ModelMapper;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.service.ReportService;

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
    @Autowired
    private MenstrualCycleService menstrualCycleService;
    @Autowired
    private ReminderService reminderService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @PostMapping("/testing-services")
    public ResponseEntity<?> createTestingService(@RequestBody TestingService request) {
        boolean created = testingServiceService.createService(request);
        if(!created) {
            return new ResponseEntity<>("Tạo service mới failed", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Tạo service mới successful", HttpStatus.CREATED);
    }
    @GetMapping("/testing-services")
    public ResponseEntity<?> GetAllTestingService() {
        try {
            return new ResponseEntity<>(testingServiceService.getAllService(), HttpStatus.OK);
        }catch (ServiceNotFoundException e){
            return new ResponseEntity<>("service not found", HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Failed to delete service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/testing-services/{serviceId}")
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
    public ResponseEntity<ApiResponse<TestingServiceResponseDTO>> deleteTestingService(@PathVariable Integer serviceId) {
        try {
            TestingServiceResponseDTO response = testingServiceService.deleteService(serviceId, true);
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.success(
                "Service deleted successfully", 
                response
            );
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ServiceNotFoundException e) {
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Service not found with ID: " + serviceId);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting testing service: {}", e.getMessage(), e);
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Failed to delete service");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/testing-services/{serviceId}/restore")
    public ResponseEntity<ApiResponse<TestingServiceResponseDTO>> restoreTestingService(@PathVariable Integer serviceId) {
        try {
            TestingServiceResponseDTO response = testingServiceService.deleteService(serviceId, false);
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.success(
                "Service restored successfully", 
                response
            );
            return new ResponseEntity<>(apiResponse, HttpStatus.OK);
        } catch (ServiceNotFoundException e) {
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Service not found with ID: " + serviceId);
            return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error restoring testing service: {}", e.getMessage(), e);
            ApiResponse<TestingServiceResponseDTO> apiResponse = ApiResponse.error("Failed to restore service");
            return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/testing-services/bookings")
    public ResponseEntity<PageResponse<BookingResponseDTO>> viewAllTestBookings(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO bookings = bookingService.getAllBookingsForStaff(pageable);
        PageResponse<BookingResponseDTO> response = new PageResponse<>();
        response.setContent(bookings.getContent());
        response.setPageNumber(bookings.getPageNumber());
        response.setPageSize(bookings.getPageSize());
        response.setTotalElements(bookings.getTotalElements());
        response.setTotalPages(bookings.getTotalPages());
        response.setHasNext(bookings.isHasNext());
        response.setHasPrevious(bookings.isHasPrevious());
        return ResponseEntity.ok(response);
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

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardReportDTO> getDashboardStats(@RequestParam int month, @RequestParam int year) {
        DashboardReportDTO stats = reportService.getDashboardStats(month, year);
        return ResponseEntity.ok(stats);
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
    public ResponseEntity<?> updateConsultantDetailsAdmin(@PathVariable Integer consultantId, @RequestBody ConsultantUpdateDTO updateDTO) {
        updateDTO.setId(consultantId); // Đảm bảo id đúng từ path variable
        consultantService.updateConsultant(updateDTO);
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
    public ResponseEntity<?> getUsersAdmin(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        Page<User> usersPage = userService.getAllUsers(pageable);
        Page<UserResponseDTO> dtoPage = usersPage.map(user -> modelMapper.map(user, UserResponseDTO.class));
        return ResponseEntity.ok(dtoPage);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUserAdmin(@PathVariable Integer userId, @RequestBody(required = false) AdminUpdateUserRequestDTO updateUserDTO) {
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
    public ResponseEntity<PageResponse<ConsultationBookingResponseDTO>> getAllConsultationBookingsAdmin(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Integer consultantId,
            @RequestParam(defaultValue = "1") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            Page<ConsultationBookingResponseDTO> bookings = consultationService.getAllConsultationBookingsForAdminPaginated(date, status, userId, consultantId, pageable);
            PageResponse<ConsultationBookingResponseDTO> response = new PageResponse<>();
            response.setContent(bookings.getContent());
            response.setPageNumber(bookings.getNumber() + 1);
            response.setPageSize(bookings.getSize());
            response.setTotalElements(bookings.getTotalElements());
            response.setTotalPages(bookings.getTotalPages());
            response.setHasNext(bookings.hasNext());
            response.setHasPrevious(bookings.hasPrevious());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching consultation bookings for admin: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
    //Menstrual Cycle Management
    @GetMapping("/menstrual-cycles/{userId}")
    public ResponseEntity<?> getMenstrualCycleDetailsAdmin(@PathVariable Integer userId) {
        try{
            boolean existingUser = authenticationService.isUserExists(userId);
            if (!existingUser) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            //List<MenstrualLogResponseDTO> menstrualLogs = menstrualCycleService.getMenstrualLogs(userId);
            return new ResponseEntity<>("notok", HttpStatus.OK);
        }
        catch (Exception e) {
            logger.error("Error fetching menstrual cycle details for user {}: {}", userId, e.getMessage(), e);
            return new ResponseEntity<>("Failed to fetch menstrual cycle details.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Reminder
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

    @PostMapping("/timeslots/auto-create")
    public ResponseEntity<?> autoCreateCommonSlots(@RequestBody AutoCreateTimeSlotsRequestDTO req) {
        LocalDate today = req.getStartDate() != null && !req.getStartDate().isEmpty()
            ? LocalDate.parse(req.getStartDate())
            : LocalDate.now();
        int days = req.getDays() != null ? req.getDays() : 7;
        List<TimeSlot> slots = new ArrayList<>();
        for (int d = 0; d < days; d++) {
            LocalDate slotDate = today.plusDays(d);
            // 4 khung giờ mẫu
            LocalTime[][] slotTimes = {
                {LocalTime.of(8,0), LocalTime.of(10,0)},
                {LocalTime.of(10,0), LocalTime.of(12,0)},
                {LocalTime.of(13,0), LocalTime.of(15,0)},
                {LocalTime.of(15,0), LocalTime.of(17,0)}
            };
            for (LocalTime[] times : slotTimes) {
                TimeSlot slot = new TimeSlot();
                slot.setSlotDate(slotDate);
                slot.setStartTime(times[0]);
                slot.setEndTime(times[1]);
                slot.setConsultant(null); // Không gán consultant
                slot.setSlotType(req.getSlotType() != null ? req.getSlotType() : "CONSULTATION");
                slot.setCapacity(req.getCapacity() != null ? req.getCapacity() : 1);
                slot.setBookedCount(0);
                slot.setIsAvailable(true);
                slot.setIsDeleted(false);
                slot.setDescription(req.getDescription());
                slot.setDuration(req.getDuration() != null ? req.getDuration() : (int) java.time.Duration.between(times[0], times[1]).toMinutes());
                slot.setCreatedAt(LocalDateTime.now());
                slots.add(slot);
            }
        }
        timeSlotRepository.saveAll(slots);
        return ResponseEntity.ok("Đã tạo " + slots.size() + " slot chung cho hệ thống trong " + days + " ngày!");
    }

    @GetMapping("/orders")
    public ResponseEntity<PageResponse<BookingResponseDTO>> getOrders(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO orders = bookingService.getAllBookingsForStaff(pageable);
        PageResponse<BookingResponseDTO> response = new PageResponse<>();
        response.setContent(orders.getContent());
        response.setPageNumber(orders.getPageNumber());
        response.setPageSize(orders.getPageSize());
        response.setTotalElements(orders.getTotalElements());
        response.setTotalPages(orders.getTotalPages());
        response.setHasNext(orders.isHasNext());
        response.setHasPrevious(orders.isHasPrevious());
        return ResponseEntity.ok(response);
    }
}
