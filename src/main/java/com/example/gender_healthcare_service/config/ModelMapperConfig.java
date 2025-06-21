package com.example.gender_healthcare_service.config;

import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.TestingServiceResponseDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.TestingService;
import com.example.gender_healthcare_service.entity.User;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        configureUserMapping(modelMapper);
        configureConsultantMapping(modelMapper);
        configureServiceMapping(modelMapper); // Register service mapping
        return modelMapper;
    }

    private void configureConsultantMapping(ModelMapper modelMapper) {
        TypeMap<Consultant, ConsultantDTO> typeMap = modelMapper.createTypeMap(Consultant.class, ConsultantDTO.class);
        typeMap.addMappings(mapper -> {
            try {
                mapper.map(Consultant::getId, ConsultantDTO::setId);
                mapper.map(src->src.getUser().getUsername(), ConsultantDTO::setUsername);
                mapper.map(src->src.getUser().getEmail(), ConsultantDTO::setEmail);
                mapper.map(src->src.getUser().getFullName(), ConsultantDTO::setFullName);
                mapper.map(src->src.getUser().getPhoneNumber(), ConsultantDTO::setPhoneNumber);
                mapper.map(src->src.getUser().getGender(), ConsultantDTO::setGender);
                mapper.map(src->src.getUser().getAddress(), ConsultantDTO::setAddress);
                mapper.map(src->src.getUser().getDateOfBirth(), ConsultantDTO::setBirthDate);
                mapper.map(Consultant::getBiography, ConsultantDTO::setBiography);
                mapper.map(Consultant::getQualifications, ConsultantDTO::setQualifications);
                mapper.map(Consultant::getExperienceYears, ConsultantDTO::setExperienceYears);
                mapper.map(Consultant::getSpecialization, ConsultantDTO::setSpecialization);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void configureServiceMapping(ModelMapper modelMapper) {
        TypeMap<TestingService, TestingServiceResponseDTO> typeMap = modelMapper.createTypeMap(TestingService.class, TestingServiceResponseDTO.class);
        typeMap.addMappings(mapper -> {
            mapper.map(TestingService::getId, TestingServiceResponseDTO::setServiceId);
            mapper.map(TestingService::getServiceName, TestingServiceResponseDTO::setServiceName);
            mapper.map(TestingService::getDescription, TestingServiceResponseDTO::setDescription);
            mapper.map(TestingService::getPrice, TestingServiceResponseDTO::setPrice);
            mapper.map(TestingService::getCreatedAt, TestingServiceResponseDTO::setCreatedAt);
            mapper.map(TestingService::getUpdatedAt, TestingServiceResponseDTO::setUpdatedAt);
            mapper.map(TestingService::getIsDeleted, TestingServiceResponseDTO::setIsDeleted);
        });
        // Optionally, add reverse mapping if needed
        modelMapper.createTypeMap(TestingServiceResponseDTO.class, TestingService.class)
            .addMappings(mapper -> {
                mapper.map(TestingServiceResponseDTO::getServiceId, TestingService::setId);
                mapper.map(TestingServiceResponseDTO::getServiceName, TestingService::setServiceName);
                mapper.map(TestingServiceResponseDTO::getDescription, TestingService::setDescription);
                mapper.map(TestingServiceResponseDTO::getPrice, TestingService::setPrice);
                mapper.map(TestingServiceResponseDTO::getCreatedAt, TestingService::setCreatedAt);
                mapper.map(TestingServiceResponseDTO::getUpdatedAt, TestingService::setUpdatedAt);
                mapper.map(TestingServiceResponseDTO::getIsDeleted, TestingService::setIsDeleted);
            });
    }

    private void configureUserMapping(ModelMapper modelMapper) {
        TypeMap<User, UserResponseDTO> typeMap = modelMapper.createTypeMap(User.class, UserResponseDTO.class);
        typeMap.addMappings(mapper -> {
            try {
                mapper.map(User::getId, UserResponseDTO::setId);
                mapper.map(User::getUsername, UserResponseDTO::setUsername);
                mapper.map(User::getEmail, UserResponseDTO::setEmail);
                mapper.map(User::getFullName, UserResponseDTO::setFullName);
                mapper.map(User::getRoleName, UserResponseDTO::setRoleName);
                mapper.map(User::getPhoneNumber, UserResponseDTO::setPhoneNumber);
                mapper.map(User::getAddress, UserResponseDTO::setAddress);
                mapper.map(User::getDateOfBirth, UserResponseDTO::setDateOfBirth);
                mapper.map(User::getMedicalHistory, UserResponseDTO::setMedicalHistory);
                mapper.map(User::getGender, UserResponseDTO::setGender);
                mapper.map(User::getDescription, UserResponseDTO::setDescription);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
//    private void configureConsultantMapping(ModelMapper modelMapper) {
//        modelMapper.typeMap(Consultant.class, Consultant
