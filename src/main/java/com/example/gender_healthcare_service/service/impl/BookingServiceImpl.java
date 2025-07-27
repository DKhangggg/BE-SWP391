package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.BookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.BookingFilterRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultantCreateBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateBookingStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateTestResultRequestDTO;
import com.example.gender_healthcare_service.dto.response.BookingResponseDTO;
import com.example.gender_healthcare_service.dto.response.BookingPageResponseDTO;
import com.example.gender_healthcare_service.dto.response.ApiResponse;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.exception.BookingConflictException;
import com.example.gender_healthcare_service.service.BookingService;
import com.example.gender_healthcare_service.service.BookingTrackingService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TestingServiceRepository testingServiceRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingTrackingService bookingTrackingService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO bookingRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }

        TestingService service = testingServiceRepository.findById(bookingRequestDTO.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("TestingService not found with ID: " + bookingRequestDTO.getServiceId()));

        TimeSlot timeSlot = timeSlotRepository.findById(bookingRequestDTO.getTimeSlotId())
                .orElseThrow(() -> new ServiceNotFoundException("TimeSlot not found with ID: " + bookingRequestDTO.getTimeSlotId()));

        // Validate time slot is available
        if (!timeSlot.isSlotAvailable()) {
            throw new IllegalStateException("This time slot is not available for booking.");
        }

        // Check if user already has a booking for this time slot
        if (bookingRepository.existsByCustomerIDAndTimeSlotAndStatusNot(currentUser, timeSlot, "CANCELLED")) {
            throw new BookingConflictException("Bạn đã đặt lịch cho khung giờ này.");
        }

        Booking booking = new Booking();
        booking.setCustomerID(currentUser);
        booking.setService(service);
        booking.setTimeSlot(timeSlot);
        booking.setStatus("PENDING");
        booking.setBookingDate(LocalDateTime.now());
        booking.setDescription(bookingRequestDTO.getDescription());
        booking.setIsDeleted(false);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        // Increment booked count in time slot
        timeSlot.incrementBookedCount();
        timeSlotRepository.save(timeSlot);

        Booking savedBooking = bookingRepository.save(booking);
        
        // Send real-time notification for new booking
        bookingTrackingService.notifyNewBooking(savedBooking);
        
        return convertToDto(savedBooking);
    }

    @Override
    public List<BookingResponseDTO> getUserBookings() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(userName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + userName);
        }
        return bookingRepository.findByCustomerID(currentUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingResponseDTO createBookingForUser(ConsultantCreateBookingRequestDTO bookingRequestDTO) {
        // Verify that current user is a consultant
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentConsultant = userRepository.findUserByUsername(currentPrincipalName);
        if(currentConsultant == null) {
            throw new ServiceNotFoundException("Consultant not found: " + currentPrincipalName);
        }
        
        // Verify consultant role
        if (!currentConsultant.getRoleName().equals("ROLE_CONSULTANT")) {
            throw new IllegalStateException("Only consultants can create bookings for users");
        }

        // Find the target user
        User targetUser = userRepository.findById(bookingRequestDTO.getUserId())
                .orElseThrow(() -> new ServiceNotFoundException("User not found with ID: " + bookingRequestDTO.getUserId()));

        // Verify user has CUSTOMER role
        if (!targetUser.getRoleName().equals("ROLE_CUSTOMER")) {
            throw new IllegalStateException("Can only create bookings for customers");
        }

        TestingService service = testingServiceRepository.findById(bookingRequestDTO.getServiceId())
                .orElseThrow(() -> new ServiceNotFoundException("TestingService not found with ID: " + bookingRequestDTO.getServiceId()));

        TimeSlot timeSlot = timeSlotRepository.findById(bookingRequestDTO.getTimeSlotId())
                .orElseThrow(() -> new ServiceNotFoundException("TimeSlot not found with ID: " + bookingRequestDTO.getTimeSlotId()));

        // Validate time slot is available
        if (!timeSlot.isSlotAvailable()) {
            throw new IllegalStateException("This time slot is not available for booking.");
        }

        // Check if user already has a booking for this time slot
        if (bookingRepository.existsByCustomerIDAndTimeSlotAndStatusNot(targetUser, timeSlot, "CANCELLED")) {
            throw new BookingConflictException("Người dùng đã đặt lịch cho khung giờ này.");
        }

        Booking booking = new Booking();
        booking.setCustomerID(targetUser);
        booking.setService(service);
        booking.setTimeSlot(timeSlot);
        booking.setStatus("PENDING");
        booking.setBookingDate(bookingRequestDTO.getBookingDate());
        booking.setDescription(bookingRequestDTO.getDescription());
        booking.setIsDeleted(false);
        booking.setCreatedAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        
        // Increment booked count in time slot
        timeSlot.incrementBookedCount();
        timeSlotRepository.save(timeSlot);

        Booking savedBooking = bookingRepository.save(booking);
        
        // Send real-time notification for new booking
        bookingTrackingService.notifyNewBooking(savedBooking);
        
        return convertToDto(savedBooking);
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

        String previousStatus = booking.getStatus();
        String newStatus = statusRequestDTO.getStatus();

        booking.setUpdatedAt(LocalDateTime.now());
        booking.setStatus(newStatus);

        // Update description
        if (statusRequestDTO.getDescription() != null) {
            booking.setDescription(statusRequestDTO.getDescription());
        }

        // Update result date
        if (statusRequestDTO.getResultDate() != null) {
            booking.setResultDate(statusRequestDTO.getResultDate());
        }

        Booking updatedBooking = bookingRepository.save(booking);

        // Send real-time notification for status change
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = authentication != null ? authentication.getName() : "System";
        bookingTrackingService.notifyBookingStatusChange(updatedBooking, newStatus, previousStatus, updatedBy);

        return convertToDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO updateTestResult(Integer bookingId, UpdateTestResultRequestDTO resultRequest) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId));



        String previousStatus = booking.getStatus();

        // Update booking with test result
        booking.setResult(resultRequest.getResult());
        booking.setResultDate(resultRequest.getResultDate());
        booking.setStatus("COMPLETED"); // Automatically set to completed when result is updated
        booking.setUpdatedAt(LocalDateTime.now());

        // Add notes if provided
        if (resultRequest.getNotes() != null) {
            String existingDescription = booking.getDescription() != null ? booking.getDescription() : "";
            String newDescription = existingDescription.isEmpty()
                ? "Kết quả: " + resultRequest.getNotes()
                : existingDescription + "\nKết quả: " + resultRequest.getNotes();
            booking.setDescription(newDescription);
        }

        Booking updatedBooking = bookingRepository.save(booking);

        // Send real-time notification for result update
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String updatedBy = authentication != null ? authentication.getName() : "Lab System";

        // Notify about status change to COMPLETED
        bookingTrackingService.notifyBookingStatusChange(updatedBooking, "COMPLETED", previousStatus, updatedBy);

        // Also notify specifically about test result being ready
        bookingTrackingService.notifyTestResultReady(bookingId, resultRequest.getResult());

        return convertToDto(updatedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO cancelBooking(Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }
        
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ServiceNotFoundException("Booking not found with ID: " + bookingId));

        boolean isAdmin = currentUser.getAuthorities().stream()
                            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (!booking.getCustomerID().equals(currentUser) && !isAdmin) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to cancel this booking.");
        }

        if (!booking.canBeCancelled()) {
            throw new IllegalStateException("Booking cannot be cancelled in current status: " + booking.getStatus());
        }

        String previousStatus = booking.getStatus();
        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());
        // Decrement booked count in time slot
        TimeSlot timeSlot = booking.getTimeSlot();
        timeSlot.decrementBookedCount();
        timeSlotRepository.save(timeSlot);
        
        Booking cancelledBooking = bookingRepository.save(booking);
        
        // Send real-time notification for cancellation
        bookingTrackingService.notifyBookingStatusChange(cancelledBooking, "CANCELLED", previousStatus, currentPrincipalName);
        
        return convertToDto(cancelledBooking);
    }

    @Override
    @Transactional
    public BookingResponseDTO confirmBooking(Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }
        
        Booking booking = bookingRepository.findByIdAndCustomerIdAndIsDeletedFalse(bookingId, currentUser.getId())
                .orElseThrow(() -> new ServiceNotFoundException("Không tìm thấy booking hoặc bạn không có quyền xác nhận booking này"));

        if (!booking.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Chỉ có thể xác nhận booking khi trạng thái là PENDING");
        }

        String previousStatus = booking.getStatus();
        booking.setStatus("CONFIRMED");
        booking.setUpdatedAt(LocalDateTime.now());
        
        Booking confirmedBooking = bookingRepository.save(booking);
        
        // Send real-time notification for confirmation
        bookingTrackingService.notifyBookingStatusChange(confirmedBooking, "CONFIRMED", previousStatus, currentPrincipalName);
        
        return convertToDto(confirmedBooking);
    }

    @Override
    @Transactional
    public ResponseEntity<?> cancelBookingWithResponse(Integer bookingId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        User currentUser = userRepository.findUserByUsername(currentPrincipalName);
        if(currentUser == null) {
            throw new ServiceNotFoundException("User not found: " + currentPrincipalName);
        }
        
        Booking booking = bookingRepository.findByIdAndCustomerIdAndIsDeletedFalse(bookingId, currentUser.getId())
                .orElseThrow(() -> new ServiceNotFoundException("Không tìm thấy booking hoặc bạn không có quyền hủy booking này"));

        if (!booking.getStatus().equals("PENDING")) {
            throw new IllegalStateException("Chỉ có thể hủy booking khi trạng thái là PENDING");
        }

        String previousStatus = booking.getStatus();
        booking.setStatus("CANCELLED");
        booking.setUpdatedAt(LocalDateTime.now());
        booking.setIsDeleted(true);
        bookingRepository.save(booking);

        // Giảm bookedCount của TimeSlot
        TimeSlot timeSlot = booking.getTimeSlot();
        timeSlot.setBookedCount(timeSlot.getBookedCount() - 1);
        if (timeSlot.getBookedCount() < timeSlot.getCapacity()) {
            timeSlot.setIsAvailable(true);
        }
        timeSlotRepository.save(timeSlot);
        
        // Send real-time notification for cancellation
        bookingTrackingService.notifyBookingStatusChange(booking, "CANCELLED", previousStatus, currentPrincipalName);
        
        return new ResponseEntity<>("hủy đặt lịch thành công", HttpStatus.OK);
    }

    // New pagination methods
    @Override
    public BookingPageResponseDTO getAllBookingsForStaff(Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findAllActive(pageable);
        return createBookingPageResponse(bookingsPage);
    }

    @Override
    public BookingPageResponseDTO getBookingsWithFilters(BookingFilterRequestDTO filter, Pageable pageable) {
        // Create sort based on filter
        Sort sort = createSort(filter.getSortBy(), filter.getSortDirection());
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        
        Page<Booking> bookingsPage = bookingRepository.findByFilters(
            filter.getStatus(),
            filter.getCustomerId(),
            filter.getServiceId(),
            filter.getFromDate(),
            filter.getToDate(),
            filter.getCustomerName(),
            filter.getServiceName(),
            pageableWithSort
        );
        
        return createBookingPageResponse(bookingsPage);
    }

    @Override
    public BookingPageResponseDTO getBookingsByStatus(String status, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByStatus(status, pageable);
        return createBookingPageResponse(bookingsPage);
    }

    @Override
    public BookingPageResponseDTO getBookingsByCustomer(Integer customerId, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByCustomerId(customerId, pageable);
        return createBookingPageResponse(bookingsPage);
    }

    @Override
    public BookingPageResponseDTO getBookingsByService(Integer serviceId, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByServiceId(serviceId, pageable);
        return createBookingPageResponse(bookingsPage);
    }

    @Override
    public BookingPageResponseDTO getBookingsByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findByDateRange(fromDate, toDate, pageable);
        return createBookingPageResponse(bookingsPage);
    }

    // Statistics methods
    @Override
    public long getTotalBookings() {
        return bookingRepository.countAllActive();
    }

    @Override
    public long getBookingsByStatus(String status) {
        return bookingRepository.countByStatusActive(status);
    }

    @Override
    public long getBookingsByCustomer(Integer customerId) {
        return bookingRepository.countByCustomerId(customerId);
    }

    @Override
    public long getBookingsByService(Integer serviceId) {
        return bookingRepository.countByServiceIdActive(serviceId);
    }

    @Override
    public long getBookingsByDateRange(LocalDate fromDate, LocalDate toDate) {
        return bookingRepository.countByDateRange(fromDate, toDate);
    }

    // Legacy method for backward compatibility
    @Override
    public Page<BookingResponseDTO> getAllBookingsForStaffLegacy(Pageable pageable) {
        Page<Booking> bookingsPage = bookingRepository.findAll(pageable);
        return bookingsPage.map(this::convertToDto);
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

    // Helper methods
    private BookingPageResponseDTO createBookingPageResponse(Page<Booking> bookingsPage) {
        List<BookingResponseDTO> bookingDtos = bookingsPage.getContent().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        BookingPageResponseDTO response = new BookingPageResponseDTO();
        response.setContent(bookingDtos);
        response.setPageNumber(bookingsPage.getNumber() + 1);
        response.setPageSize(bookingsPage.getSize());
        response.setTotalElements(bookingsPage.getTotalElements());
        response.setTotalPages(bookingsPage.getTotalPages());
        response.setHasNext(bookingsPage.hasNext());
        response.setHasPrevious(bookingsPage.hasPrevious());
        
        // Add statistics
        response.setTotalBookings(getTotalBookings());
        response.setPendingBookings(getBookingsByStatus("PENDING"));
        response.setCompletedBookings(getBookingsByStatus("COMPLETED"));
        response.setCancelledBookings(getBookingsByStatus("CANCELLED"));
        
        return response;
    }

    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;
        
        switch (sortBy.toLowerCase()) {
            case "createdat":
                return Sort.by(direction, "createdAt");
            case "status":
                return Sort.by(direction, "status");
            case "customername":
                return Sort.by(direction, "customerID.fullName");
            case "servicename":
                return Sort.by(direction, "service.serviceName");
            case "slotdate":
                return Sort.by(direction, "timeSlot.slotDate");
            default:
                return Sort.by(direction, "createdAt");
        }
    }

    private BookingResponseDTO convertToDto(Booking booking) {
        BookingResponseDTO dto = new BookingResponseDTO();
        
        // Manual mapping để tránh ModelMapper errors
        dto.setBookingId(booking.getId());
        dto.setCustomerId(booking.getCustomerID().getId());
        dto.setCustomerFullName(booking.getCustomerID().getFullName());
        dto.setCustomerEmailAddress(booking.getCustomerID().getEmail());
        dto.setCustomerPhone(booking.getCustomerID().getPhoneNumber());
        dto.setServiceId(booking.getService().getId());
        dto.setServiceName(booking.getService().getServiceName());
        dto.setServiceDescription(booking.getService().getDescription());
        dto.setServicePrice(booking.getService().getPrice());
        dto.setStatus(booking.getStatus());
        dto.setResult(booking.getResult());
        dto.setResultDate(booking.getResultDate());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        dto.setDescription(booking.getDescription());
        dto.setBookingDate(booking.getBookingDate());
        
        // Time slot mapping with null check
        if (booking.getTimeSlot() != null) {
            dto.setTimeSlotId(booking.getTimeSlot().getTimeSlotID());
            dto.setSlotDate(booking.getTimeSlot().getSlotDate());
            dto.setStartTime(booking.getTimeSlot().getStartTime());
            dto.setEndTime(booking.getTimeSlot().getEndTime());
            dto.setSlotType(booking.getTimeSlot().getSlotType());
        }
        
        return dto;
    }


}
