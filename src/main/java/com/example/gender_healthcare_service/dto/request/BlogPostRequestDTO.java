package com.example.gender_healthcare_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostRequestDTO {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được quá 200 ký tự")
    private String title;
    
    private String slug;
    
    @Size(max = 500, message = "Tóm tắt không được quá 500 ký tự")
    private String summary;
    
    @NotBlank(message = "Nội dung không được để trống")
    private String content;
    
    private String coverImageUrl;
    
    private String tags; // JSON string of tags
    
    private Set<Integer> categoryIds = new HashSet<>();
    
    private Boolean isPublished = false;
}
