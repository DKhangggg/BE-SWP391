package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleTrackerResponseDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualLogResponseDTO;
import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.repository.MenstrualCycleRepository;
import com.example.gender_healthcare_service.repository.MenstrualLogRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenstrualCycleServiceImpl implements MenstrualCycleService {

    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    @Autowired
    private MenstrualLogRepository menstrualLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final int DEFAULT_CYCLE_LENGTH = 25;

    @Override
    public MenstrualCycleResponseDTO addOrUpdateMenstrualCycle(MenstrualCycleRequestDTO requestDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        if(username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Invalid username");
        }
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }
        MenstrualCycle existingCycle = menstrualCycleRepository.findByUserId(user.getId());
        if(existingCycle != null) {
            if(requestDTO.getStartDate()!=null){
                existingCycle.setStartDate(requestDTO.getStartDate());
            }
            LocalDate predictedNextPeriodStartDate = requestDTO.getPeriodDay().plusDays(DEFAULT_CYCLE_LENGTH);
            existingCycle.setUpdatedAt(LocalDateTime.now());
            existingCycle.setPeriodDay(predictedNextPeriodStartDate);
            menstrualCycleRepository.save(existingCycle);
            return modelMapper.map(existingCycle, MenstrualCycleResponseDTO.class);
        } else {
            MenstrualCycle newCycle = new MenstrualCycle();
            newCycle.setUser(user);
            if(requestDTO.getStartDate()==null){
                newCycle.setStartDate(LocalDate.now());
            }
            newCycle.setStartDate(requestDTO.getStartDate());
            LocalDate predictedNextPeriodStartDate = requestDTO.getPeriodDay().plusDays(DEFAULT_CYCLE_LENGTH);
            newCycle.setPeriodDay(predictedNextPeriodStartDate);
            newCycle.setCreatedAt(LocalDateTime.now());
            newCycle.setUpdatedAt(LocalDateTime.now());
            menstrualCycleRepository.save(newCycle);
            return modelMapper.map(newCycle, MenstrualCycleResponseDTO.class);
        }
    }

    @Override
    public void logMenstrualPeriod(MenstrualLogRequestDTO logDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }
        MenstrualCycle currentCycle = menstrualCycleRepository.findByUserId(user.getId());
        if (currentCycle == null) {
            throw new RuntimeException("No menstrual cycle found for user " + user.getUsername());
        }
        MenstrualLog menstrualLog = new MenstrualLog();
        menstrualLog.setMenstrualCycle(currentCycle);
        menstrualLog.setLogDate(LocalDate.now());
        menstrualLog.setNotes(logDTO.getNotes());
        menstrualLog.setCreatedAt(LocalDateTime.now());
        menstrualLog.setUpdatedAt(LocalDateTime.now());
        menstrualLogRepository.save(menstrualLog);
    }

    @Override
    public List<MenstrualLogResponseDTO> getMenstrualLogs(Integer cycleId) {
        MenstrualCycle menstrualCycle = menstrualCycleRepository.findById(cycleId)
                .orElseThrow(() -> new RuntimeException("Menstrual cycle with ID " + cycleId + " not found."));
        List<MenstrualLog> logs = menstrualLogRepository.findByMenstrualCycle(menstrualCycle);
        return logs.stream()
                .map(log -> modelMapper.map(log, MenstrualLogResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MenstrualCycleTrackerResponseDTO getMenstrualCycleTracker() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }
        MenstrualCycle currentCycle = menstrualCycleRepository.findByUserId(user.getId());
        if (currentCycle == null) {
            throw new RuntimeException("No menstrual cycle found for user " + user.getUsername());
        }
        return new MenstrualCycleTrackerResponseDTO(currentCycle.getPeriodDay());
    }
}

