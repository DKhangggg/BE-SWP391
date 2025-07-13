package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

@Data
public class LocationResponseDTO {
    private Integer id;
    private String name;
    private String address;
    private String phone;
    private String hours;
    private String status;
} 