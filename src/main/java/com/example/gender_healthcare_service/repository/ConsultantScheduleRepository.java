package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.ConsultantSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ConsultantScheduleRepository extends JpaRepository<ConsultantSchedule, Integer> {

    List<ConsultantSchedule> findByConsultantIdAndScheduleDateBetween(Integer consultantId, LocalDate startDate, LocalDate endDate);

    // List<ConsultantSchedule> findByConsultantAndScheduleDateBetween(Consultant consultant, LocalDate startDate, LocalDate endDate);
}
