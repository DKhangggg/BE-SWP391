package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.Instant;

@Data
public class ConsultationHistoryDTO {
    private Integer id;
    private Instant consultationDate;
    private String status;
    private String notes;
    private UserResponseDTO patient;
}
