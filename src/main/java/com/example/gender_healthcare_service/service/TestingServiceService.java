package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.TestingServiceRequestDTO;
import com.example.gender_healthcare_service.dto.request.TestingServiceUpdateDTO;
import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.entity.TestingService;
import java.util.List;

public interface TestingServiceService {
    List<TestingService> getAllServices();
    TestingService getServiceById(Integer id);
    boolean createService(TestingService service);
    TestingServiceResponseDTO updateService(Integer id, TestingServiceUpdateDTO serviceDetails);
    void deleteService(Integer id,boolean isDeleted);
}

