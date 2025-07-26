package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.TestingServiceRequestDTO;
import com.example.gender_healthcare_service.dto.request.TestingServiceUpdateDTO;
import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.entity.TestingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface TestingServiceService {
    TestingService getServiceById(Integer id);
    boolean createService(TestingService service);
    TestingServiceResponseDTO updateService(Integer id, TestingServiceUpdateDTO serviceDetails);
    TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted);
    Page<TestingService> getAllServices(Pageable pageable);
    List<TestingServiceResponseDTO> getAllService();
    String uploadServiceImage(MultipartFile file, Integer serviceId);
}

