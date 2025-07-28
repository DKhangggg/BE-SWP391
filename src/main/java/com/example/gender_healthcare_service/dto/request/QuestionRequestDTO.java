package com.example.gender_healthcare_service.dto.request;

import lombok.Data;

@Data
public class QuestionRequestDTO {
    private String title;
    private String content;
    private Integer userId;
    private String category;
    private Boolean isAnonymous;
}
