package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.HashSet;
import java.util.Set;

@Data
public class BlogPostRequestDTO {


    private String title;

    private String content;

    private Set<Integer> categoryIds = new HashSet<>();


}
