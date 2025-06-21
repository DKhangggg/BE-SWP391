package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TestingServiceResponseDTO {
    private Long serviceId;
    private String serviceName;
    private String description;
    private BigDecimal price;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}

