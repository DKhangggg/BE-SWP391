package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultantRepository extends JpaRepository<Consultant,Integer> {
    @Query("SELECT c FROM Consultant c WHERE c.user.id = ?1")
    Consultant findByUserId(Integer userid);
    
    @Query("SELECT c FROM Consultant c JOIN FETCH c.user u WHERE c.isDeleted = false ORDER BY c.experienceYears DESC, u.fullName ASC")
    List<Consultant> findFeaturedConsultants();
    
    @Query(value = "SELECT TOP(3) c.ConsultantID FROM Consultants c INNER JOIN Users u ON c.ConsultantID = u.UserID WHERE c.IsDeleted = 0 ORDER BY c.ExperienceYears DESC, u.FullName ASC", nativeQuery = true)
    List<Integer> findFeaturedConsultantIds();
}
