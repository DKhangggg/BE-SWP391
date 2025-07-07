package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.SymptomLog;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import com.example.gender_healthcare_service.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SymptomLogRepository extends JpaRepository<SymptomLog, Integer> {
    List<SymptomLog> findByMenstrualLog(MenstrualLog menstrualLog);
    List<SymptomLog> findBySymptom(Symptom symptom);

    @Query("SELECT sl FROM SymptomLog sl JOIN sl.menstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND ml.logDate BETWEEN :startDate AND :endDate")
    List<SymptomLog> findByUserIdAndDateRange(@Param("userId") Integer userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT sl.symptom, COUNT(sl) FROM SymptomLog sl JOIN sl.menstrualLog ml WHERE ml.menstrualCycle.user.id = :userId GROUP BY sl.symptom ORDER BY COUNT(sl) DESC")
    List<Object[]> findMostCommonSymptomsByUserId(@Param("userId") Integer userId);

    @Query("SELECT sl FROM SymptomLog sl JOIN sl.menstrualLog ml WHERE ml.menstrualCycle.user.id = :userId AND sl.symptom.id = :symptomId ORDER BY ml.logDate DESC")
    List<SymptomLog> findByUserIdAndSymptomId(@Param("userId") Integer userId, @Param("symptomId") Integer symptomId);
}
