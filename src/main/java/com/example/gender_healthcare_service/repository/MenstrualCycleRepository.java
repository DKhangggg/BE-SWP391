package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Integer> {
    Optional<MenstrualCycle> findByUserId(Integer userId);
}
