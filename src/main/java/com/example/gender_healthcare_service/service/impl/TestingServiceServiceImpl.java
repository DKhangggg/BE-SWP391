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
import org.springframework.stereotype.Service;

import java.time.Instant;
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
    public List<TestingService> getAllServices() {
        return testingServiceRepository.findAllActive();
    }

    @Override
    public TestingService getServiceById(Integer id) {
        return testingServiceRepository.findActiveById(id)
                .orElseThrow(() -> new ServiceNotFoundException(id));
    }

    @Override
    public boolean createService(TestingService service) {
        service.setCreatedAt(Instant.now());
        service.setUpdatedAt(Instant.now());
        service.setIsDeleted(false);
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
        existingService.setUpdatedAt(Instant.now());
        testingServiceRepository.save(existingService);
        TestingServiceResponseDTO responseDTO = modelMapper.map(existingService, TestingServiceResponseDTO.class);
        return responseDTO;
    }

    @Override
    public void deleteService(Integer id ,boolean isDeleted) {
        TestingService service = getServiceById(id);
        service.setIsDeleted(isDeleted);
        service.setUpdatedAt(Instant.now());
        testingServiceRepository.save(service);
    }
}
