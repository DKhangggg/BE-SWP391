package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class CreateNewConsultantRequest {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phoneNumber;
    private String dateOfBirth;
    private String address;
    private String gender;
    private String medicalHistory;


    private String biography;
    private String qualifications;
    private Integer experienceYears;
    private String specialization;

}
