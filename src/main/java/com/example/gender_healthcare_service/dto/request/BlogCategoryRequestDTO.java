package com.example.gender_healthcare_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for Blog Category requests
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogCategoryRequestDTO {

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 100, message = "Tên danh mục không được vượt quá 100 ký tự")
    private String categoryName;

    @Size(max = 100, message = "Slug không được vượt quá 100 ký tự")
    private String slug;

    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    private String description;

    @Size(max = 500, message = "URL thumbnail không được vượt quá 500 ký tự")
    private String thumbnailUrl;
}
