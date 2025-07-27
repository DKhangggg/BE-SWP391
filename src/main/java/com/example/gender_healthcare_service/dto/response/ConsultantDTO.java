package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class ConsultantDTO {

    private Integer id;
    private String username;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String gender;
    private Date birthDate;
    private String address;
    private String biography;
    private String qualifications;
    private Integer experienceYears;
    private String specialization;
    private String profileImageUrl; // Field này map với ProfileImageUrl trong database

}
