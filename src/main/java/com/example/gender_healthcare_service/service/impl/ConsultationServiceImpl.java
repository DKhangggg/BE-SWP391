package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.service.ConsultationService;
import com.example.gender_healthcare_service.repository.ConsultationRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationBookingResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public ConsultationBookingResponseDTO updateConsultationBookingStatusByAdmin(
            Integer bookingId, UpdateConsultationStatusRequestDTO statusRequest) {
         Consultation consultation = consultationRepository.findConsultationById(bookingId);
         if (consultation == null) {
             throw new RuntimeException("Consultation booking not found: " + bookingId);
         }
        try {
            ConsultationStatus newStatus = ConsultationStatus.valueOf(statusRequest.getStatus().toUpperCase());
            consultation.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + statusRequest.getStatus(), e);
        }
        if (statusRequest.getAdminNotes() != null && !statusRequest.getAdminNotes().isEmpty()) {
             consultation.setNotes( (consultation.getNotes() == null ? "" : consultation.getNotes() + "\n") + "Admin: " + statusRequest.getAdminNotes());
        }
        Consultation updatedConsultation = consultationRepository.save(consultation);
        return modelMapper.map(updatedConsultation, ConsultationBookingResponseDTO.class);
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
}
