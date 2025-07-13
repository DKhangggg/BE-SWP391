package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultationBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationStatusUpdateDTO;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.ConsultationService;
import com.example.gender_healthcare_service.repository.ConsultationRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ConsultationServiceImpl implements ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;


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
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultant(currentUser);
        return consultations.stream()
                .map(this::mapToConsultationBookingResponseDTO)
                .toList();
    }

    private ConsultationBookingResponseDTO mapToConsultationBookingResponseDTO(Consultation consultation) {
        ConsultationBookingResponseDTO dto = new ConsultationBookingResponseDTO();
        dto.setId(consultation.getId());
        if (consultation.getConsultant() != null) {
            dto.setConsultantId(consultation.getConsultant().getId());
            dto.setConsultantName(consultation.getConsultant().getFullName());
        }
        if (consultation.getCustomer() != null) {
            dto.setUserId(consultation.getCustomer().getId());
            dto.setUserName(consultation.getCustomer().getFullName());
        }
        dto.setStatus(consultation.getStatus() != null ? consultation.getStatus().name() : null);
        dto.setNotes(consultation.getNotes());
        dto.setCreatedAt(consultation.getCreatedAt());
        // Nếu có các trường startTime, endTime, consultationType, updatedAt... thì map thêm ở đây
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
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
        return List.of();
    }

    @Override
    public ConsultationBookingResponseDTO bookConsultation(ConsultationBookingRequestDTO bookingRequest) {
        return null;
    }

    @Override
    public List<ConsultationBookingResponseDTO> getUserConsultations(String status) {
        return List.of();
    }

    @Override
    public List<ConsultationBookingResponseDTO> getConsultantBookings(LocalDate date, String status) {
        return List.of();
    }

    @Override
    public ConsultationBookingResponseDTO updateConsultationStatus(Integer consultationId, ConsultationStatusUpdateDTO statusUpdateDTO) {
        return null;
    }

    @Override
    public ConsultationDetailResponseDTO getConsultationDetails(Integer consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new RuntimeException("Consultation booking not found: " + consultationId));
        return mapToConsultationDetailResponseDTO(consultation);
    }


    @Override
    public ConsultationBookingResponseDTO cancelConsultation(Integer consultationId) {
        return null;
    }

    @Override
    public List<ConsultationBookingResponseDTO> getUserUpcomingConsultations() {
        return List.of();
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
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
}
