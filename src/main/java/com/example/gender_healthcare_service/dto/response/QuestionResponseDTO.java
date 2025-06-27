package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class QuestionResponseDTO {
    private Integer questionId;
    private  Integer userId;
    private String category;
    private String content;
    private String status;
    private boolean isPublic;
    private LocalDate createdAt;
    private  LocalDate updatedAt;
}
