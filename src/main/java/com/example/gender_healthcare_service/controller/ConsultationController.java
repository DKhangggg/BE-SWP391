package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.ConsultationBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultantCreateConsultationRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationStatusUpdateDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationConfirmationDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<ConsultantAvailabilityResponseDTO>>> getConsultantAvailability(
            @PathVariable Integer consultantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ConsultantAvailabilityResponseDTO> availableSlots = consultationService.getConsultantAvailability(consultantId, date);
            return ResponseEntity.ok(ApiResponse.success("Lấy lịch trống của tư vấn viên thành công", availableSlots));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy lịch trống của tư vấn viên: " + e.getMessage()));
        }
    }

    @PostMapping("/book")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> scheduleConsultation(
            @Valid @RequestBody ConsultationBookingRequestDTO bookingRequest) {
        try {
            ConsultationBookingResponseDTO booking = consultationService.bookConsultation(bookingRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đặt lịch tư vấn thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đặt lịch tư vấn: " + e.getMessage()));
        }
    }

    @PostMapping("/consultant-create")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> createConsultationForUser(
            @Valid @RequestBody ConsultantCreateConsultationRequestDTO consultationRequest) {
        try {
            ConsultationBookingResponseDTO consultation = consultationService.createConsultationForUser(consultationRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo lịch tư vấn cho người dùng thành công", consultation));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo lịch tư vấn: " + e.getMessage()));
        }
    }

    @GetMapping("/user-bookings")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ConsultationBookingResponseDTO>>> getUserBookings(
            @RequestParam(required = false) String status) {
        try {
            List<ConsultationBookingResponseDTO> bookings = consultationService.getUserConsultations(status);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch tư vấn thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch tư vấn: " + e.getMessage()));
        }
    }

    @GetMapping("/consultant-bookings")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationBookingResponseDTO>>> getConsultantBookings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String status) {
        try {
            List<ConsultationBookingResponseDTO> bookings = consultationService.getConsultantBookings(date, status);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch tư vấn của tư vấn viên thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch tư vấn: " + e.getMessage()));
        }
    }

    @PutMapping("/{consultationId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> updateConsultationStatus(
            @PathVariable Integer consultationId,
            @Valid @RequestBody ConsultationStatusUpdateDTO statusUpdateDTO) {
        try {
            ConsultationBookingResponseDTO updatedBooking = consultationService.updateConsultationStatus(consultationId, statusUpdateDTO);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái tư vấn thành công", updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái tư vấn: " + e.getMessage()));
        }
    }

    @PutMapping("/{consultationId}/confirm")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> confirmConsultation(
            @PathVariable Integer consultationId,
            @Valid @RequestBody ConsultationConfirmationDTO confirmationDTO) {
        try {
            ConsultationBookingResponseDTO confirmedBooking = consultationService.confirmConsultation(consultationId, confirmationDTO);
            return ResponseEntity.ok(ApiResponse.success("Xác nhận lịch tư vấn thành công", confirmedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xác nhận lịch tư vấn: " + e.getMessage()));
        }
    }

    @GetMapping("/{consultationId}")
    public ResponseEntity<ApiResponse<ConsultationDetailResponseDTO>> getConsultationDetails(@PathVariable Integer consultationId) {
        try {
            ConsultationDetailResponseDTO details = consultationService.getConsultationDetails(consultationId);
            if(details == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Không tìm thấy lịch tư vấn"));
            }
            return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết lịch tư vấn thành công", details));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy chi tiết lịch tư vấn: " + e.getMessage()));
        }
    }

    @PutMapping("/{consultationId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> cancelConsultation(@PathVariable Integer consultationId) {
        try {
            ConsultationBookingResponseDTO cancelledBooking = consultationService.cancelConsultation(consultationId);
            return ResponseEntity.ok(ApiResponse.success("Hủy lịch tư vấn thành công", cancelledBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy lịch tư vấn: " + e.getMessage()));
        }
    }

    @GetMapping("/upcoming")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ConsultationBookingResponseDTO>>> getUpcomingConsultations() {
        try {
            List<ConsultationBookingResponseDTO> upcomingBookings = consultationService.getUserUpcomingConsultations();
            return ResponseEntity.ok(ApiResponse.success("Lấy lịch tư vấn sắp tới thành công", upcomingBookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy lịch tư vấn sắp tới: " + e.getMessage()));
        }
    }

    // API cho consultant xem danh sách lịch hẹn chờ xác nhận
    @GetMapping("/consultant/pending-appointments")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationBookingResponseDTO>>> getPendingAppointments() {
        try {
            List<ConsultationBookingResponseDTO> pendingAppointments = consultationService.getPendingAppointments();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn chờ xác nhận thành công", pendingAppointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn chờ xác nhận: " + e.getMessage()));
        }
    }

    // API cho consultant xem tất cả lịch hẹn của mình
    @GetMapping("/consultant/my-appointments")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<ConsultationBookingResponseDTO>>> getMyAppointments(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            List<ConsultationBookingResponseDTO> appointments = consultationService.getMyAppointments(status, date);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn của tôi thành công", appointments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    // API xác nhận lịch hẹn và tạo link meeting
    @PostMapping("/consultant/{consultationId}/confirm-with-meeting")
    @PreAuthorize("hasAnyAuthority('ROLE_CONSULTANT', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<ConsultationBookingResponseDTO>> confirmWithMeetingLink(
            @PathVariable Integer consultationId) {
        try {
            ConsultationBookingResponseDTO confirmedBooking = consultationService.confirmWithMeetingLink(consultationId);
            return ResponseEntity.ok(ApiResponse.success("Xác nhận lịch hẹn và tạo link meeting thành công", confirmedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xác nhận lịch hẹn: " + e.getMessage()));
        }
    }
}
