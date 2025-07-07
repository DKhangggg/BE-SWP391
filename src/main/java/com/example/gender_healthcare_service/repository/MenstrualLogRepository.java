package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MenstrualLogRepository extends JpaRepository<MenstrualLog, Integer> {
    List<MenstrualLog> findByMenstrualCycle(MenstrualCycle menstrualCycle);

    @Query("SELECT ml FROM MenstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.logDate BETWEEN :startDate AND :endDate ORDER BY ml.logDate DESC")
    List<MenstrualLog> findByUserIdAndDateRange(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ml FROM MenstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.isActualPeriod = true ORDER BY ml.logDate DESC")
    List<MenstrualLog> findPeriodDaysByUserId(@Param("userId") Integer userId);

    @Query("SELECT ml FROM MenstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.mood IS NOT NULL ORDER BY ml.logDate DESC")
    List<MenstrualLog> findMoodLogsByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(ml) FROM MenstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.isActualPeriod = true AND ml.logDate BETWEEN :startDate AND :endDate")
    Long countPeriodDaysInRange(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ml.mood, COUNT(ml) FROM MenstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.mood IS NOT NULL GROUP BY ml.mood")
    List<Object[]> findMoodDistributionByUserId(@Param("userId") Integer userId);
}
