package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnswerResponseDTO {
    private Integer id;
    private Integer questionId;
    private String content;
    private Integer consultantId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ConsultantDTO consultant;
}
