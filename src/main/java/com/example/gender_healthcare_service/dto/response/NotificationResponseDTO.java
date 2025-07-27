package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private boolean isRead;
    private String link;
    private LocalDateTime createdAt;
    private String type; // CONSULTATION, TEST_RESULT, CYCLE_REMINDER, QUESTION_ANSWERED
    private String title;
    private String description;
} 