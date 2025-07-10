package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostMinimalDTO {
    private Integer id;
    private String title;
    private LocalDateTime createdAt;
}
