package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileRequest {
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    @Past
    private LocalDate dateOfBirth;
    private String address;
    private String gender;
    private String medicalHistory;
    private String createdAt;
    private String updatedAt;
}
