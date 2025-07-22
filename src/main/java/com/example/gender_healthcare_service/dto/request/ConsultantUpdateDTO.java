package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class ConsultantUpdateDTO {
    private Integer id;
    // User fields
    private String fullName;
    private String email;
    private String phoneNumber;
    private String gender;
    private String birthDate;
    private String address;
    // Consultant fields
    private String biography;
    private String qualifications;
    private Integer experienceYears;
    private String specialization;
}

