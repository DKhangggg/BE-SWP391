package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.LoginRequest;
import com.example.gender_healthcare_service.dto.request.RegisterRequest;
import com.example.gender_healthcare_service.dto.request.SocialLoginRequestDTO;
import com.example.gender_healthcare_service.dto.request.OTPRequestDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
     ResponseEntity<?> registerUser(RegisterRequest RegisterUser);
     ResponseEntity<?> loginUser(LoginRequest loginRequest);
     void setConsultantUser(Integer Userid);
     UserDetails loadUserByUsername(String username);
     ResponseEntity<?> refreshAccessToken(String refreshToken);
     ResponseEntity<?> sendResetPasswordEmail(String email);
     ResponseEntity<?> validateOtp(String email, String otp);
     ResponseEntity<?> resetPassword(OTPRequestDTO otpRequest);
     UserResponseDTO findUserById(Integer userId);
     ResponseEntity<?> loginByGoogle(SocialLoginRequestDTO requestDTO);
     boolean isUserExists(Integer userId);
}
