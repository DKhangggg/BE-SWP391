package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class ResetPasswordRequestDTO {
    private String token;
    private String newPassword;
}

