package com.example.gender_healthcare_service.mapper;

import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        // Bật ignore ambiguity và skip null để tránh lỗi mapping
        modelMapper.getConfiguration()
            .setAmbiguityIgnored(true)
            .setSkipNullEnabled(true)
            .setPropertyCondition(context -> {
                // Chỉ map các property có giá trị
                return context.getSource() != null;
            });
        configureUserMapping(modelMapper);
        configureConsultantMapping(modelMapper);
        configureServiceMapping(modelMapper);
        // Bỏ qua configureQuestionMapping và configureAnswerMapping vì đã chuyển sang manual mapping
        configureBlogCategoryMapping(modelMapper);
        // Bỏ qua configureConsultationMapping vì sử dụng manual mapping trong service
        // Bỏ qua configureBookingMapping vì sử dụng manual mapping trong service
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
                mapper.map(User::getAvatarUrl, UserResponseDTO::setAvatarUrl);
                mapper.map(User::getAvatarPublicId, UserResponseDTO::setAvatarPublicId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void configureBlogCategoryMapping(ModelMapper modelMapper) {
        TypeMap<BlogCategory, BlogCategoryDTO> typeMap = modelMapper.createTypeMap(BlogCategory.class, BlogCategoryDTO.class);
        typeMap.addMappings(mapper -> {
            mapper.map(BlogCategory::getCategoryID, BlogCategoryDTO::setCategoryID);
            mapper.map(BlogCategory::getCategoryName, BlogCategoryDTO::setCategoryName);
            mapper.map(BlogCategory::getSlug, BlogCategoryDTO::setSlug);
            mapper.map(BlogCategory::getDescription, BlogCategoryDTO::setDescription);
            mapper.map(BlogCategory::getThumbnailUrl, BlogCategoryDTO::setThumbnailUrl);
            mapper.map(BlogCategory::getCreatedAt, BlogCategoryDTO::setCreatedAt);
            mapper.map(BlogCategory::getUpdatedAt, BlogCategoryDTO::setUpdatedAt);
        });

        modelMapper.typeMap(com.example.gender_healthcare_service.dto.request.BlogCategoryRequestDTO.class, BlogCategory.class)
                .addMappings(mapper -> {
                    mapper.map(src -> src.getCategoryName(), BlogCategory::setCategoryName);
                    mapper.map(src -> src.getSlug(), BlogCategory::setSlug);
                    mapper.map(src -> src.getDescription(), BlogCategory::setDescription);
                    mapper.map(src -> src.getThumbnailUrl(), BlogCategory::setThumbnailUrl);
                });
    }

    private void configureBookingMapping(ModelMapper modelMapper) {
        TypeMap<Booking, BookingResponseDTO> typeMap = modelMapper.createTypeMap(Booking.class, BookingResponseDTO.class);
        typeMap.addMappings(mapper -> {
            // Explicitly map each field to avoid conflicts
            mapper.map(Booking::getId, BookingResponseDTO::setBookingId);
            mapper.map(src -> src.getCustomerID().getId(), BookingResponseDTO::setCustomerId);
            mapper.map(src -> src.getCustomerID().getFullName(), BookingResponseDTO::setCustomerFullName);
            mapper.map(src -> src.getCustomerID().getEmail(), BookingResponseDTO::setCustomerEmailAddress);
            mapper.map(src -> src.getCustomerID().getPhoneNumber(), BookingResponseDTO::setCustomerPhone);
            mapper.map(src -> src.getService().getId(), BookingResponseDTO::setServiceId);
            mapper.map(src -> src.getService().getServiceName(), BookingResponseDTO::setServiceName);
            mapper.map(src -> src.getService().getDescription(), BookingResponseDTO::setServiceDescription);
            mapper.map(src -> src.getService().getPrice(), BookingResponseDTO::setServicePrice);
            mapper.map(Booking::getStatus, BookingResponseDTO::setStatus);
            mapper.map(Booking::getResult, BookingResponseDTO::setResult);
            mapper.map(Booking::getResultDate, BookingResponseDTO::setResultDate);
            mapper.map(Booking::getCreatedAt, BookingResponseDTO::setCreatedAt);
            
            // Time slot mapping with safe null check
            mapper.map(src -> {
                try {
                    return src.getTimeSlot() != null ? src.getTimeSlot().getTimeSlotID() : null;
                } catch (Exception e) {
                    return null;
                }
            }, BookingResponseDTO::setTimeSlotId);
            
            mapper.map(src -> {
                try {
                    return src.getTimeSlot() != null ? src.getTimeSlot().getSlotDate() : null;
                } catch (Exception e) {
                    return null;
                }
            }, BookingResponseDTO::setSlotDate);
            
            mapper.map(src -> {
                try {
                    return src.getTimeSlot() != null ? src.getTimeSlot().getStartTime() : null;
                } catch (Exception e) {
                    return null;
                }
            }, BookingResponseDTO::setStartTime);
            
            mapper.map(src -> {
                try {
                    return src.getTimeSlot() != null ? src.getTimeSlot().getEndTime() : null;
                } catch (Exception e) {
                    return null;
                }
            }, BookingResponseDTO::setEndTime);
            
            mapper.map(src -> {
                try {
                    return src.getTimeSlot() != null ? src.getTimeSlot().getSlotType() : null;
                } catch (Exception e) {
                    return null;
                }
            }, BookingResponseDTO::setSlotType);
            
            // Skip any other fields to avoid conflicts
            mapper.skip(BookingResponseDTO::setNotes);
            mapper.skip(BookingResponseDTO::setDisplayInfo);
        });
    }


}
