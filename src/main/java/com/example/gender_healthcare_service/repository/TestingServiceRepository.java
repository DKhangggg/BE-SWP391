package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.TestingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestingServiceRepository extends JpaRepository<TestingService, Integer> {
    // Find all active (non-deleted) services
    @Query("SELECT s FROM TestingService s WHERE s.isDeleted = false")
    List<TestingService> findAllActive();

    // Find active service by ID
    @Query("SELECT s FROM TestingService s WHERE s.id = ?1 AND s.isDeleted = false")
    Optional<TestingService> findActiveById(Integer id);

    List<TestingService> findByServiceNameContainingIgnoreCaseAndIsDeletedFalse(String name);

    List<TestingService> findByPriceBetweenAndIsDeletedFalse(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);

    @Transactional
    @Modifying
    @Query("UPDATE TestingService s SET s.isDeleted = :isDeleted WHERE s.id = :id")
    void updateIsDeletedById(Integer id, boolean isDeleted);

}
