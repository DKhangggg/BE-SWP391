package com.example.gender_healthcare_service.repository;

import com.example.gender_healthcare_service.entity.PasswordResetOTP;
import com.example.gender_healthcare_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetOTPRepository extends JpaRepository<PasswordResetOTP, Long> {
    PasswordResetOTP findByOtpCodeAndUser_Email(String otpCode, String email);
    void deleteByUser(User user);
}
