package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.CreateNewConsultantRequest;
import com.example.gender_healthcare_service.dto.response.ConsultantDTO;
import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.ConsultantRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.ConsultantScheduleService;
import com.example.gender_healthcare_service.service.ConsultantService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public ConsultantDTO getConsultantById(Integer consultantId) {
        Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found with ID: " + consultantId));
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
        user.setCreatedAt(new Date().toInstant());
        user.setUpdatedAt(new Date().toInstant());

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
    public void updateConsultant(Integer id, ConsultantDTO consultantDTO) {
        Optional<Consultant> c = consultantRepository.findById(id);
        if(c.isPresent()){
            c.get().setBiography(consultantDTO.getBiography());
            c.get().setQualifications(consultantDTO.getQualifications());
            c.get().setExperienceYears(consultantDTO.getExperienceYears());
            c.get().setSpecialization(consultantDTO.getSpecialization());

            User user = c.get().getUser();
            if(user!=null){
                user.setFullName(consultantDTO.getFullName());
                user.setEmail(consultantDTO.getEmail());
                user.setAddress(consultantDTO.getAddress());
                user.setPhoneNumber(consultantDTO.getPhoneNumber());
                user.setGender(consultantDTO.getGender());
                try {
                    LocalDate birthDate = LocalDate.parse(consultantDTO.getBirthDate().toString());
                    user.setDateOfBirth(birthDate);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid date format. Use YYYY-MM-DD format: " + e.getMessage());
                }
            }
        }
        else {
            throw new RuntimeException("Consultant not found with id: " + id);
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
}
