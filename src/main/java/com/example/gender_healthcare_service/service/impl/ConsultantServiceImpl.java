package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultantUpdateDTO;
import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.request.UnavailabilityRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import com.example.gender_healthcare_service.entity.enumpackage.RequestStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.ConsultantUnavailabilityRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.repository.ConversationRepository;
import com.example.gender_healthcare_service.repository.PaymentRepository;
import com.example.gender_healthcare_service.entity.Payment;
import com.example.gender_healthcare_service.service.ConsultantScheduleService;
import com.example.gender_healthcare_service.service.ConsultantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import com.example.gender_healthcare_service.entity.Conversation;
import com.example.gender_healthcare_service.service.CloudinaryService;

@Service
public class ConsultantServiceImpl implements ConsultantService {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ConsultantScheduleService consultantScheduleService;

    @Autowired
    private ConsultantUnavailabilityRepository consultantUnavailabilityRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Override
    public ConsultantDTO getConsultantById(Integer consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found with ID: " + consultantId));
        ConsultantDTO dto = modelMapper.map(consultant, ConsultantDTO.class);
        // profileImageUrl sẽ được map tự động từ entity Consultant
        return dto;
    }
    @Override
    public ConsultantDTO getCurrentConsultant(){
        String  stringUser =org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(stringUser);
        if (currentUser == null) {
            throw new RuntimeException("No user is currently authenticated.");
        }
        Consultant consultant = consultantRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Consultant not found for the current user."));
        
        // Map consultant to DTO - profileImageUrl sẽ được map tự động
        return modelMapper.map(consultant, ConsultantDTO.class);
    }

    @Override
    public List<ConsultantDTO> getAllConsultants() {
        List<Consultant> c = consultantRepository.findAll();
        List<ConsultantDTO> dtos = new ArrayList<>();
        if(c.isEmpty()){
            return null;
        }
        for (Consultant c1 : c) {
            System.out.println("Consultant: " + c1.toString());
            ConsultantDTO dto = modelMapper.map(c1, ConsultantDTO.class);
            // profileImageUrl sẽ được map tự động từ entity Consultant
            dtos.add(dto);
            System.out.println(dto.toString());
        }
        return dtos;
    }

    @Override
    public List<ConsultantDTO> getFeaturedConsultants() {
        List<Consultant> consultants = consultantRepository.findFeaturedConsultants();
        List<ConsultantDTO> consultantDTOs = new ArrayList<>();
        for (Consultant consultant : consultants) {
            ConsultantDTO dto = modelMapper.map(consultant, ConsultantDTO.class);
            consultantDTOs.add(dto);
        }
        return consultantDTOs;
    }

    @Override
    public List<ConsultantDTO> getAvailableConsultants() {
        try {
            System.out.println("=== DEBUG: getAvailableConsultants ===");
            
            // Lấy tất cả consultant từ database
            List<Consultant> allConsultants = consultantRepository.findAll();
            System.out.println("Total consultants in DB: " + allConsultants.size());
            
            // Filter consultants không bị xóa
            List<Consultant> consultants = allConsultants.stream()
                    .filter(consultant -> consultant != null && (consultant.getIsDeleted() == null || !consultant.getIsDeleted()))
                    .toList();
            
            System.out.println("Active consultants: " + consultants.size());
            
            List<ConsultantDTO> consultantDTOs = new ArrayList<>();
            
            for (Consultant consultant : consultants) {
                System.out.println("Processing consultant ID: " + consultant.getId());
                System.out.println("Consultant user: " + (consultant.getUser() != null ? consultant.getUser().getFullName() : "NULL"));
                
                ConsultantDTO dto = modelMapper.map(consultant, ConsultantDTO.class);
                
                // Set ID manually
                dto.setId(consultant.getId());
                
                // Thêm thông tin user vào DTO
                if (consultant.getUser() != null) {
                    dto.setUsername(consultant.getUser().getUsername());
                    dto.setFullName(consultant.getUser().getFullName());
                    dto.setEmail(consultant.getUser().getEmail());
                    dto.setPhoneNumber(consultant.getUser().getPhoneNumber());
                    dto.setGender(consultant.getUser().getGender());
                    dto.setAddress(consultant.getUser().getAddress());
                    
                    // Convert LocalDate to Date for birthDate
                    if (consultant.getUser().getDateOfBirth() != null) {
                        dto.setBirthDate(java.sql.Date.valueOf(consultant.getUser().getDateOfBirth()));
                    }
                    
                    // Sử dụng profileImageUrl từ Consultant entity, fallback về avatarUrl của User
                    String imageUrl = consultant.getProfileImageUrl();
                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
                        imageUrl = consultant.getUser().getAvatarUrl();
                    }
                    dto.setProfileImageUrl(imageUrl);
                }
                
                // Set consultant-specific fields
                dto.setBiography(consultant.getBiography());
                dto.setQualifications(consultant.getQualifications());
                dto.setExperienceYears(consultant.getExperienceYears());
                dto.setSpecialization(consultant.getSpecialization());
                
                // Set profileImageUrl (already set above, but ensure it's set)
                if (dto.getProfileImageUrl() == null || dto.getProfileImageUrl().trim().isEmpty()) {
                    String imageUrl = consultant.getProfileImageUrl();
                    if (imageUrl == null || imageUrl.trim().isEmpty()) {
                        imageUrl = consultant.getUser().getAvatarUrl();
                    }
                    dto.setProfileImageUrl(imageUrl);
                }
                
                System.out.println("DTO created: " + dto.toString());
                
                consultantDTOs.add(dto);
            }
            
            System.out.println("Returning DTOs: " + consultantDTOs.size());
            System.out.println("First DTO: " + (consultantDTOs.isEmpty() ? "EMPTY" : consultantDTOs.get(0).toString()));
            return consultantDTOs;
            
        } catch (Exception e) {
            System.err.println("ERROR in getAvailableConsultants: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error getting available consultants: " + e.getMessage());
        }
    }

    @Override
    public Consultant createNewConsultant(CreateNewConsultantRequest request) {
        User user = new User();
        if(request.getEmail().equals(userRepository.findUserByEmail(user.getEmail()))){
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        if(request.getUsername().equals(userRepository.findUserByUsername(user.getUsername()))){
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoleName("ROLE_CONSULTANT");
        user.setIsDeleted(false);
        user.setPhoneNumber(request.getPhoneNumber());
        // Sửa: chỉ set dateOfBirth nếu có giá trị hợp lệ
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isEmpty()) {
            try {
                user.setDateOfBirth(java.time.LocalDate.parse(request.getDateOfBirth()));
            } catch (Exception e) {
                // Ignore invalid date, hoặc có thể log warning
            }
        }
        user.setGender(request.getGender());
        user.setAddress(request.getAddress());
        user.setMedicalHistory(request.getMedicalHistory());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        Consultant c = new Consultant();
        c.setUser(savedUser);
        c.setBiography(request.getBiography());
        c.setQualifications(request.getQualifications());
        c.setExperienceYears(request.getExperienceYears());
        c.setSpecialization(request.getSpecialization());
        c.setIsDeleted(false);
        consultantRepository.save(c);
        return c;
    }

    @Override
    public void updateConsultant(ConsultantUpdateDTO consultantDTO) {
        Optional<Consultant> c = consultantRepository.findById(consultantDTO.getId());
        if (c.isPresent()) {
            Consultant consultant = c.get();
            User user = consultant.getUser();
            // Update User fields
            if (consultantDTO.getFullName() != null) user.setFullName(consultantDTO.getFullName());
            if (consultantDTO.getEmail() != null) user.setEmail(consultantDTO.getEmail());
            if (consultantDTO.getPhoneNumber() != null) user.setPhoneNumber(consultantDTO.getPhoneNumber());
            if (consultantDTO.getGender() != null) user.setGender(consultantDTO.getGender());
            if (consultantDTO.getBirthDate() != null) {
                try {
                    user.setDateOfBirth(java.time.LocalDate.parse(consultantDTO.getBirthDate()));
                } catch (Exception e) {
                    // Ignore invalid date
                }
            }
            if (consultantDTO.getAddress() != null) user.setAddress(consultantDTO.getAddress());
            userRepository.save(user);
            // Update Consultant fields
            if (consultantDTO.getBiography() != null) consultant.setBiography(consultantDTO.getBiography());
            if (consultantDTO.getSpecialization() != null) consultant.setSpecialization(consultantDTO.getSpecialization());
            if (consultantDTO.getQualifications() != null) consultant.setQualifications(consultantDTO.getQualifications());
            if (consultantDTO.getExperienceYears() != null) consultant.setExperienceYears(consultantDTO.getExperienceYears());
            consultantRepository.save(consultant);
        } else {
            throw new RuntimeException("Consultant not found with id: " + consultantDTO.getId());
        }
    }

    @Override
    public void deleteConsultant(Integer id) {
        Optional<Consultant> consultant = consultantRepository.findById(id);
        if (consultant.isPresent()) {
            Consultant c = consultant.get();
            c.setIsDeleted(true);
            User user = c.getUser();
            if (user != null) {
                user.setIsDeleted(true);
                userRepository.save(user);
            }
            consultantRepository.save(c);
        } else {
            throw new RuntimeException("Consultant not found with id: " + id);
        }
    }

    @Override
    public void PermanentlyDeleteConsultant(Integer id) {
        Optional<Consultant> consultant = consultantRepository.findById(id);
        if (consultant.isPresent()) {
            Consultant c = consultant.get();
            consultantRepository.delete(c);
            User user = c.getUser();
            if (user != null) {
                userRepository.delete(user);
            }
            consultantRepository.save(c);
        } else {
            throw new RuntimeException("Consultant not found with id: " + id);
        }
    }

    @Override
    public Consultant findConsultantByUserId(Integer userId) {
        return consultantRepository.findByUserId(userId);
    }

    @Override
    public List<UserResponseDTO> getCustomers() {
        List<User> customers = userRepository.findAllByRoleNameAndIsDeletedFalse("ROLE_CUSTOMER");
        System.out.println("Found " + customers.size() + " customers with role ROLE_CUSTOMER");
        
        if (customers.isEmpty()) {
            // Kiểm tra xem có users nào không
            List<User> allUsers = userRepository.findAll();
            System.out.println("Total users in database: " + allUsers.size());
            
            // Kiểm tra roles có sẵn
            allUsers.forEach(user -> {
                System.out.println("User: " + user.getUsername() + ", Role: " + user.getRoleName() + ", Deleted: " + user.getIsDeleted());
            });
        }
        
        return customers.stream()
                .map(user -> modelMapper.map(user, UserResponseDTO.class))
                .toList();
    }

    @Override
    public boolean addUnavailability(UnavailabilityRequest unavailabilityRequest) {
        try {
            ConsultantUnavailability unavailability = new ConsultantUnavailability();
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Consultant consultant = consultantRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Consultant not found"));
            
            unavailability.setConsultant(consultant);
            unavailability.setStartTime(unavailabilityRequest.getStartDate());
            unavailability.setEndTime(unavailabilityRequest.getEndDate());
            unavailability.setReason(unavailabilityRequest.getReason());
            unavailability.setStatus(RequestStatus.IN_PROGRESS);
            
            consultantUnavailabilityRepository.save(unavailability);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<ConsultantUnavailability> getUnavailabilityByDate(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date);
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Consultant consultant = consultantRepository.findById(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Consultant not found"));
            
            return consultantUnavailabilityRepository.findByConsultantAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                consultant, LocalDateTime.now(), LocalDateTime.now().plusDays(30));
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Dashboard APIs
    // ✅ TẠMTHỜI DISABLE CHAT API
    /*
    @Override
    public long getUnreadMessagesCount() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findUserByUsername(username);
        if (currentUser == null) {
            return 0;
        }
        List<Conversation> conversations = conversationRepository.findByConsultant(currentUser);
        return conversations.stream()
                .filter(conversation -> "ACTIVE".equalsIgnoreCase(conversation.getStatus()))
                .count();
    }
    */

    @Override
    public Map<String, Object> getRevenue(String date, String month) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> result = new HashMap<>();
        
        if (date != null && !date.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date);
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();
            BigDecimal total = paymentRepository.findByConsultation_ConsultantAndPaymentDateBetweenAndIsDeletedFalse(currentUser, start, end)
                    .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("amount", total);
        } else if (month != null && !month.isEmpty()) {
            YearMonth ym = YearMonth.parse(month);
            LocalDateTime start = ym.atDay(1).atStartOfDay();
            LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();
            BigDecimal total = paymentRepository.findByConsultation_ConsultantAndPaymentDateBetweenAndIsDeletedFalse(currentUser, start, end)
                    .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            result.put("amount", total);
        } else {
            result.put("amount", BigDecimal.ZERO);
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getTotalRevenue() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Map<String, Object> result = new HashMap<>();
        
        BigDecimal total = paymentRepository.findByConsultation_ConsultantAndIsDeletedFalse(currentUser)
                .stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("amount", total);
        
        return result;
    }

    @Override
    public String uploadProfileImage(org.springframework.web.multipart.MultipartFile file, Integer consultantId) {
        try {
            // Tìm consultant
            Consultant consultant = findConsultantByUserId(consultantId);
            if (consultant == null) {
                throw new RuntimeException("Consultant not found");
            }

            // Upload lên Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);
            
            String secureUrl = (String) uploadResult.get("secure_url");
            String uploadedPublicId = (String) uploadResult.get("public_id");

            // Xóa ảnh cũ nếu có
            if (consultant.getProfileImageUrl() != null && !consultant.getProfileImageUrl().trim().isEmpty()) {
                // Có thể cần thêm logic để xóa ảnh cũ trên Cloudinary
                // cloudinaryService.deleteOldImage(oldPublicId);
            }

            // Update consultant entity
            consultant.setProfileImageUrl(secureUrl);
            consultantRepository.save(consultant);

            return secureUrl;
        } catch (Exception e) {
            throw new RuntimeException("Upload profile image failed: " + e.getMessage());
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return "";
        int dot = filename.lastIndexOf('.');
        return (dot == -1) ? "" : filename.substring(dot + 1);
    }
}
