package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.LoginRequest;
import com.example.gender_healthcare_service.dto.request.RegisterRequest;
import com.example.gender_healthcare_service.dto.request.SocialLoginRequestDTO;
import com.example.gender_healthcare_service.dto.response.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationService {
    public ResponseEntity<?> registerUser(RegisterRequest RegisterUser);
    public ResponseEntity<?> loginUser(LoginRequest loginRequest);
    public void setConsultantUser(Integer Userid);
    public UserDetails loadUserByUsername(String username);
    public ResponseEntity<?> refreshAccessToken(String refreshToken);
    public ResponseEntity<?> handleForgotPassword(String email);
    public ResponseEntity<?> handleResetPassword(String token, String newPassword);
    public UserResponseDTO findUserById(Integer userId);
    public ResponseEntity<?> loginByGoogle(SocialLoginRequestDTO requestDTO);
}
