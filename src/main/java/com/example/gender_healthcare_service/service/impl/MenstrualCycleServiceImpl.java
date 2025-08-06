package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.request.CreateMenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.MenstrualCycleRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateDayLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.QuickLogRequestDTO;
import com.example.gender_healthcare_service.dto.request.UpdateCycleSettingsRequestDTO;
import com.example.gender_healthcare_service.dto.response.MenstrualCycleResponseDTO;
import com.example.gender_healthcare_service.dto.response.DayLogResponseDTO;
import com.example.gender_healthcare_service.dto.response.PhaseInfoDTO;
import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import com.example.gender_healthcare_service.entity.Symptom;
import com.example.gender_healthcare_service.entity.SymptomLog;
import com.example.gender_healthcare_service.entity.User;
import com.example.gender_healthcare_service.entity.enumpackage.FlowIntensity;
import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import com.example.gender_healthcare_service.entity.enumpackage.SeverityLevel;
import com.example.gender_healthcare_service.repository.MenstrualCycleRepository;
import com.example.gender_healthcare_service.repository.MenstrualLogRepository;
import com.example.gender_healthcare_service.repository.SymptomRepository;
import com.example.gender_healthcare_service.repository.SymptomLogRepository;
import com.example.gender_healthcare_service.repository.UserRepository;
import com.example.gender_healthcare_service.service.MenstrualCycleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Comparator.comparing;

@Service
public class MenstrualCycleServiceImpl implements MenstrualCycleService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;
    @Autowired
    private MenstrualLogRepository menstrualLogRepository;
    @Autowired
    private SymptomRepository symptomRepository;
    @Autowired
    private SymptomLogRepository symptomLogRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MenstrualCycleResponseDTO getCurrentMenstrualCycle(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }
        if (user.getGender() == null) {
            throw new RuntimeException("Bạn chưa chọn giới tính, vui lòng cập nhật hồ sơ cá nhân");
        }
        if (user.getGender().equalsIgnoreCase("MALE")) {
            throw new RuntimeException("Tính năng này chỉ dành cho người dùng nữ");
        }
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUser_IdOrderByStartDateDesc(user.getId());
        if (cycles == null || cycles.isEmpty()) {
            return null;
        }
        MenstrualCycle latest = cycles.stream().max(comparing(MenstrualCycle::getStartDate)).orElse(null);
        return latest != null ? modelMapper.map(latest, MenstrualCycleResponseDTO.class) : null;
    }

    @Override
    public List<MenstrualCycleResponseDTO> createMenstrualCycle(Authentication authentication, List<CreateMenstrualCycleRequestDTO> requests) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) throw new RuntimeException("Không tìm thấy người dùng");
        if (requests == null || requests.isEmpty()) throw new RuntimeException("Danh sách chu kỳ không được để trống");

        requests.sort(comparing(req -> LocalDate.parse(req.getStartDate())));

        List<MenstrualCycle> cycles = new ArrayList<>();
        LocalDate prevStart = null;
        List<Integer> periodDurations = new ArrayList<>();
        List<Integer> cycleLengths = new ArrayList<>();

        for (int i = 0; i < requests.size(); i++) {
            CreateMenstrualCycleRequestDTO req = requests.get(i);
            MenstrualCycle cycle = new MenstrualCycle();
            cycle.setUser(user);
            LocalDate startDate = LocalDate.parse(req.getStartDate());
            cycle.setStartDate(startDate);
            cycle.setPeriodDay(startDate);

            Integer periodDuration = req.getPeriodDuration();
            cycle.setPeriodDuration(periodDuration);
            periodDurations.add(periodDuration);

            if (prevStart != null) {
                int cycleLength = (int) DAYS.between(prevStart, startDate);
                cycle.setCycleLength(cycleLength);
                cycleLengths.add(cycleLength);
            } else {
                cycle.setCycleLength(req.getCycleLength() != null ? req.getCycleLength() : 28);
                cycleLengths.add(cycle.getCycleLength());
            }
            prevStart = startDate;

            cycle.setCreatedAt(LocalDateTime.now());
            cycle.setUpdatedAt(LocalDateTime.now());
            cycle.setIsRegular(true);

            cycles.add(cycle);
        }

        double avgCycleLength = cycleLengths.stream().mapToInt(Integer::intValue).average().orElse(28);
        double avgPeriodDuration = periodDurations.stream().mapToInt(Integer::intValue).average().orElse(5);

        for (int i = 0; i < cycles.size(); i++) {
            MenstrualCycle cycle = cycles.get(i);
            cycle.setAverageCycleLength(avgCycleLength);
            cycle.setAveragePeriodDuration(avgPeriodDuration);

            if (i < cycles.size() - 1) {
                cycle.setNextPredictedPeriod(cycles.get(i + 1).getStartDate());
            } else {
                cycle.setNextPredictedPeriod(cycle.getStartDate().plusDays((long) avgCycleLength));
            }

            LocalDate ovulation = cycle.getStartDate().plusDays((long) (cycle.getCycleLength() - 14));
            cycle.setOvulationDate(ovulation);
            cycle.setFertilityWindowStart(ovulation.minusDays(5));
            cycle.setFertilityWindowEnd(ovulation.plusDays(1));
        }

        java.util.List<MenstrualCycleResponseDTO> result = new java.util.ArrayList<>();
        for (MenstrualCycle cycle : cycles) {
            MenstrualCycle saved = menstrualCycleRepository.save(cycle);
            result.add(modelMapper.map(saved, MenstrualCycleResponseDTO.class));
        }
        return result;
    }

    @Override
    @Transactional
    public DayLogResponseDTO getDayLog(Authentication authentication, String date) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        LocalDate targetDate = LocalDate.parse(date);
        DayLogResponseDTO response = new DayLogResponseDTO();
        response.setDate(targetDate);

        MenstrualCycleResponseDTO currentCycle = getCurrentMenstrualCycle(authentication);
        if (currentCycle != null) {
            LocalDate cycleStart = currentCycle.getStartDate();
            LocalDate ovulationDate = currentCycle.getOvulationDate();
            LocalDate fertilityStart = currentCycle.getFertilityWindowStart();
            LocalDate fertilityEnd = currentCycle.getFertilityWindowEnd();

            if (targetDate.isAfter(cycleStart.minusDays(1)) &&
                targetDate.isBefore(cycleStart.plusDays(currentCycle.getPeriodDuration()))) {
                response.setPhase("PERIOD");
            } else if (targetDate.equals(ovulationDate)) {
                response.setPhase("OVULATION");
            } else if (targetDate.isAfter(fertilityStart.minusDays(1)) && 
                       targetDate.isBefore(fertilityEnd.plusDays(1))) {
                response.setPhase("FERTILE");
            } else {
                response.setPhase("PREDICTED");
            }

            MenstrualCycle cycle = menstrualCycleRepository.findByUser_IdOrderByStartDateDesc(user.getId())
                .stream().max(comparing(MenstrualCycle::getStartDate)).orElse(null);
            
            if (cycle != null) {
                LocalDateTime startOfDay = targetDate.atStartOfDay();
                LocalDateTime endOfDay = targetDate.atTime(23, 59, 59);
                
                List<MenstrualLog> logs = menstrualLogRepository.findByUserIdAndDateRange(
                    user.getId(), targetDate, targetDate);
                
                if (!logs.isEmpty()) {
                    MenstrualLog log = logs.get(0);
                    response.setIsPeriodDay(log.getIsActualPeriod());
                    response.setIntensity(log.getFlowIntensity() != null ? log.getFlowIntensity().name() : null);
                    response.setMood(log.getMood() != null ? log.getMood().name() : null);
                    response.setNotes(log.getNotes());
                    
                    List<SymptomLog> symptomLogs = symptomLogRepository.findByMenstrualLog(log);
                    String symptoms = symptomLogs.stream()
                        .map(sl -> sl.getSymptom().getSymptomName() + 
                             (sl.getSeverity() != null ? " (" + sl.getSeverity().name() + ")" : ""))
                        .collect(Collectors.joining(", "));
                    response.setSymptoms(symptoms);
                } else {
                    response.setIsPeriodDay(false);
                    response.setIntensity(null);
                    response.setSymptoms(null);
                    response.setMood(null);
                    response.setNotes(null);
                }
            }
        }

        return response;
    }

    @Override
    @Transactional
    public DayLogResponseDTO updateDayLog(Authentication authentication, UpdateDayLogRequestDTO request) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        MenstrualCycle cycle = menstrualCycleRepository.findByUser_IdOrderByStartDateDesc(user.getId())
            .stream().max(comparing(MenstrualCycle::getStartDate)).orElse(null);
        
        if (cycle == null) {
            throw new RuntimeException("Chưa có chu kỳ kinh nguyệt");
        }

        LocalDateTime startOfDay = request.getDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDate().atTime(23, 59, 59);
        
        List<MenstrualLog> existingLogs = menstrualLogRepository.findByUserIdAndDateRange(
            user.getId(), request.getDate(), request.getDate());
        
        MenstrualLog log;
        if (!existingLogs.isEmpty()) {
            log = existingLogs.get(0);
        } else {
            log = new MenstrualLog();
            log.setMenstrualCycle(cycle);
            log.setLogDate(startOfDay);
            log.setCreatedAt(LocalDateTime.now());
        }

        log.setIsActualPeriod(request.getIsPeriodDay());
        log.setFlowIntensity(request.getIntensity() != null ? mapVietnameseFlowIntensityToEnum(request.getIntensity()) : null);
        
        if (request.getMood() != null && !request.getMood().trim().isEmpty()) {
            MoodType mappedMood = mapMoodValueToEnum(request.getMood());
            if (mappedMood != null) {
                log.setMood(mappedMood);
            } else {
                String currentNotes = log.getNotes() != null ? log.getNotes() + "\n" : "";
                log.setNotes(currentNotes + "Tâm trạng: " + request.getMood());
                log.setMood(null);
            }
        } else {
            log.setMood(null);
        }
        
        log.setNotes(request.getNotes());
        log.setUpdatedAt(LocalDateTime.now());

        log = menstrualLogRepository.save(log);

        if (request.getSymptoms() != null && !request.getSymptoms().trim().isEmpty()) {
            if (log.getId() == null) {
                log = menstrualLogRepository.save(log);
            }
            
            List<SymptomLog> existingSymptoms = symptomLogRepository.findByMenstrualLog(log);
            symptomLogRepository.deleteAll(existingSymptoms);

            String[] symptomValues = request.getSymptoms().split(",");
            for (String symptomValue : symptomValues) {
                symptomValue = symptomValue.trim();
                if (!symptomValue.isEmpty()) {
                    String symptomName = mapSymptomValueToName(symptomValue);
                    
                    Optional<Symptom> symptomOpt = symptomRepository.findBySymptomNameIgnoreCase(symptomName);
                    Symptom symptom;
                    if (symptomOpt.isPresent()) {
                        symptom = symptomOpt.get();
                    } else {
                        symptom = new Symptom();
                        symptom.setSymptomName(symptomName);
                        symptom.setCategory("General");
                        symptom.setIsActive(true);
                        symptom = symptomRepository.save(symptom);
                    }

                    SymptomLog symptomLog = new SymptomLog();
                    symptomLog.setMenstrualLog(log);
                    symptomLog.setSymptom(symptom);
                    symptomLog.setSeverity(SeverityLevel.MODERATE);
                    symptomLog.setCreatedAt(LocalDateTime.now());
                    symptomLogRepository.save(symptomLog);
                }
            }
        }

        DayLogResponseDTO response = new DayLogResponseDTO();
        response.setDate(request.getDate());
        response.setIsPeriodDay(log.getIsActualPeriod());
        response.setIntensity(log.getFlowIntensity() != null ? log.getFlowIntensity().name() : null);
        response.setMood(log.getMood() != null ? log.getMood().name() : null);
        response.setNotes(log.getNotes());

        List<SymptomLog> symptomLogs = symptomLogRepository.findByMenstrualLog(log);
        String symptoms = symptomLogs.stream()
            .map(sl -> sl.getSymptom().getSymptomName() + 
                 (sl.getSeverity() != null ? " (" + sl.getSeverity().name() + ")" : ""))
            .collect(Collectors.joining(", "));
        response.setSymptoms(symptoms);

        MenstrualCycleResponseDTO currentCycle = getCurrentMenstrualCycle(authentication);
        if (currentCycle != null) {
            LocalDate cycleStart = currentCycle.getStartDate();
            LocalDate ovulationDate = currentCycle.getOvulationDate();
            LocalDate fertilityStart = currentCycle.getFertilityWindowStart();
            LocalDate fertilityEnd = currentCycle.getFertilityWindowEnd();

            if (request.getDate().isAfter(cycleStart.minusDays(1)) && 
                request.getDate().isBefore(cycleStart.plusDays(currentCycle.getPeriodDuration()))) {
                response.setPhase("PERIOD");
            } else if (request.getDate().equals(ovulationDate)) {
                response.setPhase("OVULATION");
            } else if (request.getDate().isAfter(fertilityStart.minusDays(1)) && 
                       request.getDate().isBefore(fertilityEnd.plusDays(1))) {
                response.setPhase("FERTILE");
            } else {
                response.setPhase("PREDICTED");
            }
        }

        return response;
    }

    @Override
    @Transactional
    public DayLogResponseDTO quickLog(Authentication authentication, QuickLogRequestDTO request) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        MenstrualCycle cycle = menstrualCycleRepository.findByUser_IdOrderByStartDateDesc(user.getId())
            .stream().max(comparing(MenstrualCycle::getStartDate)).orElse(null);
        
        if (cycle == null) {
            throw new RuntimeException("Chưa có chu kỳ kinh nguyệt");
        }

        LocalDateTime startOfDay = request.getDate().atStartOfDay();
        LocalDateTime endOfDay = request.getDate().atTime(23, 59, 59);
        
        List<MenstrualLog> existingLogs = menstrualLogRepository.findByUserIdAndDateRange(
            user.getId(), request.getDate(), request.getDate());
        
        MenstrualLog log;
        if (!existingLogs.isEmpty()) {
            log = existingLogs.get(0);
        } else {
            log = new MenstrualLog();
            log.setMenstrualCycle(cycle);
            log.setLogDate(startOfDay);
            log.setCreatedAt(LocalDateTime.now());
        }

        switch (request.getType()) {
            case "SYMPTOMS":
                if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
                    if (log.getId() == null) {
                        log = menstrualLogRepository.save(log);
                    }
                    
                    List<SymptomLog> existingSymptoms = symptomLogRepository.findByMenstrualLog(log);
                    symptomLogRepository.deleteAll(existingSymptoms);

                    String[] symptomValues = request.getContent().split(",");
                    for (String symptomValue : symptomValues) {
                        symptomValue = symptomValue.trim();
                        if (!symptomValue.isEmpty()) {
                            String symptomName = mapSymptomValueToName(symptomValue);
                            
                            Optional<Symptom> symptomOpt = symptomRepository.findBySymptomNameIgnoreCase(symptomName);
                            Symptom symptom;
                            if (symptomOpt.isPresent()) {
                                symptom = symptomOpt.get();
                            } else {
                                symptom = new Symptom();
                                symptom.setSymptomName(symptomName);
                                symptom.setCategory("General");
                                symptom.setIsActive(true);
                                symptom = symptomRepository.save(symptom);
                            }
                            SymptomLog symptomLog = new SymptomLog();
                            symptomLog.setMenstrualLog(log);
                            symptomLog.setSymptom(symptom);
                            symptomLog.setSeverity(SeverityLevel.MODERATE);
                            symptomLog.setCreatedAt(LocalDateTime.now());
                            symptomLogRepository.save(symptomLog);
                        }
                    }
                }
                break;
            case "MOOD":
                if (request.getContent() != null && !request.getContent().trim().isEmpty()) {
                    MoodType mappedMood = mapMoodValueToEnum(request.getContent());
                    if (mappedMood != null) {
                        log.setMood(mappedMood);
                    } else {
                        String currentNotes = log.getNotes() != null ? log.getNotes() + "\n" : "";
                        log.setNotes(currentNotes + "Tâm trạng: " + request.getContent());
                        log.setMood(null);
                    }
                }
                break;
            case "NOTES":
                log.setNotes(request.getContent());
                break;
            default:
                throw new RuntimeException("Loại ghi nhận không hợp lệ");
        }

        log.setUpdatedAt(LocalDateTime.now());
        log = menstrualLogRepository.save(log);

        DayLogResponseDTO response = new DayLogResponseDTO();
        response.setDate(request.getDate());
        response.setIsPeriodDay(log.getIsActualPeriod());
        response.setIntensity(log.getFlowIntensity() != null ? log.getFlowIntensity().name() : null);
        response.setMood(log.getMood() != null ? log.getMood().name() : null);
        response.setNotes(log.getNotes());

        List<SymptomLog> symptomLogs = symptomLogRepository.findByMenstrualLog(log);
        String symptoms = symptomLogs.stream()
            .map(sl -> sl.getSymptom().getSymptomName() + 
                 (sl.getSeverity() != null ? " (" + sl.getSeverity().name() + ")" : ""))
            .collect(Collectors.joining(", "));
        response.setSymptoms(symptoms);

        MenstrualCycleResponseDTO currentCycle = getCurrentMenstrualCycle(authentication);
        if (currentCycle != null) {
            LocalDate cycleStart = currentCycle.getStartDate();
            LocalDate ovulationDate = currentCycle.getOvulationDate();
            LocalDate fertilityStart = currentCycle.getFertilityWindowStart();
            LocalDate fertilityEnd = currentCycle.getFertilityWindowEnd();

            if (request.getDate().isAfter(cycleStart.minusDays(1)) && 
                request.getDate().isBefore(cycleStart.plusDays(currentCycle.getPeriodDuration()))) {
                response.setPhase("PERIOD");
            } else if (request.getDate().equals(ovulationDate)) {
                response.setPhase("OVULATION");
            } else if (request.getDate().isAfter(fertilityStart.minusDays(1)) && 
                       request.getDate().isBefore(fertilityEnd.plusDays(1))) {
                response.setPhase("FERTILE");
            } else {
                response.setPhase("PREDICTED");
            }
        }

        return response;
    }

    @Override
    public MenstrualCycleResponseDTO updateMenstrualCycle(Integer userId, MenstrualCycleRequestDTO request) {
        return null;
    }

    @Override
    public void deleteMenstrualCycle(Integer userId, Integer cycleId) {
    }

    private MoodType mapMoodValueToEnum(String moodValue) {
        if (moodValue == null) return null;
        
        try {
            return MoodType.valueOf(moodValue);
        } catch (IllegalArgumentException e) {
            String mood = moodValue.toLowerCase().trim();
            
            switch (mood) {
                case "vui":
                case "hạnh phúc":
                case "happy":
                    return MoodType.HAPPY;
                case "buồn":
                case "bùn":
                case "sad":
                    return MoodType.SAD;
                case "cáu gắt":
                case "irritable":
                    return MoodType.IRRITABLE;
                case "khó chịu":
                case "irritated":
                    return MoodType.IRRITATED;
                case "lo lắng":
                case "anxious":
                    return MoodType.ANXIOUS;
                case "bình tĩnh":
                case "calm":
                    return MoodType.CALM;
                case "năng lượng":
                case "energetic":
                    return MoodType.ENERGETIC;
                case "mệt mỏi":
                case "tired":
                    return MoodType.TIRED;
                case "căng thẳng":
                case "stressed":
                    return MoodType.STRESSED;
                case "xúc động":
                case "emotional":
                    return MoodType.EMOTIONAL;
                case "bình thường":
                case "normal":
                    return MoodType.NORMAL;
                default:
                    return null;
            }
        }
    }

    private FlowIntensity mapVietnameseFlowIntensityToEnum(String vietnameseIntensity) {
        if (vietnameseIntensity == null) return null;
        
        String intensity = vietnameseIntensity.toLowerCase().trim();
        
        switch (intensity) {
            case "rất nhẹ":
            case "spotting":
                return FlowIntensity.SPOTTING;
            case "nhẹ":
            case "light":
                return FlowIntensity.LIGHT;
            case "trung bình":
            case "medium":
                return FlowIntensity.MEDIUM;
            case "nặng":
            case "heavy":
                return FlowIntensity.HEAVY;
            case "rất nặng":
            case "very heavy":
            case "very_heavy":
                return FlowIntensity.VERY_HEAVY;
            default:
                return null;
        }
    }

    private String mapSymptomValueToName(String symptomValue) {
        if (symptomValue == null) return null;
        
        switch (symptomValue) {
            case "CRAMPS":
                return "Đau bụng kinh";
            case "HEADACHE":
                return "Đau đầu";
            case "BACK_PAIN":
                return "Đau lưng";
            case "BREAST_TENDERNESS":
                return "Đau ngực";
            case "BLOATING":
                return "Đầy hơi";
            case "FATIGUE":
                return "Mệt mỏi";
            case "MOOD_SWINGS":
                return "Thay đổi tâm trạng";
            case "FOOD_CRAVINGS":
                return "Thèm ăn";
            case "ACNE":
                return "Mụn";
            case "OTHER":
                return "Khác";
            default:
                return symptomValue;
        }
    }

    @Override
    public String calculatePhaseForDate(Authentication authentication, LocalDate date) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        MenstrualCycleResponseDTO currentCycle = getCurrentMenstrualCycle(authentication);
        if (currentCycle == null) {
            return "UNKNOWN";
        }

        List<MenstrualLog> logs = menstrualLogRepository.findByUserIdAndDateRange(
            user.getId(), date, date);
        if (!logs.isEmpty() && logs.get(0).getIsActualPeriod()) {
            return "PERIOD";
        }

        boolean hasUserLogged = !logs.isEmpty();

        String calculatedPhase = calculateSmartPhase(date, currentCycle, hasUserLogged);
        
        return calculatedPhase;
    }

    private String calculateSmartPhase(LocalDate date, MenstrualCycleResponseDTO currentCycle, boolean hasUserLogged) {
        LocalDate cycleStart = currentCycle.getStartDate();
        int cycleLength = currentCycle.getCycleLength() != null ? currentCycle.getCycleLength() : 28;
        int periodDuration = currentCycle.getPeriodDuration() != null ? currentCycle.getPeriodDuration() : 5;

        // Tính toán cho chu kỳ hiện tại
        LocalDate currentPeriodEnd = cycleStart.plusDays(periodDuration - 1);
        LocalDate currentOvulation = cycleStart.plusDays(cycleLength - 14);
        LocalDate currentFertileStart = currentOvulation.minusDays(5);
        LocalDate currentFertileEnd = currentOvulation.plusDays(1);

        // Tính toán cho chu kỳ tiếp theo
        LocalDate nextCycleStart = cycleStart.plusDays(cycleLength);
        LocalDate nextPeriodEnd = nextCycleStart.plusDays(periodDuration - 1);
        LocalDate nextOvulation = nextCycleStart.plusDays(cycleLength - 14);
        LocalDate nextFertileStart = nextOvulation.minusDays(5);
        LocalDate nextFertileEnd = nextOvulation.plusDays(1);

        // Tính toán cho chu kỳ trước đó (nếu cần)
        LocalDate prevCycleStart = cycleStart.minusDays(cycleLength);
        LocalDate prevPeriodEnd = prevCycleStart.plusDays(periodDuration - 1);
        LocalDate prevOvulation = prevCycleStart.plusDays(cycleLength - 14);
        LocalDate prevFertileStart = prevOvulation.minusDays(5);
        LocalDate prevFertileEnd = prevOvulation.plusDays(1);

        // Kiểm tra chu kỳ hiện tại
        if (!date.isBefore(cycleStart) && !date.isAfter(currentPeriodEnd)) {
            return hasUserLogged ? "PERIOD" : "PREDICTED";
        }
        if (date.equals(currentOvulation)) {
            return "OVULATION";
        }
        if (!date.isBefore(currentFertileStart) && !date.isAfter(currentFertileEnd)) {
            return "FERTILE";
        }

        // Kiểm tra chu kỳ tiếp theo
        if (!date.isBefore(nextCycleStart) && !date.isAfter(nextPeriodEnd)) {
            return "PREDICTED";
        }
        if (date.equals(nextOvulation)) {
            return "OVULATION";
        }
        if (!date.isBefore(nextFertileStart) && !date.isAfter(nextFertileEnd)) {
            return "FERTILE";
        }

        // Kiểm tra chu kỳ trước đó
        if (!date.isBefore(prevCycleStart) && !date.isAfter(prevPeriodEnd)) {
            return hasUserLogged ? "PERIOD" : "PREDICTED";
        }
        if (date.equals(prevOvulation)) {
            return "OVULATION";
        }
        if (!date.isBefore(prevFertileStart) && !date.isAfter(prevFertileEnd)) {
            return "FERTILE";
        }

        return "NORMAL";
    }

    private List<LocalDate> calculatePredictedPeriodStarts(LocalDate lastPeriodStart, int cycleLength) {
        List<LocalDate> predictedStarts = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            LocalDate nextPeriodStart = lastPeriodStart.plusDays(cycleLength * i);
            predictedStarts.add(nextPeriodStart);
        }
        
        return predictedStarts;
    }

    private List<LocalDate> calculateOvulationDates(LocalDate lastPeriodStart, int cycleLength) {
        List<LocalDate> ovulationDates = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            LocalDate nextPeriodStart = lastPeriodStart.plusDays(cycleLength * i);
            LocalDate ovulationDate = nextPeriodStart.minusDays(14);
            ovulationDates.add(ovulationDate);
        }
        
        return ovulationDates;
    }

    private List<LocalDate> calculateFertilityWindows(List<LocalDate> ovulationDates) {
        List<LocalDate> fertilityDates = new ArrayList<>();
        
        for (LocalDate ovulationDate : ovulationDates) {
            for (int i = 5; i >= 1; i--) {
                fertilityDates.add(ovulationDate.minusDays(i));
            }
            fertilityDates.add(ovulationDate);
            fertilityDates.add(ovulationDate.plusDays(1));
        }
        
        return fertilityDates;
    }

    private boolean isDateInPredictedPeriod(LocalDate date, List<LocalDate> predictedStarts, int periodDuration) {
        for (LocalDate periodStart : predictedStarts) {
            if (date.isAfter(periodStart.minusDays(1)) && 
                date.isBefore(periodStart.plusDays(periodDuration))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Map<LocalDate, String> calculatePhasesForMonth(Authentication authentication, int year, int month) {
        Map<LocalDate, String> phases = new HashMap<>();
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        LocalDate currentDate = startOfMonth;
        while (!currentDate.isAfter(endOfMonth)) {
            String phase = calculatePhaseForDate(authentication, currentDate);
            phases.put(currentDate, phase);
            currentDate = currentDate.plusDays(1);
        }
        
        return phases;
    }

    @Override
    public Map<LocalDate, PhaseInfoDTO> calculateDetailedPhasesForMonth(Authentication authentication, int year, int month) {
        Map<LocalDate, PhaseInfoDTO> detailedPhases = new HashMap<>();
        
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        LocalDate currentDate = startOfMonth;
        while (!currentDate.isAfter(endOfMonth)) {
            String phase = calculatePhaseForDate(authentication, currentDate);
            PhaseInfoDTO phaseInfo = getPhaseInfo(phase);
            detailedPhases.put(currentDate, phaseInfo);
            currentDate = currentDate.plusDays(1);
        }
        
        return detailedPhases;
    }

    private PhaseInfoDTO getPhaseInfo(String phase) {
        switch (phase) {
            case "PERIOD":
                return new PhaseInfoDTO(
                    "PERIOD",
                    "🩸",
                    "#ff6b6b",
                    "Kỳ kinh thực tế - cơ thể loại bỏ lớp niêm mạc tử cung. Đây là dữ liệu bạn đã ghi nhận.",
                    "Kỳ kinh"
                );
            case "OVULATION":
                return new PhaseInfoDTO(
                    "OVULATION",
                    "⭐",
                    "#568392",
                    "Ngày rụng trứng - trứng được giải phóng từ buồng trứng, khả năng thụ thai cao nhất.",
                    "Rụng trứng"
                );
            case "FERTILE":
                return new PhaseInfoDTO(
                    "FERTILE",
                    "🌸",
                    "#ffa726",
                    "Thời kỳ màu mỡ - khoảng thời gian có khả năng thụ thai cao (5 ngày trước và 1 ngày sau rụng trứng).",
                    "Thời kỳ màu mỡ"
                );
            case "PREDICTED":
                return new PhaseInfoDTO(
                    "PREDICTED",
                    "📅",
                    "#f8bbd9",
                    "Kỳ kinh dự đoán - dựa trên chu kỳ trước và độ dài chu kỳ trung bình của bạn.",
                    "Kỳ kinh dự đoán"
                );
            case "NORMAL":
                return new PhaseInfoDTO(
                    "NORMAL",
                    "",
                    "#ffffff",
                    "Ngày bình thường - không có phase đặc biệt nào.",
                    "Bình thường"
                );
            default:
                return new PhaseInfoDTO(
                    "UNKNOWN",
                    "❓",
                    "#cccccc",
                    "Không xác định được phase cho ngày này.",
                    "Không xác định"
                );
        }
    }

    @Override
    public List<MenstrualLog> getLogsForDateRange(Authentication authentication, LocalDate startDate, LocalDate endDate) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        return menstrualLogRepository.findByUserIdAndDateRange(user.getId(), startDate, endDate);
    }

    @Override
    @Transactional
    public MenstrualCycleResponseDTO updateCycleSettings(Authentication authentication, UpdateCycleSettingsRequestDTO request) {
        String username = authentication.getName();
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng");
        }

        if (request.getCycleLength() != null && (request.getCycleLength() < 20 || request.getCycleLength() > 40)) {
            throw new RuntimeException("Độ dài chu kỳ phải từ 20-40 ngày");
        }
        if (request.getPeriodDuration() != null && (request.getPeriodDuration() < 3 || request.getPeriodDuration() > 10)) {
            throw new RuntimeException("Số ngày kinh phải từ 3-10 ngày");
        }

        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUser_IdOrderByStartDateDesc(user.getId());
        if (cycles == null || cycles.isEmpty()) {
            throw new RuntimeException("Bạn chưa có chu kỳ nào trong hệ thống");
        }

        MenstrualCycle currentCycle = cycles.stream()
                .max(comparing(MenstrualCycle::getStartDate))
                .orElse(null);

        if (currentCycle == null) {
            throw new RuntimeException("Không tìm thấy chu kỳ hiện tại");
        }

        if (request.getCycleLength() != null) {
            currentCycle.setCycleLength(request.getCycleLength());
        }
        if (request.getPeriodDuration() != null) {
            currentCycle.setPeriodDuration(request.getPeriodDuration());
        }

        MenstrualCycle updatedCycle = menstrualCycleRepository.save(currentCycle);
        
        return modelMapper.map(updatedCycle, MenstrualCycleResponseDTO.class);
    }
}