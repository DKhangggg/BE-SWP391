package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultantUnavailabilityRepository extends JpaRepository<ConsultantUnavailability, Long> {
    List<ConsultantUnavailability> findByConsultant(Consultant consultant);
    List<ConsultantUnavailability> findByConsultantAndDate(Consultant consultant, LocalDate date);
}

