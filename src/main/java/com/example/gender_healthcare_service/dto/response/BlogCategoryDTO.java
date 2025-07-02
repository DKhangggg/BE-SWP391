package com.example.gender_healthcare_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simplified DTO for BlogCategory used in responses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryDTO {
    private Integer categoryID;
    private String categoryName;
    private String description;
}
