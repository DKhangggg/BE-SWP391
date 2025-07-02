package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho BlogCategory có chứa danh sách các bài post tối giản
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryWithPostsDTO {
    private Integer categoryID;
    private String categoryName;
    private String description;
    private List<BlogPostMinimalDTO> posts;
}

