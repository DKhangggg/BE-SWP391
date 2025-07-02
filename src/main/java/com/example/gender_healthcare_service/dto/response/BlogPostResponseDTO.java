package com.example.gender_healthcare_service.dto.response;
import lombok.Data;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class BlogPostResponseDTO {

    private Integer id;
    private String title;
    private String content;
    private String thumbnail;
    private Set<Integer> categoryIds = new HashSet<>();
    private String authorName;
    private Integer authorId;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
