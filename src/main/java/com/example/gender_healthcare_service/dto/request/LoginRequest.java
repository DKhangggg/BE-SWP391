package com.example.gender_healthcare_service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}