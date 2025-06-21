package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUpdateUserRequestDTO {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String gender;
    private String dateOfBirth;
    private String roleName;
    private Boolean isDeleted;
}

