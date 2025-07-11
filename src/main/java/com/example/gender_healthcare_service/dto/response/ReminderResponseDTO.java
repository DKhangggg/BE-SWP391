package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponseDTO {
    private Integer id;
    private String reminderType;
    private String message;
    private LocalDateTime reminderDate;
    private Boolean isSent;
    private LocalDateTime createdAt;
    private Integer userId;
    private String username;
}
