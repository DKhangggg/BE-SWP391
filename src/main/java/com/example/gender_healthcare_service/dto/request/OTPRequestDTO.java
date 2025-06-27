package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class OTPRequestDTO {
    private String email;
    private String otpCode;
    private String newPassword;
}
