package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.dto.request.AddUnavailabilityRequestDTO;
import com.example.gender_healthcare_service.dto.response.ConsultantScheduleResponseDTO;

import java.time.LocalDate;

public interface ConsultantScheduleService {
    void createDefaultScheduleForConsultant(Consultant consultant, LocalDate startDate, int numberOfWeeks);
    ConsultantScheduleResponseDTO getConsultantSchedule(Integer consultantId);
    void addUnavailability(Integer consultantId, AddUnavailabilityRequestDTO request);
}
