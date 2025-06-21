package com.example.gender_healthcare_service.controller;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDTO> createBooking(@RequestBody BookingRequestDTO bookingRequestDTO) {
        BookingResponseDTO createdBooking = bookingService.createBooking(bookingRequestDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
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

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_STAFF', 'ROLE_MANAGER', 'ROLE_ADMIN')") // Admin can also access this
    public ResponseEntity<List<BookingResponseDTO>> getAllBookingsForStaffOrManager() {
        List<BookingResponseDTO> bookings = bookingService.getAllBookingsForStaff();
        return ResponseEntity.ok(bookings);
    }

}
