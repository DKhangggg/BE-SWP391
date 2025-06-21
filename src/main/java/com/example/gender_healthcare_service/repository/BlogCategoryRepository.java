package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {
}

