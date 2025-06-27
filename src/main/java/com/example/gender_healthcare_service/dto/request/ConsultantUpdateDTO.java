package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class ConsultantUpdateDTO {
    private Integer id;
    private String biography;
    private String qualifications;
    private Integer experienceYears;
    private String specialization;
    // Add more fields as needed for updating consultant details
}

