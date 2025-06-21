package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserResponseDTO {
    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String roleName;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
    private String medicalHistory;
    private String gender;
    private String description;

}
