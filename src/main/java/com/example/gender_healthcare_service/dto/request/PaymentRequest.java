package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long bookingId;
    private Double amount;
    private String paymentMethod;
    private String currency;
    private String description;
}
