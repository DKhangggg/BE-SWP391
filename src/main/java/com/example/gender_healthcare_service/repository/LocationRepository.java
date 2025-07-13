package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Integer> {
    List<Location> findByIsDeletedFalse();
} 