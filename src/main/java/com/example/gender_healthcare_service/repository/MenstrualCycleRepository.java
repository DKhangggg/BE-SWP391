package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Integer> {
    List<MenstrualCycle> findByUser(User user);
    MenstrualCycle findByUserId(Integer userId);
}
