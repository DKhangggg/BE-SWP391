package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.BookingFilterRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultantCreateBookingRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.BookingPageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateTestResultRequestDTO;
import com.example.gender_healthcare_service.entity.Booking;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    BookingResponseDTO createBookingForUser(ConsultantCreateBookingRequestDTO bookingRequestDTO);
    List<BookingResponseDTO> getUserBookings();
    BookingResponseDTO getBookingByIdForUser(Integer bookingId);
    BookingResponseDTO getBookingByIdForAdmin(Integer bookingId);
    BookingResponseDTO updateBookingStatus(Integer bookingId, UpdateBookingStatusRequestDTO status);
    BookingResponseDTO updateTestResult(Integer bookingId, UpdateTestResultRequestDTO resultRequest);
    BookingResponseDTO cancelBooking(Integer bookingId);
    BookingResponseDTO confirmBooking(Integer bookingId);
    ResponseEntity<?> cancelBookingWithResponse(Integer bookingId);
    
    // Pagination methods
    BookingPageResponseDTO getAllBookingsForStaff(Pageable pageable);
    BookingPageResponseDTO getBookingsWithFilters(BookingFilterRequestDTO filter, Pageable pageable);
    BookingPageResponseDTO getBookingsByStatus(String status, Pageable pageable);
    BookingPageResponseDTO getBookingsByCustomer(Integer customerId, Pageable pageable);
    BookingPageResponseDTO getBookingsByService(Integer serviceId, Pageable pageable);
    BookingPageResponseDTO getBookingsByDateRange(java.time.LocalDate fromDate, java.time.LocalDate toDate, Pageable pageable);
    
    // Statistics methods
    long getTotalBookings();
    long getBookingsByStatus(String status);
    long getBookingsByCustomer(Integer customerId);
    long getBookingsByService(Integer serviceId);
    long getBookingsByDateRange(java.time.LocalDate fromDate, java.time.LocalDate toDate);
    
    // Legacy methods for backward compatibility
    Page<BookingResponseDTO> getAllBookingsForStaffLegacy(Pageable pageable);
    BookingResponseDTO getBookingByIdAndUsername(Integer bookingId, String username);
}
