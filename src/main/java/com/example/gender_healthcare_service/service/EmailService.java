package com.example.gender_healthcare_service.service;

public interface EmailService {
    public String OtpMail(String email, String otp);
    public String resetPasswordEmail(String email, String otp);
    //public String appointmentConfirmationEmail(String email, String appointmentDetails);
    //public String appointmentCancellationEmail(String email, String appointmentDetails);
    public String forgotPasswordEmail(String email, String otp);
    public String welcomeEmail(String email, String userName);
}
