package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class StaffRequestDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String status;
}
