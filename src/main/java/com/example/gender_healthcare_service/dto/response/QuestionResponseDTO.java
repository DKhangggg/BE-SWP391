package com.example.gender_healthcare_service.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuestionResponseDTO {
    private Integer id;
    private Integer userId;
    private String category;
    private String content;
    private String status;
    private boolean isPublic;
    private boolean isAnswered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UserResponseDTO user;
    private List<AnswerResponseDTO> answers;
}
