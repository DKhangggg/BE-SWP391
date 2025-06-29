package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDate;
@Data
public class AnswerResponseDTO {
    private Integer id;
    private Integer questionId;
    private String content;
    private Integer consultantId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
