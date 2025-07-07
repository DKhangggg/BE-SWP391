package com.example.gender_healthcare_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "password_reset_otp")
@Data
@NoArgsConstructor
public class PasswordResetOTP {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String otpCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDate expiryDate;

    public PasswordResetOTP(String otpCode, User user) {
        this.otpCode = otpCode;
        this.user = user;
        this.expiryDate = LocalDate.now().plusDays(1);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(this.expiryDate);
    }
}
