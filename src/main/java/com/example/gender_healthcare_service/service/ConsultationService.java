package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.ConsultationBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.ConsultationStatusUpdateDTO;
import com.example.gender_healthcare_service.dto.request.RescheduleBookingRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateConsultationStatusRequestDTO;
import com.example.gender_healthcare_service.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface ConsultationService {

    void cancelConsultationBookingByAdmin(Integer bookingId, String adminNotes);
    ConsultationBookingResponseDTO rescheduleConsultationBookingByAdmin(
            Integer bookingId, RescheduleBookingRequestDTO rescheduleRequest);
    List<ConsultationBookingResponseDTO> getAllConsultationBookingsForAdmin(
            LocalDate date, String status, Integer userId, Integer consultantId);
    org.springframework.data.domain.Page<ConsultationBookingResponseDTO> getAllConsultationBookingsForAdminPaginated(
            LocalDate date, String status, Integer userId, Integer consultantId, org.springframework.data.domain.Pageable pageable);
    List<ConsultantAvailabilityResponseDTO> getConsultantAvailability(Integer consultantId, LocalDate date);
    List<ConsultationBookingResponseDTO> getConsultationBookingsForCurrentConsultant();
    ConsultationBookingResponseDTO getConsultationBookingByIdForAdmin(Integer bookingId);
    ConsultationBookingResponseDTO bookConsultation(ConsultationBookingRequestDTO bookingRequest);
    void updateConsultationStatus(Integer id, String status);
     List<ConsultationHistoryDTO> getConsultationHistoryForCurrentConsultant();
    UserResponseDTO getPatientInfoForConsultation(Integer id);
    List<ConsultationBookingResponseDTO> getUserConsultations(String status);

    List<ConsultationBookingResponseDTO> getConsultantBookings(LocalDate date, String status);

    ConsultationBookingResponseDTO updateConsultationStatus(Integer consultationId, ConsultationStatusUpdateDTO statusUpdateDTO);

    ConsultationDetailResponseDTO getConsultationDetails(Integer consultationId);

    ConsultationBookingResponseDTO cancelConsultation(Integer consultationId);

    List<ConsultationBookingResponseDTO> getUserUpcomingConsultations();

    List<ConsultationBookingResponseDTO> getConsultationBookingsForCurrentConsultant(String date, String status);
}
