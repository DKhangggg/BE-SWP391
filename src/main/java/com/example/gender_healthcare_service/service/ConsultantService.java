package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Consultant;

import java.util.List;

public interface ConsultantService {

    ConsultantDTO getConsultantById(Integer id);

    List<ConsultantDTO> getAllConsultants();

    Consultant createNewConsultant(CreateNewConsultantRequest request);

    void updateConsultant(Integer id, ConsultantDTO consultantDTO);

    void deleteConsultant(Integer id);

    void PermanentlyDeleteConsultant(Integer id);

    Consultant findConsultantByUserId(Integer userId); // Changed return type to Consultant entity

}
