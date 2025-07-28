package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String accessToken;
    private String refreshToken;
    private Integer id;
    private String username;
    private String role;
    private String email;
    private String fullName;
    private String avatarUrl;

    public AuthResponseDTO(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public AuthResponseDTO(String accessToken, String refreshToken, Integer id, String username, String role, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
    }

    public AuthResponseDTO(String accessToken, String refreshToken, Integer id, String username, String role, String email, String fullName, String avatarUrl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.id = id;
        this.username = username;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }
}
