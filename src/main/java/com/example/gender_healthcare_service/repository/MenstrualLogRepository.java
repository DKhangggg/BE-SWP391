package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.MenstrualLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenstrualLogRepository extends JpaRepository<MenstrualLog, Integer> {
    List<MenstrualLog> findByMenstrualCycle(MenstrualCycle menstrualCycle);
}
