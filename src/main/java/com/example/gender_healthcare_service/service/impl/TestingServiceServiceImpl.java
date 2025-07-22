package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.TestingServiceRequestDTO;
import com.example.gender_healthcare_service.dto.request.TestingServiceUpdateDTO;
import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.exception.ServiceNotFoundException;
import com.example.gender_healthcare_service.repository.TestingServiceRepository;
import com.example.gender_healthcare_service.service.TestingServiceService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TestingServiceServiceImpl implements TestingServiceService {
    @Autowired
    private  TestingServiceRepository testingServiceRepository;

    @Autowired
    private ModelMapper modelMapper;



    @Autowired
    public TestingServiceServiceImpl(TestingServiceRepository testingServiceRepository) {
        this.testingServiceRepository = testingServiceRepository;
    }

    @Override
    public List<TestingServiceResponseDTO> getAllService() {
        return testingServiceRepository.findAllActive().stream()
                .map(service -> modelMapper.map(service, TestingServiceResponseDTO.class))
                .toList();
    }

    @Override
    public Page<TestingService> getAllServices(Pageable pageable) {
        return testingServiceRepository.findAllByIsDeletedFalse(pageable);
    }

    @Override
    public TestingService getServiceById(Integer id) {
        return testingServiceRepository.findActiveById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    @Override
    public boolean createService(TestingService service) {
        boolean isServiceNameExists = testingServiceRepository.existsByServiceNameAndIsDeletedFalse(service.getServiceName());
        if(isServiceNameExists) {
            System.err.println("Service with name " + service.getServiceName() + " already exists.");
            return false;
        }
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        service.setIsDeleted(false);
        service.setStatus("ACTIVE");
        try {
            testingServiceRepository.save(service);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating service: " + e.getMessage());
            return false;
        }
    }

    @Override
    public TestingServiceResponseDTO updateService(Integer id, TestingServiceUpdateDTO serviceDetails) {

        TestingService existingService = getServiceById(id);
        if(existingService.getIsDeleted()) {
            throw new ServiceNotFoundException("Service with ID " + id + " is deleted and cannot be updated.");
        }
        existingService.setServiceName(serviceDetails.getServiceName());
        existingService.setDescription(serviceDetails.getDescription());
        existingService.setPrice(serviceDetails.getPrice());
        existingService.setDurationMinutes(serviceDetails.getDuration());
        existingService.setUpdatedAt(LocalDateTime.now());
        existingService.setStatus(serviceDetails.getStatus());
        testingServiceRepository.save(existingService);
        TestingServiceResponseDTO responseDTO = modelMapper.map(existingService, TestingServiceResponseDTO.class);
        return responseDTO;
    }

    @Override
    public TestingServiceResponseDTO deleteService(Integer id, boolean isDeleted) {
        TestingService service = getServiceById(id);
        service.setIsDeleted(isDeleted);
        service.setUpdatedAt(LocalDateTime.now());
        TestingService savedService = testingServiceRepository.save(service);
        
        // Tạo response DTO với thông tin chi tiết
        TestingServiceResponseDTO responseDTO = modelMapper.map(savedService, TestingServiceResponseDTO.class);
        responseDTO.setMessage(isDeleted ? "Service deleted successfully" : "Service restored successfully");
        responseDTO.setOperation(isDeleted ? "DELETE" : "RESTORE");
        responseDTO.setOperationTime(LocalDateTime.now());
        
        return responseDTO;
    }
}
