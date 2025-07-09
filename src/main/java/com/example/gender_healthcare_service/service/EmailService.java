package com.example.gender_healthcare_service.service;

public interface EmailService {
     String OtpMail(String email, String otp);
     String resetPasswordEmail(String email, String otp);
     String forgotPasswordEmail(String email, String otp);
     String welcomeEmail(String email, String userName);
     String sendOTPEmail(String email, String userName, String otpCode, String validationLink);
}
