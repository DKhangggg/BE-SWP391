package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    void deleteByUser(com.example.gender_healthcare_service.entity.User user); // To delete tokens once used or when a new one is requested
}

