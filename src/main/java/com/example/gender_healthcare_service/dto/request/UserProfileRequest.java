package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String address;
    private String gender;
    private String medicalHistory;
    private String createdAt;
    private String updatedAt;
}
