package com.example.gender_healthcare_service.service;

import org.springframework.security.core.Authentication;

public interface JwtService {
      String generateToken(Authentication authentication);
     String generateRefreshToken(Authentication authentication);
     String getUserNameFromJWT(String token);
     boolean validateToken(String token);
}
