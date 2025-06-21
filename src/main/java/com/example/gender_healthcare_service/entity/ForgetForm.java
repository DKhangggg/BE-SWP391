package com.example.gender_healthcare_service.entity;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ForgetForm {
    private String email;
    private String otp;

    public ForgetForm(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

}
