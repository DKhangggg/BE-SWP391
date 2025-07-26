package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultationBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationStatusUpdateDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationConfirmationDTO;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.dto.response.LocationResponseDTO;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.ConsultationService;
import com.example.gender_healthcare_service.repository.ConsultationRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import com.example.gender_healthcare_service.dto.request.ReminderRequestDTO;
import com.example.gender_healthcare_service.service.ReminderService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.example.gender_healthcare_service.entity.TimeSlot;
import com.example.gender_healthcare_service.repository.TimeSlotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    private static final Logger log = LoggerFactory.getLogger(ConsultationServiceImpl.class);

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ReminderService reminderService;


    @Override
    public List<ConsultationBookingResponseDTO> getAllConsultationBookingsForAdmin(
            LocalDate date, String status, Integer userId, Integer consultantId) {
         ConsultationStatus consultationStatus = status != null ? ConsultationStatus.valueOf(status.toUpperCase()) : null;
         List<Consultation> consultations = consultationRepository.findWithFilters(date, consultationStatus, userId, consultantId);
         List<ConsultationBookingResponseDTO> responseDTOs = consultations.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationBookingResponseDTO.class))
                .toList();
         return responseDTOs;
    }

    @Override
    public org.springframework.data.domain.Page<ConsultationBookingResponseDTO> getAllConsultationBookingsForAdminPaginated(
            LocalDate date, String status, Integer userId, Integer consultantId, org.springframework.data.domain.Pageable pageable) {
         ConsultationStatus consultationStatus = status != null ? ConsultationStatus.valueOf(status.toUpperCase()) : null;
         org.springframework.data.domain.Page<Consultation> consultations = consultationRepository.findWithFiltersPaginated(date, consultationStatus, userId, consultantId, pageable);
         return consultations.map(consultation -> modelMapper.map(consultation, ConsultationBookingResponseDTO.class));
    }



    @Override
    public void cancelConsultationBookingByAdmin(Integer bookingId, String adminNotes) {
        Consultation consultation = consultationRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + bookingId));
        consultation.setStatus(ConsultationStatus.CANCELLED);
        consultationRepository.save(consultation);
        System.out.println("Cancelling consultation booking by admin for ID: " + bookingId + " with notes: " + adminNotes);
    }

    @Override
    public ConsultationBookingResponseDTO rescheduleConsultationBookingByAdmin(
            Integer bookingId, RescheduleBookingRequestDTO rescheduleRequest) {
        Consultation consultation = consultationRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + bookingId));

        // consultation.setConsultationDate(rescheduleRequest.getNewBookingDate().atStartOfDay().toInstant(java.time.ZoneOffset.UTC)); // Example conversion
        // consultation.setTimeSlotId(rescheduleRequest.getNewTimeSlotId());

        if (rescheduleRequest.getAdminNotes() != null && !rescheduleRequest.getAdminNotes().isEmpty()) {
            // consultation.setAdminNotes(rescheduleRequest.getAdminNotes());
            consultation.setNotes( (consultation.getNotes() == null ? "" : consultation.getNotes() + "\n") + "Admin Reschedule: " + rescheduleRequest.getAdminNotes());
        }
        // Add logic to update date/time here based on your entity structure
        System.out.println("Rescheduling consultation booking by admin for ID: " + bookingId + " to date: " + rescheduleRequest.getNewBookingDate());
        // Consultation updatedConsultation = consultationRepository.save(consultation);
        // return modelMapper.map(updatedConsultation, ConsultationBookingResponseDTO.class);
        return new ConsultationBookingResponseDTO(); // Return dummy/actual DTO after implementing reschedule logic
    }
    @Override
    public ConsultationBookingResponseDTO getConsultationBookingByIdForAdmin(Integer bookingId) {
        Consultation consultation = consultationRepository.findConsultationById(bookingId);
        if (consultation == null) {
                throw  new RuntimeException("Consultation booking not found: " + bookingId);
        }
        return modelMapper.map(consultation, ConsultationBookingResponseDTO.class);
    }

    @Override
    public List<ConsultationBookingResponseDTO> getConsultationBookingsForCurrentConsultant() {

        String stringUser = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(stringUser);
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultant(currentUser);
        return consultations.stream()
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    private ConsultationBookingResponseDTO mapToConsultationBookingResponseDTO(Consultation consultation) {
        ConsultationBookingResponseDTO dto = new ConsultationBookingResponseDTO();
        dto.setId(consultation.getId());
        
        // Consultant info
        if (consultation.getConsultant() != null) {
            dto.setConsultantId(consultation.getConsultant().getId());
            dto.setConsultantName(consultation.getConsultant().getFullName());
        }
        
        // Customer info
        if (consultation.getCustomer() != null) {
            dto.setUserId(consultation.getCustomer().getId());
            dto.setUserName(consultation.getCustomer().getFullName());
        }
        
        // Time slot info - convert LocalTime to LocalDateTime
        if (consultation.getTimeSlot() != null) {
            LocalDateTime startDateTime = consultation.getTimeSlot().getSlotDate().atTime(consultation.getTimeSlot().getStartTime());
            dto.setStartTime(startDateTime);
        }
        
        dto.setStatus(consultation.getStatus() != null ? consultation.getStatus().name() : null);
        dto.setNotes(consultation.getNotes());
        dto.setCreatedAt(consultation.getCreatedAt());
        
        return dto;
    }

    @Override
    public void updateConsultationStatus(Integer id, String status) {
    Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + id));
        try {
            ConsultationStatus newStatus = ConsultationStatus.valueOf(status.toUpperCase());
            consultation.setStatus(newStatus);
            consultationRepository.save(consultation);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + status, e);
        }
    }

    @Override
    public List<ConsultationHistoryDTO> getConsultationHistoryForCurrentConsultant() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultantAndStatus(currentUser, ConsultationStatus.COMPLETED);
        return consultations.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationHistoryDTO.class))
                .toList();
    }

    @Override
    public UserResponseDTO getPatientInfoForConsultation(Integer id) {
    Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + id));
    User patient = consultation.getCustomer();
        return modelMapper.map(patient, UserResponseDTO.class);
    }

    @Override
    public List<ConsultantAvailabilityResponseDTO> getConsultantAvailability(Integer consultantId, LocalDate date) {
        log.info("Getting availability for consultant {} on date {}", consultantId, date);
        
        // Lấy thông tin consultant
        User consultant = userRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found: " + consultantId));
        
        // Lấy tất cả timeslot trống cho ngày đó (chưa có consultantID hoặc chưa được book)
        List<TimeSlot> availableTimeSlots = timeSlotRepository.findAvailableTimeSlotsByDate(date);
        
        // Lọc ra các timeslot có thể book cho consultant này
        List<ConsultantAvailabilityResponseDTO> result = new ArrayList<>();
        
        for (TimeSlot timeSlot : availableTimeSlots) {
            // Chỉ hiển thị timeslot CONSULTATION
            if (!"CONSULTATION".equals(timeSlot.getSlotType())) {
                continue;
            }
            
            // Kiểm tra xem consultant này đã có booking trong timeslot này chưa
            boolean consultantHasBooking = consultationRepository.existsByConsultantIdAndTimeSlotAndDate(
                consultantId, timeSlot.getTimeSlotID(), date);
            
            // Nếu consultant chưa có booking thì hiển thị timeslot này
            if (!consultantHasBooking) {
                ConsultantAvailabilityResponseDTO dto = new ConsultantAvailabilityResponseDTO();
                dto.setSlotId(timeSlot.getTimeSlotID());
                dto.setConsultantId(consultantId);
                dto.setConsultantName(consultant.getFullName());
                dto.setStartTime(date.atTime(timeSlot.getStartTime()));
                dto.setEndTime(date.atTime(timeSlot.getEndTime()));
                dto.setStartTimeStr(timeSlot.getStartTime().toString());
                dto.setEndTimeStr(timeSlot.getEndTime().toString());
                dto.setIsAvailable(true);
                
                result.add(dto);
            }
        }
        
        log.info("Found {} available time slots for consultant {} on date {}", result.size(), consultantId, date);
        return result;
    }

    @Override
    public ConsultationBookingResponseDTO bookConsultation(ConsultationBookingRequestDTO bookingRequest) {
        // Lấy thông tin user hiện tại
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findUserByUsername(currentUsername);
        
        // Lấy thông tin consultant
        User consultant = userRepository.findById(bookingRequest.getConsultantId())
                .orElseThrow(() -> new RuntimeException("Consultant not found: " + bookingRequest.getConsultantId()));
        
        // Kiểm tra xem user có phải là consultant không
        if (!"ROLE_CONSULTANT".equals(consultant.getRoleName()) && !"ROLE_ADMIN".equals(consultant.getRoleName())) {
            throw new RuntimeException("User is not a consultant: " + consultant.getFullName());
        }
        
        // Tìm time slot phù hợp với start time và end time
        List<TimeSlot> availableTimeSlots = timeSlotRepository.findAvailableTimeSlotsByDate(bookingRequest.getStartTime().toLocalDate());
        log.info("Found {} available time slots for date {}", availableTimeSlots.size(), bookingRequest.getStartTime().toLocalDate());
        
        // Debug: Log thông tin về các time slot có sẵn
        availableTimeSlots.forEach(ts -> {
            log.debug("Available time slot: ID={}, Date={}, StartTime={}, EndTime={}, SlotType={}, Consultant={}", 
                     ts.getTimeSlotID(), ts.getSlotDate(), ts.getStartTime(), ts.getEndTime(), 
                     ts.getSlotType(), ts.getConsultant() != null ? ts.getConsultant().getId() : "null");
        });
        
        TimeSlot timeSlot = availableTimeSlots.stream()
                .filter(ts -> ts.getStartTime().equals(bookingRequest.getStartTime().toLocalTime()) && 
                              ts.getEndTime().equals(bookingRequest.getEndTime().toLocalTime()) &&
                              "CONSULTATION".equals(ts.getSlotType()))
                .findFirst()
                .orElseThrow(() -> {
                    log.error("No available time slot found for date: {}, startTime: {}, endTime: {}, slotType: CONSULTATION", 
                             bookingRequest.getStartTime().toLocalDate(), 
                             bookingRequest.getStartTime().toLocalTime(), 
                             bookingRequest.getEndTime().toLocalTime());
                    return new RuntimeException("No available time slot found for the requested time");
                });
        
        // Kiểm tra time slot có available không
        if (!timeSlot.isAvailable()) {
            throw new RuntimeException("Time slot is not available");
        }
        
        // Kiểm tra xem customer đã có booking trong time slot này chưa
        boolean customerHasBooking = consultationRepository.findConsultationsByCustomer(customer).stream()
                .anyMatch(c -> c.getTimeSlot().getTimeSlotID().equals(timeSlot.getTimeSlotID()) && 
                              c.getTimeSlot().getSlotDate().equals(timeSlot.getSlotDate()) &&
                              !c.getStatus().equals(ConsultationStatus.CANCELLED));
        
        if (customerHasBooking) {
            throw new RuntimeException("You already have a booking for this time slot");
        }
        
        // Kiểm tra xem consultant đã có booking trong time slot này chưa
        boolean consultantHasBooking = consultationRepository.existsByConsultantIdAndTimeSlotAndDate(
            bookingRequest.getConsultantId(), timeSlot.getTimeSlotID(), bookingRequest.getStartTime().toLocalDate());
        
        if (consultantHasBooking) {
            throw new RuntimeException("Consultant already has a booking for this time slot");
        }
        
        // Assign consultant vào time slot nếu chưa có
        if (timeSlot.getConsultant() == null) {
            if (consultant.getConsultant() != null) {
                timeSlot.setConsultant(consultant.getConsultant());
                timeSlotRepository.save(timeSlot);
            } else {
                throw new RuntimeException("Consultant profile not found for user: " + consultant.getFullName());
            }
        }
        
        // Tạo consultation mới
        Consultation consultation = new Consultation();
        consultation.setCustomer(customer);
        consultation.setConsultant(consultant);
        consultation.setTimeSlot(timeSlot);
        consultation.setStatus(ConsultationStatus.SCHEDULED);
        consultation.setNotes(bookingRequest.getNotes());
        consultation.setIsDeleted(false);
        
        // Lưu consultation
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        // Cập nhật booked count của time slot
        timeSlot.incrementBookedCount();
        timeSlotRepository.save(timeSlot);
        
        log.info("Consultation booked: ID={}, Customer={}, Consultant={}, TimeSlot={}", 
                savedConsultation.getId(), customer.getFullName(), consultant.getFullName(), timeSlot.getTimeSlotID());
        
        return mapToConsultationBookingResponseDTO(savedConsultation);
    }

    @Override
    public List<ConsultationBookingResponseDTO> getUserConsultations(String status) {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(currentUsername);
        
        List<Consultation> consultations = consultationRepository.findConsultationsByCustomer(currentUser);
        
        return consultations.stream()
                .filter(c -> {
                    if (status != null && !status.isEmpty() && c.getStatus() != null) {
                        return c.getStatus().name().equalsIgnoreCase(status);
                    }
                    return true;
                })
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    @Override
    public List<ConsultationBookingResponseDTO> getConsultantBookings(LocalDate date, String status) {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(currentUsername);
        
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultant(currentUser);
        
        return consultations.stream()
                .filter(c -> {
                    boolean match = true;
                    
                    // Filter by date
                    if (date != null && c.getTimeSlot() != null && c.getTimeSlot().getSlotDate() != null) {
                        match = match && c.getTimeSlot().getSlotDate().equals(date);
                    }
                    
                    // Filter by status
                    if (status != null && !status.isEmpty() && c.getStatus() != null) {
                        match = match && c.getStatus().name().equalsIgnoreCase(status);
                    }
                    
                    return match;
                })
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    @Override
    public ConsultationBookingResponseDTO updateConsultationStatus(Integer consultationId, ConsultationStatusUpdateDTO statusUpdateDTO) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found: " + consultationId));
        
        // Kiểm tra quyền - chỉ consultant của lịch hẹn hoặc admin mới được update
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(currentUsername);
        
        if (!consultation.getConsultant().getId().equals(currentUser.getId()) && 
            !currentUser.getRoleName().equals("ROLE_ADMIN")) {
            throw new RuntimeException("You can only update your own consultations");
        }
        
        // Cập nhật status
        ConsultationStatus newStatus = ConsultationStatus.valueOf(statusUpdateDTO.getStatus().toUpperCase());
        consultation.setStatus(newStatus);
        
        // Cập nhật notes nếu có
        if (statusUpdateDTO.getNotes() != null && !statusUpdateDTO.getNotes().isEmpty()) {
            String currentNotes = consultation.getNotes() != null ? consultation.getNotes() : "";
            consultation.setNotes(currentNotes + "\nStatus Update: " + statusUpdateDTO.getNotes());
        }
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        log.info("Consultation status updated: ID={}, NewStatus={}, UpdatedBy={}", 
                consultationId, newStatus, currentUser.getFullName());
        
        return mapToConsultationBookingResponseDTO(savedConsultation);
    }

    @Override
    public ConsultationBookingResponseDTO confirmConsultation(Integer consultationId, ConsultationConfirmationDTO confirmationDTO) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found: " + consultationId));
        
        // Update status
        consultation.setStatus(ConsultationStatus.valueOf(confirmationDTO.getStatus().toUpperCase()));
        
        // Update meeting information if confirmed
        if ("CONFIRMED".equalsIgnoreCase(confirmationDTO.getStatus())) {
            consultation.setMeetingLink(confirmationDTO.getMeetingLink());
            
            // Build meeting info string
            StringBuilder meetingInfo = new StringBuilder();
            if (confirmationDTO.getMeetingPlatform() != null) {
                meetingInfo.append("Platform: ").append(confirmationDTO.getMeetingPlatform());
            }
            if (confirmationDTO.getMeetingPassword() != null && !confirmationDTO.getMeetingPassword().isEmpty()) {
                meetingInfo.append("\nPassword: ").append(confirmationDTO.getMeetingPassword());
            }
            
            // Add meeting info to notes
            String currentNotes = consultation.getNotes() != null ? consultation.getNotes() : "";
            if (meetingInfo.length() > 0) {
                currentNotes += "\nMeeting Info:\n" + meetingInfo.toString();
            }
            consultation.setNotes(currentNotes);
        }
        
        // Update consultant notes
        if (confirmationDTO.getNotes() != null && !confirmationDTO.getNotes().isEmpty()) {
            String currentNotes = consultation.getNotes() != null ? consultation.getNotes() : "";
            consultation.setNotes(currentNotes + "\nConsultant Notes: " + confirmationDTO.getNotes());
        }
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        // Tạo reminder nếu consultation được xác nhận
        if ("CONFIRMED".equalsIgnoreCase(confirmationDTO.getStatus())) {
            createConsultationReminder(savedConsultation);
        }
        
        return modelMapper.map(consultation, ConsultationBookingResponseDTO.class);
    }

    @Override
    public ConsultationDetailResponseDTO getConsultationDetails(Integer consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + consultationId));
        return mapToConsultationDetailResponseDTO(consultation);
    }


    @Override
    public ConsultationBookingResponseDTO cancelConsultation(Integer consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found: " + consultationId));
        
        // Kiểm tra quyền - customer hoặc consultant của lịch hẹn mới được cancel
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(currentUsername);
        
        if (!consultation.getCustomer().getId().equals(currentUser.getId()) && 
            !consultation.getConsultant().getId().equals(currentUser.getId()) &&
            !currentUser.getRoleName().equals("ROLE_ADMIN")) {
            throw new RuntimeException("You can only cancel your own consultations");
        }
        
        // Kiểm tra trạng thái hiện tại
        if (consultation.getStatus() == ConsultationStatus.CANCELLED) {
            throw new RuntimeException("Consultation is already cancelled");
        }
        
        if (consultation.getStatus() == ConsultationStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed consultation");
        }
        
        // Cập nhật status
        consultation.setStatus(ConsultationStatus.CANCELLED);
        
        // Giảm booked count của time slot
        TimeSlot timeSlot = consultation.getTimeSlot();
        if (timeSlot != null) {
            timeSlot.decrementBookedCount();
            timeSlotRepository.save(timeSlot);
        }
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        log.info("Consultation cancelled: ID={}, CancelledBy={}", consultationId, currentUser.getFullName());
        
        return mapToConsultationBookingResponseDTO(savedConsultation);
    }

    @Override
    public List<ConsultationBookingResponseDTO> getUserUpcomingConsultations() {
        String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(currentUsername);
        
        List<Consultation> consultations = consultationRepository.findConsultationsByCustomer(currentUser);
        
        return consultations.stream()
                .filter(c -> {
                    // Chỉ lấy lịch hẹn trong tương lai và chưa hoàn thành
                    if (c.getTimeSlot() != null && c.getTimeSlot().getSlotDate() != null) {
                        LocalDate today = LocalDate.now();
                        LocalDate consultationDate = c.getTimeSlot().getSlotDate();
                        
                        return consultationDate.isAfter(today) || consultationDate.equals(today);
                    }
                    return false;
                })
                .filter(c -> c.getStatus() != ConsultationStatus.CANCELLED && 
                            c.getStatus() != ConsultationStatus.COMPLETED)
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    private ConsultationDetailResponseDTO mapToConsultationDetailResponseDTO(Consultation consultation) {
        ConsultationDetailResponseDTO dto = new ConsultationDetailResponseDTO();
        dto.setId(consultation.getId());
        // Consultant info
        if (consultation.getConsultant() != null) {
            dto.setConsultantId(consultation.getConsultant().getId());
            dto.setConsultantName(consultation.getConsultant().getFullName());
            // Nếu có chuyên môn, avatar thì lấy từ entity Consultant
            if (consultation.getConsultant().getConsultant() != null) {
                dto.setConsultantSpecialty(consultation.getConsultant().getConsultant().getSpecialization());
                // dto.setConsultantImageUrl(consultation.getConsultant().getConsultant().getImageUrl()); // nếu có
            }
        }
        // Customer info
        if (consultation.getCustomer() != null) {
            dto.setUserId(consultation.getCustomer().getId());
            dto.setUserName(consultation.getCustomer().getFullName());
            dto.setUserEmail(consultation.getCustomer().getEmail());
        }
        // Mapping location
        if (consultation.getLocation() != null) {
            LocationResponseDTO locationDTO = new LocationResponseDTO();
            locationDTO.setId(consultation.getLocation().getId());
            locationDTO.setName(consultation.getLocation().getName());
            locationDTO.setAddress(consultation.getLocation().getAddress());
            locationDTO.setPhone(consultation.getLocation().getPhone());
            locationDTO.setHours(consultation.getLocation().getHours());
            locationDTO.setStatus(consultation.getLocation().getStatus());
            dto.setLocation(locationDTO);
        } else {
            dto.setLocation(null);
        }
        // Mapping timeslot
        if (consultation.getTimeSlot() != null) {
            TimeSlotResponseDTO slotDTO = new TimeSlotResponseDTO();
            slotDTO.setTimeSlotId(consultation.getTimeSlot().getTimeSlotID());
            slotDTO.setSlotDate(consultation.getTimeSlot().getSlotDate());
            slotDTO.setSlotType(consultation.getTimeSlot().getSlotType());
            slotDTO.setDuration(consultation.getTimeSlot().getDuration());
            slotDTO.setDescription(consultation.getTimeSlot().getDescription());
            slotDTO.setIsActive(consultation.getTimeSlot().getIsAvailable());
            // Consultant info in slot
            if (consultation.getTimeSlot().getConsultant() != null) {
                slotDTO.setConsultantId(consultation.getTimeSlot().getConsultant().getId());
                slotDTO.setConsultantName(consultation.getTimeSlot().getConsultant().getUser().getFullName());
                slotDTO.setConsultantSpecialization(consultation.getTimeSlot().getConsultant().getSpecialization());
            }
            slotDTO.setCapacity(consultation.getTimeSlot().getCapacity());
            slotDTO.setBookedCount(consultation.getTimeSlot().getBookedCount());
            // slotDTO.setAvailableSlots(...); // Nếu có logic tính
            slotDTO.setDisplayInfo(consultation.getTimeSlot().getDisplayInfo());
            slotDTO.setIsAvailable(consultation.getTimeSlot().isAvailable());
            dto.setTimeSlot(slotDTO);
        } else {
            dto.setTimeSlot(null);
        }
        dto.setStatus(consultation.getStatus() != null ? consultation.getStatus().name() : null);
        dto.setCreatedAt(consultation.getCreatedAt());
        // Nếu có updatedAt thì set thêm
        // dto.setUpdatedAt(consultation.getUpdatedAt());
        dto.setNotes(consultation.getNotes());
        dto.setMeetingLink(consultation.getMeetingLink());
        // Nếu có consultationNotes thì map, nếu không thì trả về List.of()
        dto.setConsultationNotes(List.of());
        return dto;
    }

    @Override
    public List<ConsultationBookingResponseDTO> getConsultationBookingsForCurrentConsultant(String date, String status) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultant(currentUser);
        return consultations.stream()
                .filter(c -> {
                    boolean match = true;
                    if (date != null && !date.isEmpty() && c.getTimeSlot() != null && c.getTimeSlot().getSlotDate() != null) {
                        match = match && c.getTimeSlot().getSlotDate().toString().equals(date);
                    }
                    if (status != null && !status.isEmpty() && c.getStatus() != null) {
                        match = match && c.getStatus().name().equalsIgnoreCase(status);
                    }
                    return match;
                })
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    @Override
    public List<ConsultationBookingResponseDTO> getPendingAppointments() {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultantAndStatus(currentUser, ConsultationStatus.SCHEDULED);
        
        return consultations.stream()
                .filter(c -> c.getTimeSlot() != null && c.getTimeSlot().getSlotDate() != null && 
                           c.getTimeSlot().getSlotDate().isAfter(LocalDate.now().minusDays(1))) // Chỉ lấy lịch hẹn từ hôm qua trở đi
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    @Override
    public List<ConsultationBookingResponseDTO> getMyAppointments(String status, LocalDate date) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultant(currentUser);
        
        return consultations.stream()
                .filter(c -> {
                    boolean match = true;
                    
                    // Filter by status
                    if (status != null && !status.isEmpty() && c.getStatus() != null) {
                        match = match && c.getStatus().name().equalsIgnoreCase(status);
                    }
                    
                    // Filter by date
                    if (date != null && c.getTimeSlot() != null && c.getTimeSlot().getSlotDate() != null) {
                        match = match && c.getTimeSlot().getSlotDate().equals(date);
                    }
                    
                    return match;
                })
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    @Override
    public ConsultationBookingResponseDTO confirmWithMeetingLink(Integer consultationId) {
        String username = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        
        if (currentUser == null) {
            throw new RuntimeException("User not found: " + username);
        }
        
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation not found: " + consultationId));
        
        // Kiểm tra xem consultant hiện tại có phải là consultant của lịch hẹn này không
        if (!consultation.getConsultant().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only confirm your own consultations");
        }
        
        // Kiểm tra trạng thái hiện tại
        if (consultation.getStatus() != ConsultationStatus.SCHEDULED) {
            throw new RuntimeException("Can only confirm consultations with SCHEDULED status");
        }
        
        // Tạo link meeting (có thể sử dụng Google Meet, Zoom, hoặc link tùy chỉnh)
        String meetingLink = generateMeetingLink(consultation);
        
        // Cập nhật consultation
        consultation.setStatus(ConsultationStatus.CONFIRMED);
        consultation.setMeetingLink(meetingLink);
        consultation.setNotes((consultation.getNotes() == null ? "" : consultation.getNotes() + "\n") + 
                             "Confirmed by consultant with meeting link: " + meetingLink);
        
        Consultation savedConsultation = consultationRepository.save(consultation);
        
        // Tạo reminder cho user về lịch hẹn đã được xác nhận
        createConsultationReminder(savedConsultation);
        
        log.info("Consultation {} confirmed with meeting link: {}", consultationId, meetingLink);
        
        return mapToConsultationBookingResponseDTO(savedConsultation);
    }

    private String generateMeetingLink(Consultation consultation) {
        // Tạo meeting link tùy chỉnh dựa trên thông tin consultation
        String baseUrl = "https://meet.google.com/";
        String meetingCode = generateMeetingCode(consultation);
        return baseUrl + meetingCode;
    }

    private String generateMeetingCode(Consultation consultation) {
        // Tạo code meeting dựa trên ID consultation và thời gian
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(8); // Lấy 4 số cuối
        String consultationId = String.valueOf(consultation.getId());
        return "swp-" + consultationId + "-" + timestamp;
    }
    
    /**
     * Tạo reminder cho user về lịch hẹn consultation đã được xác nhận
     */
    private void createConsultationReminder(Consultation consultation) {
        try {
            // Tính thời gian reminder (30 phút trước lịch hẹn)
            LocalDateTime consultationTime = consultation.getTimeSlot().getSlotDate()
                    .atTime(consultation.getTimeSlot().getStartTime());
            LocalDateTime reminderTime = consultationTime.minusMinutes(30);
            
            // Chỉ tạo reminder nếu thời gian reminder chưa qua
            if (reminderTime.isAfter(LocalDateTime.now())) {
                ReminderRequestDTO reminderRequest = new ReminderRequestDTO();
                reminderRequest.setUserId(consultation.getCustomer().getId());
                reminderRequest.setReminderType("CONSULTATION_CONFIRMATION");
                reminderRequest.setReminderDate(reminderTime.toLocalDate());
                
                // Tạo message reminder
                String consultantName = consultation.getConsultant().getFullName();
                String meetingTime = consultationTime.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm"));
                String meetingDate = consultationTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                
                String message = String.format(
                    "Lịch hẹn tư vấn với %s đã được xác nhận. " +
                    "Thời gian: %s ngày %s. " +
                    "Meeting link: %s. " +
                    "Vui lòng tham gia đúng giờ!",
                    consultantName, meetingTime, meetingDate, consultation.getMeetingLink()
                );
                
                reminderRequest.setMessage(message);
                
                // Tạo reminder
                reminderService.createReminder(reminderRequest);
                
                log.info("Created consultation reminder for user {} at {}", 
                    consultation.getCustomer().getUsername(), reminderTime);
            } else {
                log.warn("Cannot create reminder for consultation {} - reminder time {} has passed", 
                    consultation.getId(), reminderTime);
            }
            
        } catch (Exception e) {
            log.error("Error creating consultation reminder for consultation {}: {}", 
                consultation.getId(), e.getMessage(), e);
            // Không throw exception để không ảnh hưởng đến việc xác nhận lịch hẹn
        }
    }
}
