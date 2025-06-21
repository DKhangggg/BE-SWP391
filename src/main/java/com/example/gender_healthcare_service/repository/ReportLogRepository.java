package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.ReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportLogRepository extends JpaRepository<ReportLog, Integer> {
}

