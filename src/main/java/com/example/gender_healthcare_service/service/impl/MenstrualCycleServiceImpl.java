package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.EnhancedMenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualLogRequestDTO;
import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.SeverityLevel;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.service.MenstrualCycleAnalyticsService;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    @Autowired
    private MenstrualCycleAnalyticsService analyticsService;
    @Autowired
    private SymptomRepository symptomRepository;
    @Autowired
    private SymptomLogRepository symptomLogRepository;
    @Autowired
    private ConsultantRepository consultantRepository;

    private static final int DEFAULT_CYCLE_LENGTH = 28;

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
        menstrualLog.setLogDate(LocalDateTime.now());
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
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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

    @Override
    public void logEnhancedMenstrualData(EnhancedMenstrualLogRequestDTO requestDTO) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        User user = userRepository.findUserByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found in the system");
        }

        if (!"FEMALE".equalsIgnoreCase(user.getGender()) && !"Female".equalsIgnoreCase(user.getGender())) {
            throw new RuntimeException("Menstrual cycle tracking is only available for female users");
        }

        MenstrualCycle currentCycle = menstrualCycleRepository.findByUserId(user.getId());
        if (currentCycle == null) {
            // Create a new menstrual cycle for the user
            currentCycle = new MenstrualCycle();
            currentCycle.setUser(user);
            currentCycle.setStartDate(requestDTO.getLogDate() != null ? requestDTO.getLogDate().toLocalDate() : LocalDate.now());
            currentCycle.setCycleLength(DEFAULT_CYCLE_LENGTH);
            currentCycle.setPeriodDuration(5); // Default period duration
            currentCycle.setIsRegular(true);
            currentCycle.setCreatedAt(LocalDateTime.now());
            currentCycle.setUpdatedAt(LocalDateTime.now());
            currentCycle = menstrualCycleRepository.save(currentCycle);
        }

        // Create enhanced menstrual log
        MenstrualLog menstrualLog = new MenstrualLog();
        menstrualLog.setMenstrualCycle(currentCycle);
        menstrualLog.setLogDate(requestDTO.getLogDate() != null ? requestDTO.getLogDate() : LocalDateTime.now());
        menstrualLog.setIsActualPeriod(requestDTO.getIsActualPeriod());
        menstrualLog.setFlowIntensity(requestDTO.getFlowIntensity());
        menstrualLog.setMood(requestDTO.getMood());
        menstrualLog.setTemperature(requestDTO.getTemperature());
        menstrualLog.setNotes(requestDTO.getNotes());
        menstrualLog.setCreatedAt(LocalDateTime.now());
        menstrualLog.setUpdatedAt(LocalDateTime.now());

        MenstrualLog savedLog = menstrualLogRepository.save(menstrualLog);

        // Log symptoms if provided
        if (requestDTO.getSymptoms() != null && !requestDTO.getSymptoms().isEmpty()) {
            for (EnhancedMenstrualLogRequestDTO.SymptomEntryDTO symptomEntry : requestDTO.getSymptoms()) {
                Symptom symptom = findOrCreateSymptom(symptomEntry);

                SymptomLog symptomLog = new SymptomLog();
                symptomLog.setMenstrualLog(savedLog);
                symptomLog.setSymptom(symptom);
                symptomLog.setSeverity(SeverityLevel.valueOf(symptomEntry.getSeverity()));
                symptomLog.setNotes(symptomEntry.getNotes());
                symptomLog.setCreatedAt(LocalDateTime.now());

                symptomLogRepository.save(symptomLog);
            }
        }

        // Update cycle statistics
        analyticsService.updateCycleStatistics(user.getId());
    }

    @Override
    public PeriodPredictionDTO getPeriodPrediction(Integer userId) {
        return analyticsService.predictNextPeriod(userId);
    }

    @Override
    public FertilityWindowDTO getFertilityWindow(Integer userId) {
        return analyticsService.calculateFertilityWindow(userId);
    }

    @Override
    public CycleAnalyticsDTO getCycleAnalytics(Integer userId) {
        return analyticsService.analyzeCyclePatterns(userId);
    }

    @Override
    public List<SymptomPatternDTO> getSymptomPatterns(Integer userId) {
        return analyticsService.analyzeSymptomPatterns(userId);
    }

    @Override
    public List<String> getHealthInsights(Integer userId) {
        return analyticsService.getHealthInsights(userId);
    }

    @Override
    public List<MenstrualLogResponseDTO> getMenstrualLogsByDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<MenstrualLog> logs = menstrualLogRepository.findByUserIdAndDateRange(userId, startDate.toLocalDate(), endDate.toLocalDate());
        return logs.stream()
                .map(log -> modelMapper.map(log, MenstrualLogResponseDTO.class))
                .collect(Collectors.toList());
    }

    // Consultant features for managing user menstrual data
    @Override
    public void logMenstrualDataForUser(Integer userId, EnhancedMenstrualLogRequestDTO requestDTO, Integer consultantId) {
        validateConsultantAccess(consultantId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!"FEMALE".equalsIgnoreCase(user.getGender()) && !"Female".equalsIgnoreCase(user.getGender())) {
            throw new RuntimeException("Menstrual cycle tracking is only available for female users");
        }

        MenstrualCycle currentCycle = menstrualCycleRepository.findByUserId(userId);
        if (currentCycle == null) {
            currentCycle = createNewMenstrualCycleForUser(user, requestDTO.getLogDate());
        }

        MenstrualLog savedLog = logEnhancedData(currentCycle, requestDTO);

        if (requestDTO.getSymptoms() != null && !requestDTO.getSymptoms().isEmpty()) {
            logSymptomsForMenstrualLog(savedLog, requestDTO.getSymptoms());
        }

        analyticsService.updateCycleStatistics(userId);
    }

    @Override
    public MenstrualCycleResponseDTO updateUserMenstrualCycle(Integer userId, MenstrualCycleRequestDTO requestDTO, Integer consultantId) {
        validateConsultantAccess(consultantId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        MenstrualCycle cycle = menstrualCycleRepository.findByUserId(userId);
        if (cycle == null) {
            throw new RuntimeException("No menstrual cycle found for user to update.");
        }

        if (requestDTO.getStartDate() != null) {
            cycle.setStartDate(requestDTO.getStartDate());
        }
        if (requestDTO.getPeriodDay() != null) {
            LocalDate predictedNextPeriodStartDate = requestDTO.getPeriodDay().plusDays(DEFAULT_CYCLE_LENGTH);
            cycle.setPeriodDay(predictedNextPeriodStartDate);
        }
        cycle.setUpdatedAt(LocalDateTime.now());
        menstrualCycleRepository.save(cycle);
        analyticsService.updateCycleStatistics(userId);
        return modelMapper.map(cycle, MenstrualCycleResponseDTO.class);
    }

    @Override
    public List<MenstrualLogResponseDTO> getUserMenstrualHistory(Integer userId, Integer consultantId) {
        validateConsultantAccess(consultantId, userId);
        return getMenstrualLogsByDateRange(userId, LocalDateTime.now().minusYears(1), LocalDateTime.now());
    }

    @Override
    public CycleAnalyticsDTO getUserCycleAnalytics(Integer userId, Integer consultantId) {
        validateConsultantAccess(consultantId, userId);
        return analyticsService.analyzeCyclePatterns(userId);
    }

    @Override
    public PeriodPredictionDTO getUserPeriodPrediction(Integer userId, Integer consultantId) {
        validateConsultantAccess(consultantId, userId);
        return analyticsService.predictNextPeriod(userId);
    }

    @Override
    public void deleteMenstrualLog(Integer logId, Integer consultantId) {
        MenstrualLog log = menstrualLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Menstrual log not found"));

        validateConsultantAccess(consultantId, log.getMenstrualCycle().getUser().getId());

        menstrualLogRepository.delete(log);
        analyticsService.updateCycleStatistics(log.getMenstrualCycle().getUser().getId());
    }

    @Override
    @Transactional
    public MenstrualLogResponseDTO updateMenstrualLog(Integer logId, EnhancedMenstrualLogRequestDTO requestDTO, Integer consultantId) {
        MenstrualLog existingLog = menstrualLogRepository.findById(logId)
                .orElseThrow(() -> new RuntimeException("Menstrual log not found"));

        validateConsultantAccess(consultantId, existingLog.getMenstrualCycle().getUser().getId());

        // Map basic properties, but handle collections manually
        modelMapper.map(requestDTO, existingLog);
        existingLog.setUpdatedAt(LocalDateTime.now());

        // Handle symptom updates
        if (requestDTO.getSymptoms() != null) {
            // Clear existing symptoms and let orphanRemoval handle deletion
            existingLog.getSymptoms().clear();

            // Add new symptoms
            for (EnhancedMenstrualLogRequestDTO.SymptomEntryDTO symptomEntry : requestDTO.getSymptoms()) {
                Symptom symptom = findOrCreateSymptom(symptomEntry);
                SymptomLog symptomLog = new SymptomLog();
                symptomLog.setMenstrualLog(existingLog);
                symptomLog.setSymptom(symptom);
                symptomLog.setSeverity(SeverityLevel.valueOf(symptomEntry.getSeverity().toUpperCase()));
                symptomLog.setNotes(symptomEntry.getNotes());
                symptomLog.setCreatedAt(LocalDateTime.now());
                existingLog.getSymptoms().add(symptomLog);
            }
        }

        MenstrualLog updatedLog = menstrualLogRepository.save(existingLog);

        analyticsService.updateCycleStatistics(existingLog.getMenstrualCycle().getUser().getId());
        return modelMapper.map(updatedLog, MenstrualLogResponseDTO.class);
    }

    @Override
    @Transactional
    public MenstrualLogResponseDTO updateEnhancedMenstrualLog(Long logId, EnhancedMenstrualLogRequestDTO requestDTO) {
        MenstrualLog existingLog = menstrualLogRepository.findById(logId.intValue())
                .orElseThrow(() -> new RuntimeException("Menstrual log not found with ID: " + logId));

        // Map non-collection fields from DTO to the entity
        modelMapper.map(requestDTO, existingLog);
        existingLog.setUpdatedAt(LocalDateTime.now());

        // Handle symptom updates correctly
        if (requestDTO.getSymptoms() != null) {
            // Clear the old collection. If orphanRemoval=true, this will delete them.
            existingLog.getSymptoms().clear();

            // Create and add the new symptom logs to the collection
            for (EnhancedMenstrualLogRequestDTO.SymptomEntryDTO symptomEntry : requestDTO.getSymptoms()) {
                Symptom symptom = findOrCreateSymptom(symptomEntry);
                SymptomLog newSymptomLog = new SymptomLog();
                newSymptomLog.setMenstrualLog(existingLog);
                newSymptomLog.setSymptom(symptom);
                newSymptomLog.setSeverity(SeverityLevel.valueOf(symptomEntry.getSeverity().toUpperCase()));
                newSymptomLog.setNotes(symptomEntry.getNotes());
                newSymptomLog.setCreatedAt(LocalDateTime.now());
                existingLog.getSymptoms().add(newSymptomLog);
            }
        }

        // Save the parent entity, and changes to the collection will be cascaded.
        MenstrualLog updatedLog = menstrualLogRepository.save(existingLog);

        // Recalculate analytics after update
        analyticsService.updateCycleStatistics(existingLog.getMenstrualCycle().getUser().getId());

        return modelMapper.map(updatedLog, MenstrualLogResponseDTO.class);
    }

    private void validateConsultantAccess(Integer consultantId, Integer userId) {
        consultantRepository.findById(consultantId)
                .orElseThrow(() -> new RuntimeException("Consultant not found"));
        // In a real application, you would have a linking table to check if this consultant is assigned to this user.
        // For now, we assume any consultant can access any female user's data.
    }

    private MenstrualCycle createNewMenstrualCycleForUser(User user, LocalDateTime startDate) {
        MenstrualCycle newCycle = new MenstrualCycle();
        newCycle.setUser(user);
        newCycle.setStartDate(startDate != null ? startDate.toLocalDate() : LocalDate.now());
        newCycle.setCycleLength(DEFAULT_CYCLE_LENGTH);
        newCycle.setPeriodDuration(5);
        newCycle.setIsRegular(true);
        newCycle.setCreatedAt(LocalDateTime.now());
        newCycle.setUpdatedAt(LocalDateTime.now());
        return menstrualCycleRepository.save(newCycle);
    }

    private MenstrualLog logEnhancedData(MenstrualCycle cycle, EnhancedMenstrualLogRequestDTO requestDTO) {
        MenstrualLog menstrualLog = new MenstrualLog();
        modelMapper.map(requestDTO, menstrualLog);
        menstrualLog.setMenstrualCycle(cycle);
        menstrualLog.setLogDate(requestDTO.getLogDate() != null ? requestDTO.getLogDate() : LocalDateTime.now());
        menstrualLog.setCreatedAt(LocalDateTime.now());
        menstrualLog.setUpdatedAt(LocalDateTime.now());
        return menstrualLogRepository.save(menstrualLog);
    }

    private void logSymptomsForMenstrualLog(MenstrualLog log, List<EnhancedMenstrualLogRequestDTO.SymptomEntryDTO> symptoms) {
        for (EnhancedMenstrualLogRequestDTO.SymptomEntryDTO symptomEntry : symptoms) {
            Symptom symptom = findOrCreateSymptom(symptomEntry);

            SymptomLog symptomLog = new SymptomLog();
            symptomLog.setMenstrualLog(log);
            symptomLog.setSymptom(symptom); // Associate the persistent symptom
            symptomLog.setSeverity(SeverityLevel.valueOf(symptomEntry.getSeverity().toUpperCase()));
            symptomLog.setNotes(symptomEntry.getNotes());
            symptomLog.setCreatedAt(LocalDateTime.now());

            // Save each SymptomLog individually to ensure correct order
            symptomLogRepository.save(symptomLog);
        }
    }

    private Symptom findOrCreateSymptom(EnhancedMenstrualLogRequestDTO.SymptomEntryDTO symptomEntry) {
        if (symptomEntry.getSymptomId() != null) {
            return symptomRepository.findById(symptomEntry.getSymptomId())
                    .orElseThrow(() -> new RuntimeException("Symptom not found"));
        }

        Optional<Symptom> existingSymptom = symptomRepository.findBySymptomNameIgnoreCase(symptomEntry.getSymptomName());
        if (existingSymptom.isPresent()) {
            return existingSymptom.get();
        }

        // If not found, create and SAVE the new symptom before returning it
        Symptom newSymptom = new Symptom();
        newSymptom.setSymptomName(symptomEntry.getSymptomName());
        newSymptom.setCategory("USER_DEFINED"); // Or another default category
        newSymptom.setIsActive(true);
        return symptomRepository.save(newSymptom);
    }
}
