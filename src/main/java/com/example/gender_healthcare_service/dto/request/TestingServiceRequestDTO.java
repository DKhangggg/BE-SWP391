package com.example.gender_healthcare_service.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TestingServiceRequestDTO {
    private String serviceName;
    private String description;
    private BigDecimal price;
    private String status;
}

