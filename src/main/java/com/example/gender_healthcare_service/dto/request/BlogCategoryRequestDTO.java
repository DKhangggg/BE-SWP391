package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


/**
 * Data Transfer Object for Blog Category requests
 */
@Data

public class BlogCategoryRequestDTO {
    private String name;
    private String description;
    private Set<Integer> categoryIds;
}
