package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponseDTO {
    private Integer id;
    private String reminderType;
    private String message;
    private Instant reminderDate;
    private Boolean isSent;
    private Instant createdAt;
    private Integer userId;
    private String username;
}
