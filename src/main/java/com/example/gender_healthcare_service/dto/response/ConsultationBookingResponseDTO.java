package com.example.gender_healthcare_service.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConsultationBookingResponseDTO {
    private Integer id;
    private Integer consultantId;
    private String consultantName;
    private Integer userId;
    private String userName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String consultationType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String notes;
}
