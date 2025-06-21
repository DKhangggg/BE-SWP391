package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TestingServiceRepository testingServiceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser== null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }
        TestingService service = testingServiceRepository.findById(bookingRequestDTO.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("TestingService not found with ID: " + bookingRequestDTO.getServiceId()));

        TimeSlot timeSlot = timeSlotRepository.findById(bookingRequestDTO.getTimeSlotId())
                .orElseThrow(() -> new ServiceNotFoundException("TimeSlot not found with ID: " + bookingRequestDTO.getTimeSlotId()));
        if (bookingRepository.existsByCustomerIDAndBookingDateAndTimeSlotTimeSlotID(currentUser, bookingRequestDTO.getBookingDate(), timeSlot.getTimeSlotID())) {
            throw new IllegalStateException("You already have a booking at this date and time slot.");
        }
        if (bookingRepository.existsByBookingDateAndTimeSlotTimeSlotIDAndStatusNot(bookingRequestDTO.getBookingDate(), timeSlot.getTimeSlotID(), "Cancelled")) {
            throw new IllegalStateException("This time slot is already booked.");
        }

        Booking booking = new Booking();
        booking.setCustomerID(currentUser);
        booking.setService(service);
        booking.setBookingDate(bookingRequestDTO.getBookingDate());
        booking.setTimeSlot(timeSlot);
        booking.setStatus("Scheduled");
        // booking.setNotes(bookingRequestDTO.getNotes()); // Uncomment if notes are used

        Booking savedBooking = bookingRepository.save(booking);
        return convertToDto(savedBooking);
    }

    @Override
    public List<BookingResponseDTO> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
                if(currentUser== null) {
                throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
                }
        return bookingRepository.findByCustomerID(currentUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO getBookingByIdForUser(Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }
        Booking booking = bookingRepository.findByIdAndCustomerID(bookingId, currentUser)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId + " for user " + currentPrincipalName));
        return convertToDto(booking);
    }

    @Override
    public BookingResponseDTO getBookingByIdForAdmin(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId));
        return convertToDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateBookingStatus(Integer bookingId, UpdateBookingStatusRequestDTO statusRequestDTO) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId));

        // TODO: Add validation for allowed status transitions if necessary
        booking.setStatus(statusRequestDTO.getStatus());
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
                if(currentUser==null) {
                throw  new ServiceNotFoundException("User not found: " + currentPrincipalName);
                }
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId));

        boolean isAdmin = currentUser.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!booking.getCustomerID().equals(currentUser) && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to cancel this booking.");
        }

        if ("Completed".equalsIgnoreCase(booking.getStatus()) || "Cancelled".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalStateException("Booking is already " + booking.getStatus() + " and cannot be cancelled.");
        }

        booking.setStatus("Cancelled");
        Booking cancelledBooking = bookingRepository.save(booking);
        return convertToDto(cancelledBooking);
    }

    @Override
    public List<BookingResponseDTO> getAllBookingsForStaff() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDTO getBookingByIdAndUsername(Integer bookingId, String username) {
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + username);
        }
        Booking booking = bookingRepository.findByIdAndCustomerID(bookingId, currentUser)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId + " for user " + username));
         if (booking.getIsDeleted() != null && booking.getIsDeleted()) {
             throw new ServiceNotFoundException("Booking not found with ID: " + bookingId + " for user " + username);
        }
        return convertToDto(booking);
    }


    private BookingResponseDTO convertToDto(Booking booking) {
        BookingResponseDTO dto = modelMapper.map(booking, BookingResponseDTO.class);
        dto.setCustomerId(booking.getCustomerID().getId());
        dto.setCustomerName(booking.getCustomerID().getFullName());
        dto.setServiceId(booking.getService().getId());
        dto.setServiceName(booking.getService().getServiceName());
        dto.setTimeSlotId(booking.getTimeSlot().getTimeSlotID());
        dto.setStartTime(booking.getTimeSlot().getStartTime());
        dto.setEndTime(booking.getTimeSlot().getEndTime());
        return dto;
    }
}
