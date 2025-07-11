package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import com.example.gender_healthcare_service.entity.ConsultantUnavailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ConsultantUnavailabilityRepository extends JpaRepository<ConsultantUnavailability, Integer> {
    List<ConsultantUnavailability> findByConsultant(Consultant consultant);

    List<ConsultantUnavailability> findByConsultantAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
            Consultant consultant, LocalDateTime endDate, LocalDateTime startDate);

    @Query("SELECT cu FROM ConsultantUnavailability cu WHERE cu.consultant = :consultant " +
            "AND :date BETWEEN cu.startTime AND cu.endTime")
    List<ConsultantUnavailability> findByConsultantAndDateBetweenStartAndEndTime(
            @Param("consultant") Consultant consultant,
            @Param("date") LocalDate date);
}
