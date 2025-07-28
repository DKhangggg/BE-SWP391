package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Integer> {

    BlogCategory findByCategoryName(String categoryName);

    Optional<BlogCategory> findBySlug(String slug);

    @Query("SELECT c FROM BlogCategory c WHERE c.isDeleted = false")
    List<BlogCategory> findAllActive();

    @Query("SELECT c FROM BlogCategory c WHERE c.categoryID = :id AND c.isDeleted = false")
    Optional<BlogCategory> findByIdAndIsDeletedFalse(@Param("id") Integer id);
}
