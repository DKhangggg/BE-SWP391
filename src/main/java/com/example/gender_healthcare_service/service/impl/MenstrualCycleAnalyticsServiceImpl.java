package com.example.gender_healthcare_service.service.impl;

import com.example.gender_healthcare_service.dto.response.*;
import com.example.gender_healthcare_service.entity.*;
import com.example.gender_healthcare_service.entity.enumpackage.MoodType;
import com.example.gender_healthcare_service.repository.*;
import com.example.gender_healthcare_service.service.MenstrualCycleAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenstrualCycleAnalyticsServiceImpl implements MenstrualCycleAnalyticsService {

    @Autowired
    private MenstrualCycleRepository menstrualCycleRepository;

    @Autowired
    private MenstrualLogRepository menstrualLogRepository;

    @Autowired
    private SymptomLogRepository symptomLogRepository;

    private static final int MINIMUM_CYCLES_FOR_PREDICTION = 3;
    private static final int IRREGULAR_CYCLE_THRESHOLD = 7; // days variance

    @Override
    public PeriodPredictionDTO predictNextPeriod(Integer userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserIdOrderByStartDateDesc(userId);

        if (cycles.size() < MINIMUM_CYCLES_FOR_PREDICTION) {
            return createBasicPrediction(cycles.isEmpty() ? null : cycles.get(0));
        }

        Double weightedAverage = calculateWeightedAverageCycleLength(userId);
        Integer avgPeriodDuration = calculateAveragePeriodDuration(cycles);

        MenstrualCycle lastCycle = cycles.get(0);
        LocalDate nextPeriodDate = lastCycle.getStartDate().plusDays(weightedAverage.intValue());
        LocalDate periodEndDate = nextPeriodDate.plusDays(avgPeriodDuration - 1);

        Double confidence = calculatePredictionConfidence(cycles);

        return new PeriodPredictionDTO(
            nextPeriodDate,
            periodEndDate,
            confidence,
            "Prediction based on historical data",
            weightedAverage.intValue(),
            avgPeriodDuration,
            "Reliability based on cycle history"
        );
    }

    @Override
    public FertilityWindowDTO calculateFertilityWindow(Integer userId) {
        PeriodPredictionDTO nextPeriod = predictNextPeriod(userId);

        if (nextPeriod == null || nextPeriod.getNextPeriodDate() == null) {
            return new FertilityWindowDTO(null, null, null, 0.0, "LOW", "Insufficient data for fertility calculation");
        }

        // Ovulation typically occurs 14 days before next period
        LocalDate ovulationDate = nextPeriod.getNextPeriodDate().minusDays(14);
        LocalDate fertileStart = ovulationDate.minusDays(5); // Sperm can survive 5 days
        LocalDate fertileEnd = ovulationDate.plusDays(1); // Egg survives 1 day

        String fertilityStatus = determineFertilityStatus(ovulationDate);

        return new FertilityWindowDTO(
            fertileStart,
            fertileEnd,
            ovulationDate,
            nextPeriod.getConfidence(),
            fertilityStatus,
            "Fertility window based on predicted ovulation"
        );
    }

    @Override
    public CycleAnalyticsDTO analyzeCyclePatterns(Integer userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserIdOrderByStartDateDesc(userId);

        if (cycles.isEmpty()) {
            return new CycleAnalyticsDTO(null, null, null, null, 0, "INSUFFICIENT_DATA",
                Arrays.asList("Start tracking your cycles to get insights"), new HashMap<>(), new HashMap<>(), new ArrayList<>());
        }

        Double avgCycleLength = calculateAverageCycleLength(cycles);
        Double avgPeriodDuration = calculateAveragePeriodDuration(cycles).doubleValue();
        Double variability = calculateCycleVariability(cycles);
        Boolean isRegular = variability != null && variability <= IRREGULAR_CYCLE_THRESHOLD;

        String regularityStatus = determineRegularityStatus(cycles.size(), variability);
        List<String> trends = analyzeTrends(cycles);
        Map<String, Double> moodPatterns = analyzeMoodPatterns(userId);
        Map<String, Integer> symptomFrequency = analyzeSymptomFrequency(userId);
        List<String> recommendations = generateRecommendations(cycles, isRegular, moodPatterns);

        return new CycleAnalyticsDTO(
            avgCycleLength, avgPeriodDuration, isRegular, variability, cycles.size(),
            regularityStatus, trends, moodPatterns, symptomFrequency, recommendations
        );
    }

    @Override
    public List<SymptomPatternDTO> analyzeSymptomPatterns(Integer userId) {
        List<SymptomLog> symptomLogs = symptomLogRepository.findByUserIdAndDateRange(
            userId, LocalDateTime.now().minusMonths(6), LocalDateTime.now()
        );

        Map<Symptom, List<SymptomLog>> groupedBySymptom = symptomLogs.stream()
            .collect(Collectors.groupingBy(SymptomLog::getSymptom));

        return groupedBySymptom.entrySet().stream()
            .map(entry -> analyzeSymptomPattern(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
    }

    @Override
    public void updateCycleStatistics(Integer userId) {
        Double avgCycleLength = menstrualCycleRepository.calculateAverageCycleLength(userId);
        Double avgPeriodDuration = menstrualCycleRepository.calculateAveragePeriodDuration(userId);

        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserIdOrderByStartDateDesc(userId);
        MenstrualCycle currentCycle = (cycles != null && !cycles.isEmpty()) ? cycles.get(0) : null;
        if (currentCycle != null) {
            currentCycle.setAverageCycleLength(avgCycleLength);
            currentCycle.setAveragePeriodDuration(avgPeriodDuration);
            currentCycle.setIsRegular(!isIrregularCycle(userId));
            menstrualCycleRepository.save(currentCycle);
        }
    }

    @Override
    public boolean isIrregularCycle(Integer userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserIdOrderByStartDateDesc(userId);
        if (cycles.size() < 3) return false;

        Double variability = calculateCycleVariability(cycles);
        return variability != null && variability > IRREGULAR_CYCLE_THRESHOLD;
    }

    @Override
    public List<String> getHealthInsights(Integer userId) {
        List<String> insights = new ArrayList<>();
        CycleAnalyticsDTO analytics = analyzeCyclePatterns(userId);

        if (analytics.getTotalCyclesTracked() < 3) {
            insights.add("Track at least 3 cycles for accurate predictions and insights.");
        }

        if (Boolean.FALSE.equals(analytics.getIsRegular())) {
            insights.add("Your cycles show irregularity. Consider consulting a healthcare provider.");
        }

        if (analytics.getAverageCycleLength() != null) {
            if (analytics.getAverageCycleLength() < 21) {
                insights.add("Your cycles are shorter than average. This might indicate hormonal changes.");
            } else if (analytics.getAverageCycleLength() > 35) {
                insights.add("Your cycles are longer than average. Consider tracking ovulation signs.");
            }
        }

        return insights;
    }

    @Override
    public Double calculateWeightedAverageCycleLength(Integer userId) {
        List<MenstrualCycle> cycles = menstrualCycleRepository.findByUserIdOrderByStartDateDesc(userId);

        if (cycles.size() < 2) return 28.0; // Default cycle length

        double weightedSum = 0;
        double totalWeight = 0;

        for (int i = 0; i < Math.min(cycles.size() - 1, 6); i++) { // Use last 6 cycles
            MenstrualCycle current = cycles.get(i);
            MenstrualCycle next = cycles.get(i + 1);

            long cycleLength = ChronoUnit.DAYS.between(next.getStartDate(), current.getStartDate());
            double weight = Math.pow(0.8, i); // Recent cycles have higher weight

            weightedSum += cycleLength * weight;
            totalWeight += weight;
        }

        return totalWeight > 0 ? weightedSum / totalWeight : 28.0;
    }

    // Helper methods
    private PeriodPredictionDTO createBasicPrediction(MenstrualCycle lastCycle) {
        LocalDate nextPeriodDate = lastCycle != null ?
            lastCycle.getStartDate().plusDays(28) : LocalDate.now().plusDays(28);

        return new PeriodPredictionDTO(
            nextPeriodDate,
            nextPeriodDate.plusDays(5),
            0.5,
            "default",
            28,
            5,
            "Insufficient data - using average values"
        );
    }

    private Double calculatePredictionConfidence(List<MenstrualCycle> cycles) {
        if (cycles.size() < 3) return 0.3;

        Double variability = calculateCycleVariability(cycles);
        if (variability == null) return 0.5;

        // Higher confidence for more regular cycles
        if (variability <= 3) return 0.9;
        if (variability <= 5) return 0.8;
        if (variability <= 7) return 0.7;
        return 0.5;
    }

    private String determineFertilityStatus(LocalDate ovulationDate) {
        LocalDate today = LocalDate.now();
        long daysToOvulation = ChronoUnit.DAYS.between(today, ovulationDate);

        if (Math.abs(daysToOvulation) <= 1) return "HIGH";
        if (Math.abs(daysToOvulation) <= 3) return "MEDIUM";
        return "LOW";
    }

    private Double calculateAverageCycleLength(List<MenstrualCycle> cycles) {
        if (cycles.size() < 2) return null;

        List<Long> cycleLengths = new ArrayList<>();
        for (int i = 0; i < cycles.size() - 1; i++) {
            long length = ChronoUnit.DAYS.between(cycles.get(i + 1).getStartDate(), cycles.get(i).getStartDate());
            cycleLengths.add(length);
        }

        return cycleLengths.stream().mapToLong(Long::longValue).average().orElse(28.0);
    }

    private Integer calculateAveragePeriodDuration(List<MenstrualCycle> cycles) {
        OptionalDouble average = cycles.stream()
            .filter(c -> c.getPeriodDuration() != null)
            .mapToInt(MenstrualCycle::getPeriodDuration)
            .average();

        return (int) average.orElse(5.0); // Cast double to int
    }

    private Double calculateCycleVariability(List<MenstrualCycle> cycles) {
        if (cycles.size() < 3) return null;

        List<Long> cycleLengths = new ArrayList<>();
        for (int i = 0; i < cycles.size() - 1; i++) {
            long length = ChronoUnit.DAYS.between(cycles.get(i + 1).getStartDate(), cycles.get(i).getStartDate());
            cycleLengths.add(length);
        }

        double mean = cycleLengths.stream().mapToLong(Long::longValue).average().orElse(0);
        double variance = cycleLengths.stream()
            .mapToDouble(length -> Math.pow(length - mean, 2))
            .average().orElse(0);

        return Math.sqrt(variance);
    }

    private String determineRegularityStatus(int cycleCount, Double variability) {
        if (cycleCount < 3) return "INSUFFICIENT_DATA";
        if (variability == null) return "INSUFFICIENT_DATA";
        return variability <= IRREGULAR_CYCLE_THRESHOLD ? "REGULAR" : "IRREGULAR";
    }

    private List<String> analyzeTrends(List<MenstrualCycle> cycles) {
        List<String> trends = new ArrayList<>();

        if (cycles.size() < 3) {
            trends.add("Need more cycles to identify trends");
            return trends;
        }

        // Analyze cycle length trend
        List<Long> recentLengths = new ArrayList<>();
        for (int i = 0; i < Math.min(3, cycles.size() - 1); i++) {
            long length = ChronoUnit.DAYS.between(cycles.get(i + 1).getStartDate(), cycles.get(i).getStartDate());
            recentLengths.add(length);
        }

        if (recentLengths.size() >= 2) {
            double avgRecent = recentLengths.subList(0, Math.min(2, recentLengths.size())).stream().mapToLong(Long::longValue).average().orElse(0);
            double avgOlder = recentLengths.subList(Math.min(2, recentLengths.size()), recentLengths.size()).stream().mapToLong(Long::longValue).average().orElse(0);

            if (avgRecent > avgOlder + 2) {
                trends.add("Cycles are getting longer");
            } else if (avgRecent < avgOlder - 2) {
                trends.add("Cycles are getting shorter");
            } else {
                trends.add("Cycle length is stable");
            }
        }

        return trends;
    }

    private Map<String, Double> analyzeMoodPatterns(Integer userId) {
        try {
            List<MenstrualLog> moodLogs = menstrualLogRepository.findMoodLogsByUserId(userId);

            Map<MoodType, Long> moodCounts = moodLogs.stream()
                .filter(log -> log.getMood() != null)
                .collect(Collectors.groupingBy(MenstrualLog::getMood, Collectors.counting()));

            long total = moodCounts.values().stream().mapToLong(Long::longValue).sum();

            return moodCounts.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> entry.getKey().name(),
                    entry -> (double) entry.getValue() / total * 100
                ));
        } catch (Exception e) {
            // Log error và trả về map rỗng nếu có lỗi enum
            System.err.println("Error analyzing mood patterns: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Integer> analyzeSymptomFrequency(Integer userId) {
        List<Object[]> symptomFrequency = symptomLogRepository.findMostCommonSymptomsByUserId(userId);

        return symptomFrequency.stream()
            .collect(Collectors.toMap(
                row -> ((Symptom) row[0]).getSymptomName(),
                row -> ((Long) row[1]).intValue()
            ));
    }

    private List<String> generateRecommendations(List<MenstrualCycle> cycles, Boolean isRegular, Map<String, Double> moodPatterns) {
        List<String> recommendations = new ArrayList<>();

        if (Boolean.FALSE.equals(isRegular)) {
            recommendations.add("Consider lifestyle changes: regular exercise, stress management, and consistent sleep schedule");
            recommendations.add("Track additional symptoms to identify patterns");
        }

        if (moodPatterns.containsKey("STRESSED") && moodPatterns.get("STRESSED") > 30) {
            recommendations.add("High stress levels detected. Consider relaxation techniques or counseling");
        }

        if (cycles.size() < 6) {
            recommendations.add("Continue tracking for better insights and predictions");
        }

        recommendations.add("Stay hydrated and maintain a balanced diet rich in iron and vitamins");

        return recommendations;
    }

    private SymptomPatternDTO analyzeSymptomPattern(Symptom symptom, List<SymptomLog> logs) {
        int frequency = logs.size();
        double avgSeverity = logs.stream()
            .filter(log -> log.getSeverity() != null)
            .mapToInt(log -> log.getSeverity().ordinal() + 1)
            .average().orElse(0.0);

        List<LocalDate> occurrenceDates = logs.stream()
            .map(log -> log.getMenstrualLog().getLogDate().toLocalDate())
            .sorted()
            .collect(Collectors.toList());

        String pattern = analyzeTemporalPattern(occurrenceDates);
        String cyclePhase = determineCyclePhase(logs);

        return new SymptomPatternDTO(
            symptom.getSymptomName(),
            symptom.getCategory(),
            frequency,
            avgSeverity,
            occurrenceDates,
            pattern,
            cyclePhase,
            "Analysis based on recent data"
        );
    }

    private String analyzeTemporalPattern(List<LocalDate> dates) {
        if (dates.size() < 3) return "Insufficient data";

        // Simple pattern analysis - could be enhanced with more sophisticated algorithms
        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < dates.size(); i++) {
            intervals.add(ChronoUnit.DAYS.between(dates.get(i - 1), dates.get(i)));
        }

        double avgInterval = intervals.stream().mapToLong(Long::longValue).average().orElse(0);

        if (avgInterval < 10) return "Frequent occurrence";
        if (avgInterval > 25 && avgInterval < 35) return "Monthly pattern";
        return "Irregular pattern";
    }

    private String determineCyclePhase(List<SymptomLog> logs) {
        // This would require more sophisticated analysis based on cycle dates
        // For now, return a placeholder
        return "VARIOUS";
    }
}
