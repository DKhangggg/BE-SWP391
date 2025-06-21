package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.entity.Booking;

import java.util.List;

public interface BookingService {
    BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO);
    List<BookingResponseDTO> getUserBookings();
    BookingResponseDTO getBookingByIdForUser(Integer bookingId);
    BookingResponseDTO getBookingByIdForAdmin(Integer bookingId);
    BookingResponseDTO updateBookingStatus(Integer bookingId, UpdateBookingStatusRequestDTO status);
    BookingResponseDTO cancelBooking(Integer bookingId);
    List<BookingResponseDTO> getAllBookingsForStaff();

    BookingResponseDTO getBookingByIdAndUsername(Integer bookingId, String username);
}
