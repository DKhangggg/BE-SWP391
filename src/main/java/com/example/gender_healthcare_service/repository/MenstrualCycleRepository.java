package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.MenstrualCycle;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MenstrualCycleRepository extends JpaRepository<MenstrualCycle, Integer> {
    List<MenstrualCycle> findByUser_IdOrderByStartDateDesc(Integer userId);
}
