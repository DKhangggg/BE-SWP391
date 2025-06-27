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
         List<Consultation> consultations = consultationRepository.findWithFilters(date, status, userId, consultantId);
         List<ConsultationBookingResponseDTO> responseDTOs = consultations.stream()
                .map(consultation -> modelMapper.map(consultation, ConsultationBookingResponseDTO.class))
                .toList();
         return responseDTOs;
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
                .map(consultation -> modelMapper.map(consultation, ConsultationBookingResponseDTO.class))
                .toList();
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
        List<Consultation> consultations = consultationRepository.findConsultationsByConsultantAndStatus_Completed(currentUser);
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
        return null;
    }

    @Override
    public ConsultationBookingResponseDTO cancelConsultation(Integer consultationId) {
        return null;
    }

    @Override
    public List<ConsultationBookingResponseDTO> getUserUpcomingConsultations() {
        return List.of();
    }
}
