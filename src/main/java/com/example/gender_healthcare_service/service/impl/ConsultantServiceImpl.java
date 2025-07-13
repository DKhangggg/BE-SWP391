package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.ConsultantUpdateDTO;
import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.request.UnavailabilityRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import com.example.gender_healthcare_service.entity.enumpackage.RequestStatus;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.ConsultantUnavailabilityRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.repository.ChatRepository;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private ChatRepository chatRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public ConsultantDTO getConsultantById(Integer consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found with ID: " + consultantId));
        return modelMapper.map(consultant, ConsultantDTO.class);
    }
    @Override
    public ConsultantDTO getCurrentConsultant(){
        User currentUser = (User) org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser == null) {
            throw new RuntimeException("No user is currently authenticated.");
        }
        Consultant consultant = consultantRepository.findById(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Consultant not found for the current user."));
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
            dtos.add(dto);
            System.out.println(dto.toString());
        }
        return dtos;
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
        try {
            LocalDate birthDate = LocalDate.parse(request.getDateOfBirth());
            user.setDateOfBirth(birthDate);
        } catch (Exception e) {
            throw new RuntimeException("Invalid date format. Use YYYY-MM-DD format: " + e.getMessage());
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
        if(c.isPresent()){
            if(consultantDTO.getBiography()!=null){
                c.get().setBiography(consultantDTO.getBiography());
            }
            if(consultantDTO.getSpecialization()!=null){
                c.get().setSpecialization(consultantDTO.getSpecialization());
            }
            if(consultantDTO.getQualifications()!=null){
                c.get().setQualifications(consultantDTO.getQualifications());
            }
            if(consultantDTO.getExperienceYears()!=null){
                c.get().setExperienceYears(consultantDTO.getExperienceYears());
            }
            consultantRepository.save(c.get());
        }
        else {
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
        Optional<Consultant> consultantOptional = consultantRepository.findById(userId);
        return consultantOptional.orElse(null);
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
    @Override
    public long getUnreadMessagesCount() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return chatRepository.findByConsultant(currentUser).stream()
                .filter(chat -> "PENDING".equalsIgnoreCase(chat.getStatus()))
                .count();
    }

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
}
