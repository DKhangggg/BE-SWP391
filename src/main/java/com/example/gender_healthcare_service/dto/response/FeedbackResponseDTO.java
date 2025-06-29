package com.example.gender_healthcare_service.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeedbackResponseDTO {
    private Integer id;
    private String comment;
    private Integer rating;
    private Integer userId;
    private Integer consultantId;
    private String createdAt;
}
