package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.BookingFilterRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.BookingPageResponseDTO;
import com.example.gender_healthcare_service.dto.response.PageResponse;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.service.BookingService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Booking Management", description = "API endpoints for managing healthcare service bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        try {
            BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
            ApiResponse response = ApiResponse.success("Đặt lịch thành công", createdBooking);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            // Exception sẽ được handle bởi GlobalExceptionHandler
            throw e;
        }
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<List<BookingResponseDTO>> getCurrentUserBookings() {
        List<BookingResponseDTO> bookings = bookingService.getUserBookings();
        return ResponseEntity.ok(bookings);
    }

    @GetMapping("/{bookingId}/my-booking")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDTO> getBookingByIdForCurrentUser(@PathVariable Integer bookingId) {
        BookingResponseDTO booking = bookingService.getBookingByIdForUser(bookingId);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/{bookingId}/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDTO> getBookingByIdForAdmin(@PathVariable Integer bookingId) {
        BookingResponseDTO booking = bookingService.getBookingByIdForAdmin(bookingId);
        return ResponseEntity.ok(booking);
    }

    @PatchMapping("/{bookingId}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_STAFF', 'ROLE_MANAGER')")
    public ResponseEntity<BookingResponseDTO> updateBookingStatus(@PathVariable Integer bookingId, @RequestBody UpdateBookingStatusRequestDTO statusRequestDTO) {
        BookingResponseDTO updatedBooking = bookingService.updateBookingStatus(bookingId, statusRequestDTO);
        return ResponseEntity.ok(updatedBooking);
    }

    @PatchMapping("/{bookingId}/cancel")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDTO> cancelBooking(@PathVariable Integer bookingId) {
        BookingResponseDTO cancelledBooking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(cancelledBooking);
    }

    @PatchMapping("/{bookingId}/cancel-with-response")
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<?> cancelBookingWithResponse(@PathVariable Integer bookingId) {
        return bookingService.cancelBookingWithResponse(bookingId);

    }

    // New pagination endpoints
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getAllBookingsForStaffOrManager(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO response = bookingService.getAllBookingsForStaff(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getBookingsWithFilters(
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getBookingsByStatus(
        @PathVariable String status,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO response = bookingService.getBookingsByStatus(status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getBookingsByCustomer(
        @PathVariable Integer customerId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO response = bookingService.getBookingsByCustomer(customerId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getBookingsByService(
        @PathVariable Integer serviceId,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO response = bookingService.getBookingsByService(serviceId, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingPageResponseDTO> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize);
        BookingPageResponseDTO response = bookingService.getBookingsByDateRange(fromDate, toDate, pageable);
        return ResponseEntity.ok(response);
    }

    // Statistics endpoints
    @GetMapping("/statistics/total")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> getTotalBookings() {
        long total = bookingService.getTotalBookings();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/statistics/status/{status}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> getBookingsByStatus(@PathVariable String status) {
        long count = bookingService.getBookingsByStatus(status);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> getBookingsByCustomer(@PathVariable Integer customerId) {
        long count = bookingService.getBookingsByCustomer(customerId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/service/{serviceId}")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> getBookingsByService(@PathVariable Integer serviceId) {
        long count = bookingService.getBookingsByService(serviceId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/statistics/date-range")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<Long> getBookingsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        long count = bookingService.getBookingsByDateRange(fromDate, toDate);
        return ResponseEntity.ok(count);
    }

    // Legacy endpoint for backward compatibility
    @GetMapping("/all-legacy")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<PageResponse<BookingResponseDTO>> getAllBookingsLegacy(
        @RequestParam(defaultValue = "1") int pageNumber,
        @RequestParam(defaultValue = "10") int pageSize
    ) {
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
        return ResponseEntity.ok(response);
    }
}
