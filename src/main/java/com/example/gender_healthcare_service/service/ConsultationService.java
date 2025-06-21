package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.Consultation;
import com.example.gender_healthcare_service.entity.enumpackage.ConsultationStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConsultationBookingResponseDTO;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ConsultationService {
    List<ConsultationBookingResponseDTO> getAllConsultationBookingsForAdmin(LocalDate date, String status, Integer userId, Integer consultantId);
    ConsultationBookingResponseDTO getConsultationBookingByIdForAdmin(Integer bookingId);
    ConsultationBookingResponseDTO updateConsultationBookingStatusByAdmin(Integer bookingId, UpdateConsultationStatusRequestDTO statusRequest);
    void cancelConsultationBookingByAdmin(Integer bookingId, String adminNotes);
    ConsultationBookingResponseDTO rescheduleConsultationBookingByAdmin(Integer bookingId, RescheduleBookingRequestDTO rescheduleRequest);
}
