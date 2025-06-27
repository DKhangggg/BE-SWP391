package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderRequestDTO {
    private Integer userId;
    private String reminderType;
    private String message;
    private Instant reminderDate;
}
