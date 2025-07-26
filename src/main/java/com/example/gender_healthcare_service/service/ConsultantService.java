package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.ConsultantUpdateDTO;
import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.request.UnavailabilityRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;

import java.util.List;
import java.util.Map;

public interface ConsultantService {

    ConsultantDTO getConsultantById(Integer id);
    ConsultantDTO getCurrentConsultant();
    List<ConsultantDTO> getAllConsultants();

    void PermanentlyDeleteConsultant(Integer id);
    boolean addUnavailability(UnavailabilityRequest unavailabilityRequest);
    List<ConsultantUnavailability> getUnavailabilityByDate(String date);
    Consultant createNewConsultant(CreateNewConsultantRequest request);
    void updateConsultant(ConsultantUpdateDTO consultantUpdateDTO);
    void deleteConsultant(Integer id);

    Consultant findConsultantByUserId(Integer userId); // Changed return type to Consultant entity

    // Dashboard APIs
    long getUnreadMessagesCount();
    Map<String, Object> getRevenue(String date, String month);
    Map<String, Object> getTotalRevenue();
    String uploadProfileImage(org.springframework.web.multipart.MultipartFile file, Integer consultantId);
}
