package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Integer> {
    List<MenstrualCycle> findByUser(User user);
    // Xóa method trả về 1 bản ghi duy nhất
    // MenstrualCycle findByUserId(Integer userId);

    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.id = :userId AND mc.startDate BETWEEN :startDate AND :endDate ORDER BY mc.startDate DESC")
    List<MenstrualCycle> findByUserIdAndDateRange(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.id = :userId ORDER BY mc.startDate DESC")
    List<MenstrualCycle> findByUserIdOrderByStartDateDesc(@Param("userId") Integer userId);

    @Query("SELECT AVG(mc.cycleLength) FROM MenstrualCycle mc WHERE mc.user.id = :userId AND mc.cycleLength IS NOT NULL")
    Double calculateAverageCycleLength(@Param("userId") Integer userId);

    @Query("SELECT AVG(mc.periodDuration) FROM MenstrualCycle mc WHERE mc.user.id = :userId AND mc.periodDuration IS NOT NULL")
    Double calculateAveragePeriodDuration(@Param("userId") Integer userId);

    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.id = :userId AND mc.nextPredictedPeriod BETWEEN :startDate AND :endDate")
    List<MenstrualCycle> findUpcomingPeriods(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT mc FROM MenstrualCycle mc WHERE mc.user.id = :userId AND mc.fertilityWindowStart <= :date AND mc.fertilityWindowEnd >= :date")
    Optional<MenstrualCycle> findActiveFertilityWindow(@Param("userId") Integer userId, @Param("date") LocalDate date);
}
