package com.example.gender_healthcare_service.mapper;

import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class BlogMapper {

    public BlogPostResponseDTO toResponseDTO(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }

        BlogPostResponseDTO dto = new BlogPostResponseDTO();
        dto.setId(blogPost.getPostID());
        dto.setTitle(blogPost.getTitle());
        dto.setContent(blogPost.getContent());

        if (blogPost.getCategories() != null) {
            dto.setCategoryIds(
                blogPost.getCategories().stream()
                    .map(BlogCategory::getCategoryID)
                    .collect(Collectors.toSet())
            );
        } else {
            dto.setCategoryIds(new HashSet<>());
        }

        if (blogPost.getAuthor() != null) {
            dto.setAuthorName(blogPost.getAuthor().getFullName());
            dto.setAuthorId(blogPost.getAuthor().getId());
        }

        dto.setCreatedAt(blogPost.getCreatedAt());
        dto.setUpdatedAt(blogPost.getUpdatedAt());

        return dto;
    }
}
