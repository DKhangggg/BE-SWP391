package com.example.gender_healthcare_service.mapper;

import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.entity.BlogCategory;
import com.example.gender_healthcare_service.entity.BlogPost;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class BlogMapper {

    public BlogPostResponseDTO toResponseDTO(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }

        BlogPostResponseDTO dto = new BlogPostResponseDTO();
        dto.setPostID(blogPost.getPostID());
        dto.setTitle(blogPost.getTitle());
        dto.setContent(blogPost.getContent());
        dto.setSlug(blogPost.getSlug());
        dto.setSummary(blogPost.getSummary());
        dto.setCoverImageUrl(blogPost.getCoverImageUrl());
        dto.setTags(blogPost.getTags());
        dto.setViews(blogPost.getViews());
        dto.setLikes(blogPost.getLikes());
        dto.setCommentsCount(blogPost.getCommentsCount());
        dto.setIsPublished(blogPost.getIsPublished());
        dto.setCreatedAt(blogPost.getCreatedAt());
        dto.setUpdatedAt(blogPost.getUpdatedAt());

        // Map author
        if (blogPost.getAuthor() != null) {
            BlogPostResponseDTO.AuthorDTO authorDTO = new BlogPostResponseDTO.AuthorDTO();
            authorDTO.setId(blogPost.getAuthor().getId());
            authorDTO.setFullName(blogPost.getAuthor().getFullName());
            authorDTO.setUsername(blogPost.getAuthor().getUsername());
            authorDTO.setAvatarUrl(blogPost.getAuthor().getAvatarUrl());
            dto.setAuthor(authorDTO);
        }

        // Map categories
        if (blogPost.getCategories() != null) {
            List<BlogPostResponseDTO.CategoryDTO> categoryDTOs = blogPost.getCategories().stream().map(cat -> {
                BlogPostResponseDTO.CategoryDTO catDTO = new BlogPostResponseDTO.CategoryDTO();
                catDTO.setCategoryID(cat.getCategoryID());
                catDTO.setCategoryName(cat.getCategoryName());
                catDTO.setSlug(cat.getSlug());
                catDTO.setThumbnailUrl(cat.getThumbnailUrl());
                return catDTO;
            }).collect(Collectors.toList());
            dto.setCategories(categoryDTOs);
        }

        // isLikedByCurrentUser sẽ được set ở service nếu cần
        return dto;
    }
}
