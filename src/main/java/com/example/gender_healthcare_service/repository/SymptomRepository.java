package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SymptomRepository extends JpaRepository<Symptom, Integer> {
    List<Symptom> findByIsActiveTrue();
    Optional<Symptom> findBySymptomNameIgnoreCase(String symptomName);

    @Query("SELECT s FROM Symptom s WHERE s.category = :category AND s.isActive = true")
    List<Symptom> findByCategoryAndIsActiveTrue(@Param("category") String category);

    @Query("SELECT DISTINCT s.category FROM Symptom s WHERE s.isActive = true")
    List<String> findDistinctCategories();
}
