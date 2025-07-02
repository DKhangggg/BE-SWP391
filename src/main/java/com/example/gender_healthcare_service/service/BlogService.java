package com.example.gender_healthcare_service.service;

import com.example.gender_healthcare_service.dto.request.BlogPostRequestDTO;
import com.example.gender_healthcare_service.dto.response.BlogPostResponseDTO;
import com.example.gender_healthcare_service.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface BlogService {
    Page<BlogPostResponseDTO> getBlogPosts(Pageable pageable);

    BlogPost getBlogPostById(Integer postId) throws Exception;

    boolean createBlogPost(BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) throws Exception;

    BlogPost updateBlogPost(Integer postId, BlogPostRequestDTO blogPostRequestDTO, Authentication authentication) throws Exception;

    boolean deleteBlogPost(Integer postId) throws Exception;

    Page<BlogPost> getBlogPostsByCategory(Integer categoryId, Pageable pageable) throws Exception;

    Page<BlogPost> searchBlogPosts(String keyword, Pageable pageable);

    Page<BlogPost> getFeaturedBlogPosts(Pageable pageable);

    Page<BlogPost> getBlogPostsByAuthor(Integer userId, Pageable pageable) throws Exception;


}
