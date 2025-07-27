package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.BookingFilterRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultantCreateBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateTestResultRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.BookingPageResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Đặt lịch thành công", createdBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi đặt lịch: " + e.getMessage()));
        }
    }

    @PostMapping("/consultant-create")
    @PreAuthorize("hasAuthority('ROLE_CONSULTANT')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> createBookingForUser(@RequestBody ConsultantCreateBookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO createdBooking = bookingService.createBookingForUser(bookingRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Tạo lịch hẹn cho người dùng thành công", createdBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi tạo lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<List<BookingResponseDTO>>> getCurrentUserBookings() {
        try {
            List<BookingResponseDTO> bookings = bookingService.getUserBookings();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", bookings));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/{bookingId}/my-booking")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingByIdForCurrentUser(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdForUser(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lịch hẹn thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/{bookingId}/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> getBookingByIdForAdmin(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO booking = bookingService.getBookingByIdForAdmin(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin lịch hẹn thành công", booking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy thông tin lịch hẹn: " + e.getMessage()));
        }
    }

    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateBookingStatus(@PathVariable Integer bookingId, @RequestBody UpdateBookingStatusRequestDTO statusRequestDTO) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(bookingId, statusRequestDTO);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái lịch hẹn thành công", updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật trạng thái lịch hẹn: " + e.getMessage()));
        }
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBooking(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO cancelledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", cancelledBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy lịch hẹn: " + e.getMessage()));
        }
    }

    @PatchMapping("/{bookingId}/confirm")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> confirmBooking(@PathVariable Integer bookingId) {
        try {
            BookingResponseDTO confirmedBooking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.ok(ApiResponse.success("Xác nhận lịch hẹn thành công", confirmedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi xác nhận lịch hẹn: " + e.getMessage()));
        }
    }

    @PatchMapping("/{bookingId}/cancel-with-response")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> cancelBookingWithResponse(@PathVariable Integer bookingId) {
        try {
            ResponseEntity<?> response = bookingService.cancelBookingWithResponse(bookingId);
            if (response.getBody() instanceof BookingResponseDTO) {
                return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", (BookingResponseDTO) response.getBody()));
            } else {
                return ResponseEntity.ok(ApiResponse.success("Hủy lịch hẹn thành công", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi hủy lịch hẹn: " + e.getMessage()));
        }
    }

    @PatchMapping("/{bookingId}/test-result")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingResponseDTO>> updateTestResult(@PathVariable Integer bookingId,
                                                              @RequestBody UpdateTestResultRequestDTO resultRequest) {
        try {
            BookingResponseDTO updatedBooking = bookingService.updateTestResult(bookingId, resultRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật kết quả xét nghiệm thành công", updatedBooking));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi cập nhật kết quả xét nghiệm: " + e.getMessage()));
        }
    }

    // New pagination endpoints
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getAllBookingsForStaffOrManager(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO response = bookingService.getAllBookingsForStaff(pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsWithFilters(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Integer customerId,
        @RequestParam(required = false) Integer serviceId,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(required = false) String customerName,
        @RequestParam(required = false) String serviceName,
        @RequestParam(defaultValue = "createdAt") String sortBy,
        @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            
            BookingFilterRequestDTO filter = new BookingFilterRequestDTO();
            filter.setStatus(status);
            filter.setCustomerId(customerId);
            filter.setServiceId(serviceId);
            filter.setFromDate(fromDate);
            filter.setToDate(toDate);
            filter.setCustomerName(customerName);
            filter.setServiceName(serviceName);
            filter.setSortBy(sortBy);
            filter.setSortDirection(sortDirection);
            
            BookingPageResponseDTO response = bookingService.getBookingsWithFilters(filter, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lọc danh sách lịch hẹn thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lọc danh sách lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO response = bookingService.getBookingsByStatus(status, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo trạng thái thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn theo trạng thái: " + e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByCustomer(
        @PathVariable Integer customerId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO response = bookingService.getBookingsByCustomer(customerId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo khách hàng thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn theo khách hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByService(
        @PathVariable Integer serviceId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO response = bookingService.getBookingsByService(serviceId, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo dịch vụ thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn theo dịch vụ: " + e.getMessage()));
        }
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<BookingPageResponseDTO>> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            BookingPageResponseDTO response = bookingService.getBookingsByDateRange(fromDate, toDate, pageable);
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn theo khoảng thời gian thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn theo khoảng thời gian: " + e.getMessage()));
        }
    }

    // Statistics endpoints
    @GetMapping("/statistics/total")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getTotalBookings() {
        try {
            long total = bookingService.getTotalBookings();
            return ResponseEntity.ok(ApiResponse.success("Lấy tổng số lịch hẹn thành công", total));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy tổng số lịch hẹn: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByStatus(@PathVariable String status) {
        try {
            long count = bookingService.getBookingsByStatus(status);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lượng lịch hẹn theo trạng thái thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lượng lịch hẹn theo trạng thái: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByCustomer(@PathVariable Integer customerId) {
        try {
            long count = bookingService.getBookingsByCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lượng lịch hẹn theo khách hàng thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lượng lịch hẹn theo khách hàng: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByService(@PathVariable Integer serviceId) {
        try {
            long count = bookingService.getBookingsByService(serviceId);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lượng lịch hẹn theo dịch vụ thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lượng lịch hẹn theo dịch vụ: " + e.getMessage()));
        }
    }

    @GetMapping("/statistics/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Long>> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        try {
            long count = bookingService.getBookingsByDateRange(fromDate, toDate);
            return ResponseEntity.ok(ApiResponse.success("Lấy số lượng lịch hẹn theo khoảng thời gian thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy số lượng lịch hẹn theo khoảng thời gian: " + e.getMessage()));
        }
    }

    // Legacy endpoint for backward compatibility
    @GetMapping("/all-legacy")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<BookingResponseDTO>>> getAllBookingsLegacy(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
            var bookings = bookingService.getAllBookingsForStaffLegacy(pageable);
            PageResponse<BookingResponseDTO> response = new PageResponse<>();
            response.setContent(bookings.getContent());
            response.setPageNumber(bookings.getNumber() + 1);
            response.setPageSize(bookings.getSize());
            response.setTotalElements(bookings.getTotalElements());
            response.setTotalPages(bookings.getTotalPages());
            response.setHasNext(bookings.hasNext());
            response.setHasPrevious(bookings.hasPrevious());
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách lịch hẹn thành công", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi khi lấy danh sách lịch hẹn: " + e.getMessage()));
        }
    }
}
