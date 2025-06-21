package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant,Integer> {
}
